package com.qihoo360.replugin.loader.a;

import android.content.Context;
import android.content.res.Resources;
import com.qihoo360.replugin.RePlugin;

/**
 * 创建时间：2018/07/13/14:22
 * 作者：xiejiangquan <br>
 * 描述：
 */
public class IActivityProxy {

    public static int getHostAnimResId(int anim) {
        final Resources resources = RePlugin.getPluginContext().getResources();
        int animHost = anim;
        if (anim != 0) {
            String animStr = resources.getResourceEntryName(anim);
            animHost = getHostResId("anim", animStr);
        }
        return animHost;
    }

    /**
     * 获取主工程的资源id：针对某些系统动画的情况下，需要将资源放到主工程获取
     *
     * @param defType  资源类型
     * @param animStr  资源id字符名
     * @return
     */
    public static int getHostResId(String defType, String animStr) {
        final Context hostContext = RePlugin.getHostContext();
        int resId = hostContext.getResources().getIdentifier(animStr, defType , RePlugin.getPluginContext().getPackageName());
        return resId;
    }
}
