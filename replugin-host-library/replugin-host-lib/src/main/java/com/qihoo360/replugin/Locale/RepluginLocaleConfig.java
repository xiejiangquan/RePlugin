package com.qihoo360.replugin.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import com.qihoo360.mobilesafe.api.IPC;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * 创建时间：2018/09/03/17:29
 * 作者：xiejiangquan <br>
 * 描述：
 */
public class RepluginLocaleConfig {

    public static final String ACTION_REPLUGIN_LOCALE_CHANGE = "action_replugin_locale_change";

    public static final String TAG_REPLUGIN_LOCALE_EXTRA = "tag_replugin_locale_extra";

    private static ILocaleDependency sLocaleDependency;

    private final static List<WeakReference<ILocaleListener>> mLocaleListener = new ArrayList<>();

    public static ILocaleDependency getLocaleDependency() {
        return sLocaleDependency;
    }

    public static void setLocaleDependency(Context context, ILocaleDependency localeDependency) {
        sLocaleDependency = localeDependency;
        LocalBroadcastManager.getInstance(context).registerReceiver(new LocaleChangeReceiver(), new IntentFilter(ACTION_REPLUGIN_LOCALE_CHANGE));
    }

    public static void addLocaleListener(ILocaleListener localeListener) {
        if (null == localeListener) {
            return;
        }
        mLocaleListener.add(new WeakReference<>(localeListener));
    }

    public static void onLocaleChange(Locale locale) {
        Intent intent = new Intent();
        intent.setAction(ACTION_REPLUGIN_LOCALE_CHANGE);
        intent.putExtra(TAG_REPLUGIN_LOCALE_EXTRA, locale);
        IPC.sendLocalBroadcast2All(null, intent);
    }

    private static void onLocaleChangeInternal(Locale locale) {
        List<WeakReference<ILocaleListener>> copy = new ArrayList<>(mLocaleListener);
        for (Iterator<WeakReference<ILocaleListener>> iterator = copy.iterator();
            iterator.hasNext(); ) {
            ILocaleListener localeListener = iterator.next().get();
            if (null == localeListener) {
                iterator.remove();
            } else {
                localeListener.onLocaleChange(locale);
            }
        }
    }


    public interface ILocaleDependency {
        /**
         * 获取当前Locale设置
         */
        Locale getLocale();
    }

    public interface ILocaleListener {
        /**
         * 当语言环境改变调用
         */
        void onLocaleChange(Locale locale);
    }

    public static class LocaleChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent) {
                return;
            }
            if (ACTION_REPLUGIN_LOCALE_CHANGE.equals(intent.getAction())) {
                Locale locale = (Locale) intent.getSerializableExtra(TAG_REPLUGIN_LOCALE_EXTRA);
                if (null != locale) {
                    onLocaleChangeInternal(locale);
                }
            }
        }
    }
}
