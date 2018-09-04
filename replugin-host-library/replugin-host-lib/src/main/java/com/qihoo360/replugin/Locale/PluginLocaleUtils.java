package com.qihoo360.replugin.Locale;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;
import java.util.Locale;

/**
 * @author RePlugin Team
 */
public class PluginLocaleUtils {

    /**
     * 当语言环境改变调用
     * @param context
     * @param locale
     */
    public static void onLocaleChange(Context context, Locale locale) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale);
            configuration.setLocales(new LocaleList(locale));
        } else {
            configuration.locale = locale;
        }
        resources.updateConfiguration(configuration, metrics);
    }


    /**
     * 改变context语言状态
     * @param context 环境上下文
     * @param locale 语言
     * @return
     */
    public static Context updateContextForLocale(Context context, Locale locale, Resources resources) {
        Configuration config = resources.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
            config.setLocales(new LocaleList(locale));
            context = context.createConfigurationContext(config);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        } else {
            config.locale = locale;
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }
        return context;
    }
}
