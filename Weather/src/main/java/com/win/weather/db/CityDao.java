package com.win.weather.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.win.weather.bean.CityBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by idea on 2016-09-29.
 */
public class CityDao {
    private static final String filePath = "/data/data/com.win.dragrecyclerview/files/cities.db";

    public static List<CityBean> queryCity(String key) {
        List<CityBean> mList = new ArrayList<>();
        String sql = "select * from city where name like '%" + key + "%' or province like '%" + key + "%'" +
                " or shortpinyin like '%," + key + "%' or fullpinyin like '%," + key + "%'";
        SQLiteDatabase database = SQLiteDatabase.openDatabase(filePath, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = database.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String code = cursor.getString(cursor.getColumnIndex("code"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String province = cursor.getString(cursor.getColumnIndex("province"));
            String longitude = cursor.getString(cursor.getColumnIndex("longtitude"));
            String latitude = cursor.getString(cursor.getColumnIndex("latitude"));
            mList.add(new CityBean(code, name, province, longitude, latitude));
        }
        cursor.close();
        return mList;
    }

    public static List<CityBean> queryHotCity() {
        List<CityBean> mList = new ArrayList<>();
        mList.add(new CityBean("自动定位"));
        SQLiteDatabase database = SQLiteDatabase.openDatabase(filePath, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = database.rawQuery("select * from hotcity", null);
        while (cursor.moveToNext()) {
            String code = cursor.getString(cursor.getColumnIndex("code"));
            Cursor cursor1 = database.rawQuery("select * from city where code = ?", new String[]{code});
            while (cursor1.moveToNext()) {
                code = cursor1.getString(cursor1.getColumnIndex("code"));
                String name = cursor1.getString(cursor1.getColumnIndex("name"));
                String province = cursor1.getString(cursor1.getColumnIndex("province"));
                String longitude = cursor1.getString(cursor1.getColumnIndex("longtitude"));
                String latitude = cursor1.getString(cursor1.getColumnIndex("latitude"));
                mList.add(new CityBean(code, name, province, longitude, latitude));
            }
            cursor1.close();
        }
        cursor.close();
        return mList;
    }
}
