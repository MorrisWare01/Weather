package com.win.weather.bean;

/**
 * Created by idea on 2016-09-30.
 */
public class LifeIndexBean {
    private String short_name;
    private String long_name;

    public LifeIndexBean(String short_name, String long_name) {
        this.short_name = short_name;
        this.long_name = long_name;
    }

    public LifeIndexBean() {
    }

    @Override
    public String toString() {
        return "LifeIndexBean{" +
                "short_name='" + short_name + '\'' +
                ", long_name='" + long_name + '\'' +
                '}';
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public String getLong_name() {
        return long_name;
    }

    public void setLong_name(String long_name) {
        this.long_name = long_name;
    }
}
