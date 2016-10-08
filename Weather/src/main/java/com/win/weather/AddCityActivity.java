package com.win.weather;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.win.weather.adapter.CityQueryAdapter;
import com.win.weather.adapter.HotCityQueryAdapter;
import com.win.weather.bean.CityBean;
import com.win.weather.bean.CityManageBean;
import com.win.weather.db.CityDao;
import com.win.weather.utils.AMapLocationUtils;
import com.win.weather.utils.NetworkUtils;

import java.util.List;

public class AddCityActivity extends AppCompatActivity implements TextWatcher {
    private Toolbar toolbar;
    private ImageView iv_back;
    private ListView lv_search;
    private EditText et_search;
    private TextView tv_null;
    private TextView tv_hot_city;
    private GridView gv_hot_city;

    private static final int NO_DATA = 1;
    private static final int HAVE_DATA = 2;
    private static final int Normal = 3;
    private List<CityBean> hotCityList;
    private List<CityBean> cityBeenList;
    private AlertDialog alertDialog;
    private AMapLocationUtils locationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);

        initToolbar();
        initView();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            iv_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private void initView() {
        tv_hot_city = (TextView) findViewById(R.id.tv_hot_city);
        lv_search = (ListView) findViewById(R.id.lv_search);
        gv_hot_city = (GridView) findViewById(R.id.gv_hot_city);
        tv_null = (TextView) findViewById(R.id.tv_null);
        et_search = (EditText) findViewById(R.id.et_search);

        hotCityList = CityDao.queryHotCity();
        gv_hot_city.setAdapter(new HotCityQueryAdapter(this, hotCityList));
        et_search.addTextChangedListener(this);
        lv_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String city = cityBeenList.get(position).getName();
                finishAdd(new CityManageBean(city, city));
            }
        });
        gv_hot_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    CityManageBean bean = ((BaseApplication) getApplication()).cityManageDao.find("自动定位");
                    if (bean != null) {
                        Toast.makeText(AddCityActivity.this, "城市“自动定位”已经添加", Toast.LENGTH_SHORT).show();
                    } else {
                        if (NetworkUtils.isNetworkConnected(AddCityActivity.this)) {
                            locationUtils = new AMapLocationUtils(AddCityActivity.this);
                            locationUtils.setLocationListener(new MyLocationListener());
                            View dialog_view = View.inflate(AddCityActivity.this, R.layout.dialog_location, null);
                            alertDialog = new AlertDialog.Builder(AddCityActivity.this).setView(dialog_view).create();
                            alertDialog.show();
                            locationUtils.startLocation();
                        } else {
                            Toast.makeText(AddCityActivity.this, "请连接互联网以查询位置对应的城市", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    String city = hotCityList.get(position).getName();
                    finishAdd(new CityManageBean(city, city));
                }
            }
        });
    }

    public class MyLocationListener implements AMapLocationListener {

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            alertDialog.cancel();
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    String city = locationUtils.formatCity(aMapLocation.getAddress());
                    CityManageBean bean = new CityManageBean();
                    bean.setName("自动定位");
                    bean.setCity(city);
                    Intent intent = new Intent();
                    intent.putExtra("city", bean);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(AddCityActivity.this, "定位失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AddCityActivity.this, "定位失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationUtils != null) {
            locationUtils.recycle();
        }
    }

    /**
     * 1.编辑框不为空，找不到数据
     * 2.编辑框不为空，找得到数据
     * 3.编辑框为空
     */
    public void changeStatus(int type) {
        switch (type) {
            case NO_DATA:
                tv_null.setVisibility(View.VISIBLE);
                lv_search.setVisibility(View.INVISIBLE);
                tv_hot_city.setVisibility(View.INVISIBLE);
                gv_hot_city.setVisibility(View.INVISIBLE);
                break;
            case HAVE_DATA:
                tv_null.setVisibility(View.INVISIBLE);
                lv_search.setVisibility(View.VISIBLE);
                tv_hot_city.setVisibility(View.INVISIBLE);
                gv_hot_city.setVisibility(View.INVISIBLE);
                break;
            case Normal:
                tv_null.setVisibility(View.INVISIBLE);
                lv_search.setVisibility(View.INVISIBLE);
                tv_hot_city.setVisibility(View.VISIBLE);
                gv_hot_city.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 0) {
            changeStatus(Normal);
            return;
        }
        cityBeenList = CityDao.queryCity(s.toString());
        if (cityBeenList.size() == 0) {
            changeStatus(NO_DATA);
        } else {
            changeStatus(HAVE_DATA);
            lv_search.setAdapter(new CityQueryAdapter(AddCityActivity.this, cityBeenList));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public void finishAdd(CityManageBean cityManageBean) {
        String name = cityManageBean.getName();
        CityManageBean bean = ((BaseApplication) getApplication()).cityManageDao.find(name);
        if (bean != null) {
            Toast.makeText(this, "城市“" + name + "”已经添加", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent();
            intent.putExtra("city", cityManageBean);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
