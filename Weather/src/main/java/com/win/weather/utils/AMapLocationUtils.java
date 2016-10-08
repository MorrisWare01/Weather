package com.win.weather.utils;

import android.content.Context;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * Created by idea on 2016-10-03.
 */
public class AMapLocationUtils {
    private AMapLocationClient mLocationClient = null;

    public AMapLocationUtils(Context context) {
        mLocationClient = new AMapLocationClient(context.getApplicationContext());
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        mLocationOption.setOnceLocation(true);
        mLocationOption.setOnceLocationLatest(true);
        mLocationOption.setNeedAddress(true);
        mLocationClient.setLocationOption(mLocationOption);
    }

    public void setLocationListener(AMapLocationListener listener) {
        mLocationClient.setLocationListener(listener);
    }

    public void startLocation() {
        mLocationClient.startLocation();
    }

    public void recycle() {
        mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
    }

    public String formatCity(String addr) {
        String city = null;
        if (addr.contains("北京市") && addr.contains("区")) {
            city = addr.substring(addr.indexOf("市") + 1, addr.indexOf("区"));
        } else if (addr.contains("县")) {
            city = addr.substring(addr.indexOf("市") + 1, addr.indexOf("县"));
        } else {
            int start = addr.indexOf("市");
            int end = addr.lastIndexOf("市");
            if (start == end) {
                if (addr.contains("省")) {
                    city = addr.substring(addr.indexOf("省") + 1,
                            addr.indexOf("市"));
                } else if (addr.contains("市")) {
                    city = addr.substring(0, addr.indexOf("市"));
                }
            } else {
                city = addr.substring(addr.indexOf("市") + 1,
                        addr.lastIndexOf("市"));
            }
        }
        return city;
    }
}
