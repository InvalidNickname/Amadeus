package com.example.yink.amadeus;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.preference.PreferenceManager;

import java.util.Locale;

public class LangContext extends ContextWrapper {
    public LangContext(Context base) {
        super(base);
    }

    public static ContextWrapper wrap(Context context) {
        Configuration config = context.getResources().getConfiguration();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

        String lang = settings.getString("lang", "en");
        String[] langArr = lang.split("-");
        Locale locale;
        switch (langArr.length) {
            case 3:
                locale = new Locale(langArr[0], langArr[1], langArr[2]);
                break;
            case 2:
                locale = new Locale(langArr[0], langArr[1]);
                break;
            default:
                locale = new Locale(langArr[0]);
                break;
        }

        Locale.setDefault(locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setSystemLocale(config, locale);
        } else {
            setSystemLocaleLegacy(config, locale);
        }

        context = context.createConfigurationContext(config);
        return new LangContext(context);
    }

    public static Context load(Context context, String lang) {
        Configuration config = context.getResources().getConfiguration();

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setSystemLocale(config, locale);
        } else {
            setSystemLocaleLegacy(config, locale);
        }

        context = context.createConfigurationContext(config);

        return context;
    }

    private static void setSystemLocaleLegacy(Configuration config, Locale locale) {
        config.locale = locale;
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static void setSystemLocale(Configuration config, Locale locale) {
        config.setLocale(locale);
    }
}
