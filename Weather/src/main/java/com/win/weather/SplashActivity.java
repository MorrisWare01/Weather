package com.win.weather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.win.weather.bean.CityManageBean;
import com.win.weather.db.CityManageDao;
import com.win.weather.utils.AMapLocationUtils;
import com.win.weather.utils.DBUtils;
import com.win.weather.utils.NetworkUtils;
import com.win.weather.utils.PrefUtils;

public class SplashActivity extends AppCompatActivity {
    private AMapLocationUtils locationUtils;
    private CityManageDao dao;
    private boolean init;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        dao = ((BaseApplication) getApplication()).cityManageDao;
        init = PrefUtils.getBoolean(this, "init", false);
        DBUtils.copyDB(this, "cities.db");
        if (NetworkUtils.isNetworkConnected(this)) {
            initLocation();
        } else {
            onFail();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationUtils != null)
            locationUtils.recycle();
    }

    private void initLocation() {
        locationUtils = new AMapLocationUtils(this);
        locationUtils.setLocationListener(new MyLocationListener());
        locationUtils.startLocation();
    }

    private void onSuccess(String city) {
        if (init) {
            if (dao.findAll().size() != 0) {
                CityManageBean bean = dao.findLocation();
                if (bean != null && !bean.getCity().equals(city)) {
                    bean.setCity(city);
                    dao.update(bean);
                }
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(SplashActivity.this, CityManageActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            PrefUtils.putBoolean(this, "init", true);
            dao.insert(new CityManageBean(city, 0, true));
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void onFail() {
        if (init) {
            if (dao.findAll().size() != 0) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(SplashActivity.this, CityManageActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            PrefUtils.putBoolean(this, "init", true);
            Intent intent = new Intent(SplashActivity.this, CityManageActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public class MyLocationListener implements AMapLocationListener {

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    String city = aMapLocation.getCity().substring(0, aMapLocation.getCity().lastIndexOf("市"));
                    onSuccess(city);
                } else {
                    onFail();
                }
            } else {
                onFail();
            }
        }
    }
}
