package com.reddit.demo.utils;

import android.content.Context;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.Date;

public class CommonTools {

    public static String prettyNumber(int n){
        return prettyNumber(n, 0);
    }

    private static String prettyNumber(int n, int iteration){
        if(n<1000)
            return String.valueOf(n);
        char[] c = new char[]{'k', 'm', 'b', 't'};
        double d = ((long) n / 100) / 10.0;
        boolean isRound = (d * 10) %10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (d < 1000? //this determines the class, i.e. 'k', 'm' etc
                ((d > 99.9 || isRound || (!isRound && d > 9.99)? //this decides whether to trim the decimals
                        (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
                ) + "" + c[iteration])
                : prettyNumber((int) d, iteration+1));
    }

    public static String prettyTime(int timeStamp){
        Date time = new Date((long)timeStamp*1000);
        return (String) DateFormat.format("yyyy, MMM dd HH:mm", time);
    }

    public static String userAvatarlink(String id){
        return "https://www.redditstatic.com/avatars/avatar_default_"+id+".png";
    }

    public static int getAppTheme(Context context){
        if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("dark_mode",false)) {
            return AppCompatDelegate.MODE_NIGHT_YES;
        }
        switch(PreferenceManager.getDefaultSharedPreferences(context).getString("app_theme","SD")){
            case "LM":
                return AppCompatDelegate.MODE_NIGHT_NO;
            case "NM":
                return AppCompatDelegate.MODE_NIGHT_YES;
            case "AT":
            default:
                return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
    }
}
