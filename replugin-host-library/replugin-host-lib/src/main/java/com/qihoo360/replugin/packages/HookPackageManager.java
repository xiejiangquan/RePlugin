package com.qihoo360.replugin.packages;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 创建时间：2018/08/05/11:58
 * 作者：xiejiangquan <br>
 * 描述：解决glide 通过调用
 *         ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
 *         得不到NameNotFoundException错误
 */
public class HookPackageManager {

    private static final String TAG = "HookPackageManager";

    public static void hookPackageManager(Context hostContext) {

        final String hostPackageName = hostContext.getPackageName();

        try {
            // 获取全局的ActivityThread对象
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
            Object currentActivityThread = currentActivityThreadMethod.invoke(null);

            // 获取ActivityThread里面原始的 sPackageManager
            Field sPackageManagerField = activityThreadClass.getDeclaredField("sPackageManager");
            sPackageManagerField.setAccessible(true);
            Object sPackageManager = sPackageManagerField.get(currentActivityThread);

            // 准备好代理对象, 用来替换原始的对象
            Class<?> iPackageManagerInterface = Class.forName("android.content.pm.IPackageManager");
            Object proxy = Proxy.newProxyInstance(iPackageManagerInterface.getClassLoader(),
                new Class<?>[] { iPackageManagerInterface },
                new HookHandler(sPackageManager, hostPackageName));

            // 1. 替换掉ActivityThread里面的 sPackageManager 字段
            sPackageManagerField.set(currentActivityThread, proxy);

            // 2. 替换 ApplicationPackageManager里面的 mPM对象
            PackageManager pm = hostContext.getPackageManager();
            Field mPmField = pm.getClass().getDeclaredField("mPM");
            mPmField.setAccessible(true);
            mPmField.set(pm, proxy);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class HookHandler implements InvocationHandler {

        Object oldPM;

        String hostPackageName;

        public HookHandler(Object oldPM, String hostPackageName) {
            this.oldPM = oldPM;
            this.hostPackageName = hostPackageName;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("getApplicationInfo")) {
                Object result = method.invoke(oldPM, args);
                if (null != result) {
                    return result;
                }
                Log.i(TAG, "getApplicationInfo error, appName : " + args[0] + ", try replace with hostPackageName :" + hostPackageName);
                args[0] = hostPackageName;
                return method.invoke(oldPM, args);
            }
            return method.invoke(oldPM, args);
        }
    }
}
