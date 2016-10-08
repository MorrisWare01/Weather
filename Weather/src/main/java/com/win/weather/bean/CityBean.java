package com.win.weather.bean;

import java.io.Serializable;

/**
 * Created by idea on 2016-09-29.
 */
public class CityBean implements Serializable {
    private String code;
    private String name;
    private String province;
    private String longitude;
    private String latitude;

    public CityBean(String name) {
        this.name = name;
    }

    public CityBean(String code, String name, String province, String longitude, String latitude) {
        this.code = code;
        this.name = name;
        this.province = province;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "CityBean{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", province='" + province + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                '}';
    }
}
