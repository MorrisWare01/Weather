package com.win.weather.bean;

import java.io.Serializable;

/**
 * Created by idea on 2016-09-27.
 */
public class CityManageBean implements Serializable, Comparable<CityManageBean> {
    private String name;
    private String city;
    private String temp;
    private int resId;
    private int position;
    private boolean isDefault;

    public CityManageBean() {
    }

    public CityManageBean(String name, String city) {
        this.name = name;
        this.city = city;
    }

    public CityManageBean(String city, int position, boolean isDefault) {
        this.name = "自动定位";
        this.city = city;
        this.position = position;
        this.isDefault = isDefault;
    }

    public CityManageBean(String name, String city, String temp, int resId, int position, boolean isDefault) {
        this.name = name;
        this.city = city;
        this.temp = temp;
        this.resId = resId;
        this.position = position;
        this.isDefault = isDefault;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }


    @Override
    public String toString() {
        return "CityManageBean{" +
                "name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", temp='" + temp + '\'' +
                ", resId=" + resId +
                ", position=" + position +
                ", isDefault=" + isDefault +
                '}';
    }

    @Override
    public int compareTo(CityManageBean another) {
        return position >= another.getPosition() ? 1 : -1;
    }
}
