package com.win.weather.utils;

import android.content.Context;

public class CacheUtils {

    public static void putCache(Context ctx, String key, String value) {
        PrefUtils.putString(ctx, key, value);
    }

    public static String getCache(Context ctx, String key) {
        return PrefUtils.getString(ctx, key, null);
    }
}
