package com.win.weather.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by idea on 2016-10-05.
 */
public class MyDateUtils {
    public static String getWeekOfDate(Date date) {
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;

        return weekDays[w];
    }
}
