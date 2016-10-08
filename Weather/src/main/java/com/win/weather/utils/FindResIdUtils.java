package com.win.weather.utils;

import android.content.Context;

/**
 * Created by idea on 2016-09-08.
 */
public class FindResIdUtils {

    /**
     * 根据字符串名字找到对应Drawable目录下相同名字的资源文件ID
     * 例：name=d100 ------- 返回R.drawable.d100;
     * @param context
     * @param name
     * @return
     */
    public static int findDrawableIdByString(Context context, String name) {
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }


}
