package com.win.weather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by idea on 2016-10-03.
 */
public class CityManageOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "city_manage.db";
    private static final int VERSION = 1;
    private static final String CREATE_SQL = "create table if not exists city(_id integer primary key autoincrement," +
            "name varchar(20),city varchar(20),isDefault TINYINT(1),isLocation TINYINT(1)," +
            "temp varchar(20),resId integer,position integer)";

    public CityManageOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
