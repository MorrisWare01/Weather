package com.win.weather.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by idea on 2016-10-03.
 */
public class DBUtils {

    public static void copyDB(Context context,String dbName){
        File destFile = new File(context.getFilesDir(), dbName);
        if (destFile.exists()) {
            return;
        }
        InputStream is = null;
        OutputStream os = null;
        try {
            is = context.getAssets().open("cities.db");
            os = new FileOutputStream(destFile);
            byte[] buff = new byte[1024];
            int len;
            while ((len = is.read(buff)) != -1) {
                os.write(buff, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
