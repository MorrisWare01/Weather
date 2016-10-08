package com.win.weather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.win.weather.bean.CityManageBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by idea on 2016-10-03.
 */
public class CityManageDao {
    private CityManageOpenHelper helper;
    private String TABLE_NAME = "city";

    public CityManageDao(Context context) {
        helper = new CityManageOpenHelper(context);
    }

    public boolean insert(CityManageBean bean) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", bean.getName());
        values.put("city", bean.getCity());
        values.put("isDefault", bean.isDefault());
        values.put("temp", bean.getTemp());
        values.put("resId", bean.getResId());
        values.put("position", bean.getPosition());
        long res = db.insert(TABLE_NAME, null, values);
        db.close();
        return res != -1;
    }

    public boolean delete(String name) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int res = db.delete(TABLE_NAME, "name=?", new String[]{name});
        db.close();
        return res != -1;
    }

    public boolean update(CityManageBean bean) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", bean.getName());
        values.put("city", bean.getCity());
        values.put("isDefault", bean.isDefault());
        values.put("temp", bean.getTemp());
        values.put("resId", bean.getResId());
        values.put("position", bean.getPosition());
        int res = db.update(TABLE_NAME, values, "name=?", new String[]{bean.getName()});
        db.close();
        return res != -1;
    }

    public CityManageBean find(String name) {
        CityManageBean bean = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "name=?", new String[]{name}, null, null, null);
        if (cursor.moveToNext()) {
            String city = cursor.getString(cursor.getColumnIndex("city"));
            String temp = cursor.getString(cursor.getColumnIndex("temp"));
            boolean isDefault = cursor.getInt(cursor.getColumnIndex("isDefault")) == 1;
            int position = cursor.getInt(cursor.getColumnIndex("position"));
            int resId = cursor.getInt(cursor.getColumnIndex("resId"));
            bean = new CityManageBean(name, city, temp, resId, position, isDefault);
        }
        cursor.close();
        db.close();
        return bean;
    }

    public CityManageBean findLocation() {
        CityManageBean bean = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "name=?", new String[]{"自动定位"}, null, null, null);
        if (cursor.moveToNext()) {
            String city = cursor.getString(cursor.getColumnIndex("city"));
            String temp = cursor.getString(cursor.getColumnIndex("temp"));
            boolean isDefault = cursor.getInt(cursor.getColumnIndex("isDefault")) == 1;
            int resId = cursor.getInt(cursor.getColumnIndex("resId"));
            int position = cursor.getInt(cursor.getColumnIndex("position"));
            bean = new CityManageBean("自动定位", city, temp, resId, position, isDefault);
        }
        cursor.close();
        db.close();
        return bean;
    }

    public List<CityManageBean> findAll() {
        List<CityManageBean> mList = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String city = cursor.getString(cursor.getColumnIndex("city"));
            String temp = cursor.getString(cursor.getColumnIndex("temp"));
            boolean isDefault = cursor.getInt(cursor.getColumnIndex("isDefault")) == 1;
            int position = cursor.getInt(cursor.getColumnIndex("position"));
            int resId = cursor.getInt(cursor.getColumnIndex("resId"));
            mList.add(new CityManageBean(name, city, temp, resId, position, isDefault));
        }
        cursor.close();
        db.close();
        return mList;
    }

}
