package com.qihoo360.replugin.loader.a;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import com.qihoo360.replugin.RePlugin;

/**
 * 创建时间：2018/07/13/14:22
 * 作者：xiejiangquan <br>
 * 描述：
 */
public class IActivityProxy {

    public static void overridePendingTransition(Activity activity, int enterAnim, int exitAnim) {
        final Resources resources = RePlugin.getPluginContext().getResources();

        int enterAnimHost = getHostResId(enterAnim, resources);

        int exitAnimHost = getHostResId(exitAnim, resources);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            activity.getParent().overridePendingTransition(enterAnimHost, exitAnimHost);
        }
    }

    private static int getHostResId(int anim, Resources resources) {
        int animHost = anim;
        if (anim != 0) {
            String animStr = resources.getResourceEntryName(anim);
            animHost = getHostResId(animStr);
        }
        return animHost;
    }

    /**
     * 获取主工程的资源id：针对某些系统动画的情况下，需要将资源放到主工程获取
     *
     * @param animStr  资源id字符名
     * @return
     */
    private static int getHostResId(String animStr) {
        final Context hostContext = RePlugin.getHostContext();
        int resId = hostContext.getResources().getIdentifier(animStr, "anim" , hostContext.getPackageName());
        return resId;
    }
}
