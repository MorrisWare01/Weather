package com.win.weather;

import android.app.Application;

import com.win.weather.db.CityManageDao;

/**
 * Created by idea on 2016-09-29.
 */
public class BaseApplication extends Application {
    public CityManageDao cityManageDao;

    @Override
    public void onCreate() {
        super.onCreate();
        cityManageDao = new CityManageDao(this);
    }


}
