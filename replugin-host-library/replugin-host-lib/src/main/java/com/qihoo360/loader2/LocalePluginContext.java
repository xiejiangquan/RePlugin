package com.qihoo360.loader2;

import android.content.Context;
import android.content.res.Resources;
import com.qihoo360.replugin.Locale.PluginLocaleUtils;
import com.qihoo360.replugin.Locale.RepluginLocaleConfig;
import java.util.Locale;

/**
 * 创建时间：2018/09/03/17:39
 * 作者：xiejiangquan <br>
 * 描述：
 */
public class LocalePluginContext extends PluginContext implements
    RepluginLocaleConfig.ILocaleListener {

    public LocalePluginContext(Context base, int themeres, ClassLoader cl, Resources r,
        String plugin, Loader loader) {
        super(null == RepluginLocaleConfig.getLocaleDependency() ? base :
            PluginLocaleUtils.updateContextForLocale(base, RepluginLocaleConfig.getLocaleDependency().getLocale(), r), themeres, cl, r, plugin, loader);
        RepluginLocaleConfig.addLocaleListener(this);
    }

    /**
     * 当语言环境改变调用
     */
    @Override
    public void onLocaleChange(Locale locale) {
        PluginLocaleUtils.onLocaleChange(this, locale);
    }
}
