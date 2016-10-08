package com.win.weather.bean;

/**
 * Created by idea on 2016-10-05.
 */
public class ForecastBean {
    private String week;
    private int drawableId;
    private String sky;
    private String temp;

    public ForecastBean(String week, int drawableId, String sky, String temp) {
        this.week = week;
        this.drawableId = drawableId;
        this.sky = sky;
        this.temp = temp;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public String getSky() {
        return sky;
    }

    public void setSky(String sky) {
        this.sky = sky;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    @Override
    public String toString() {
        return "ForecastBean{" +
                "week='" + week + '\'' +
                ", drawableId=" + drawableId +
                ", sky='" + sky + '\'' +
                ", temp='" + temp + '\'' +
                '}';
    }
}
