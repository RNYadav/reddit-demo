package com.reddit.demo.utils;

import android.content.Context;
import android.preference.PreferenceManager;

public class Config {
    public static final String SITE_PROTOCOL = "https";
    public static final String SITE_DOMAIN = "reddit.com";
    public static String DATA_LIMIT(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("data_limit","10");
//        return "10";
    }
    public static String GEO_LIMIT(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("app_lang","IN");
    }

    public static final String TAG = "IndiaForums/android";
}
