package com.win.weather;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.win.weather.recyclerview.MyItemTouchAdapter;
import com.win.weather.adapter.MyRecyclerAdapter;
import com.win.weather.recyclerview.NoAlphaItemAnimator;
import com.win.weather.recyclerview.OnStatusChangeListener;
import com.win.weather.recyclerview.SpaceItemDecoration;
import com.win.weather.bean.CityManageBean;
import com.win.weather.bean.WeatherBean;
import com.win.weather.db.CityManageDao;
import com.win.weather.global.GlobalConstants;
import com.win.weather.utils.CacheUtils;
import com.win.weather.utils.FindResIdUtils;
import com.win.weather.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CityManageActivity extends AppCompatActivity implements View.OnClickListener, OnStatusChangeListener {
    private RecyclerView mRecyclerView;
    private ImageView iv_edit;
    private ImageView iv_refresh;
    private ImageView iv_back;
    private View view;
    private List<CityManageBean> mCityList;
    private boolean isEdited;
    private MyRecyclerAdapter mAdapter;
    private CityManageDao dao;
    private RotateAnimation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_manage);
        initToolbar();
        initData();
        initRecyclerView();
        initAnimation();
    }

    private void initAnimation() {
        animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(500);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(-1);
//        iv_refresh.startAnimation(animation);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_edit = (ImageView) findViewById(R.id.iv_edit);
        iv_refresh = (ImageView) findViewById(R.id.iv_refresh);
        view = findViewById(R.id.divider);
        iv_edit.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        iv_refresh.setOnClickListener(this);
    }

    private void initData() {
        dao = ((BaseApplication) getApplication()).cityManageDao;
        mCityList = (List<CityManageBean>) getIntent().getSerializableExtra("cityList");
        if (mCityList == null) {
            mCityList = new ArrayList<>();
        }

        for (CityManageBean bean : mCityList) {
            String cache = CacheUtils.getCache(this, bean.getCity());
            if (cache != null) {
                Gson gson = new Gson();
                WeatherBean weatherBean = gson.fromJson(cache, WeatherBean.class);
                WeatherBean.Weather weather = weatherBean.weathers.get(0);
                WeatherBean.Weather.DailyForecast.TmpBean tmp = weather.daily_forecast.get(0).tmp;
                String temp = tmp.min + "~" + tmp.max + "℃";
                int resId = FindResIdUtils.findDrawableIdByString(this, "d" + weather.now.cond.code);
                bean.setTemp(temp);
                bean.setResId(resId);
            }
        }
    }

    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        MyItemTouchAdapter mItemTouchAdapter = new MyItemTouchAdapter();
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(mItemTouchAdapter);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        mAdapter = new MyRecyclerAdapter(this, mItemTouchHelper, mCityList);
        mAdapter.setOnStatusChangeListener(this);

        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);
        int span = (int) getResources().getDimension(R.dimen.city_rv_span);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(span));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new NoAlphaItemAnimator());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_edit:
                doEdit();
                break;
            case R.id.iv_back:
                doBack();
                break;
            case R.id.iv_refresh:
                if (mCityList.size() == 0) {
                    Toast.makeText(CityManageActivity.this, "需要先添加城市", Toast.LENGTH_SHORT).show();
                } else {
                    doRefresh();
                }
                break;
        }
    }

    private void doEdit() {
        isEdited = !isEdited;
        iv_edit.setImageResource(isEdited ? R.drawable.city_mgr_top_bar_edit_done_icon_normal : R.drawable.city_mgr_top_bar_edit_icon_normal);
        iv_refresh.setVisibility(isEdited ? View.GONE : View.VISIBLE);
        view.setVisibility(isEdited ? View.GONE : View.VISIBLE);
        mAdapter.setDeleteVisibility(mRecyclerView, isEdited);
        mAdapter.setAddItemVisibility(isEdited);
    }

    private void doBack() {
        back();
    }


    private void doRefresh() {
        if (NetworkUtils.isNetworkConnected(this)) {
            iv_refresh.setClickable(false);
            iv_refresh.startAnimation(animation);
            animation.start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshAll();
                }
            }, 1000);
        } else {
            Toast.makeText(this, "需要连接网络", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshAll() {
        for (final CityManageBean bean : mCityList) {
            final String city = bean.getCity();
            String url = GlobalConstants.WEATHER_URL + "?city=" + city + "&key=" + GlobalConstants.WEATHER_KEY;
            HttpUtils httpUtils = new HttpUtils();
            httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    Gson gson = new Gson();
                    WeatherBean weatherBean = gson.fromJson(responseInfo.result, WeatherBean.class);
                    WeatherBean.Weather weather = weatherBean.weathers.get(0);
                    if (weather.daily_forecast.size() > 0) {
                        WeatherBean.Weather.DailyForecast.TmpBean tmp = weather.daily_forecast.get(0).tmp;
                        String temp = tmp.min + "~" + tmp.max + "℃";
                        int resId = FindResIdUtils.findDrawableIdByString(CityManageActivity.this, "d" + weather.now.cond.code);
                        bean.setTemp(temp);
                        bean.setResId(resId);
                        mAdapter.notifyDataSetChanged();
                        CacheUtils.putCache(CityManageActivity.this, city, responseInfo.result);
                    }
                    if (bean.getPosition() == mCityList.size() - 1) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv_refresh.clearAnimation();
                                Toast.makeText(CityManageActivity.this, "天气更新完毕", Toast.LENGTH_SHORT).show();
                                iv_refresh.setClickable(true);
                            }
                        });
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CityManageActivity.this.animation.cancel();
                            Toast.makeText(CityManageActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                            iv_refresh.setClickable(true);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void enterEdit() {
        doEdit();
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAddItemClick() {
        Intent intent = new Intent(this, AddCityActivity.class);
        startActivityForResult(intent, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK) {
            CityManageBean cityManageBean = (CityManageBean) data.getSerializableExtra("city");
            if (mCityList.size() == 0) {
                cityManageBean.setDefault(true);
            }
            cityManageBean.setPosition(mCityList.size());
            mCityList.add(cityManageBean);
            dao.insert(cityManageBean);
            mAdapter.notifyDataSetChanged();
            getCityDataFromAPI(cityManageBean);
        }
    }

    public void getCityDataFromAPI(final CityManageBean bean) {
        final String city = bean.getCity();
        String url = GlobalConstants.WEATHER_URL + "?city=" + city + "&key=" + GlobalConstants.WEATHER_KEY;
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Gson gson = new Gson();
                WeatherBean weatherBean = gson.fromJson(responseInfo.result, WeatherBean.class);
                WeatherBean.Weather weather = weatherBean.weathers.get(0);
                if (weather.status != null && weather.status.equals("ok")) {
                    WeatherBean.Weather.DailyForecast.TmpBean tmp = weather.daily_forecast.get(0).tmp;
                    String temp = tmp.min + "~" + tmp.max + "℃";
                    int resId = FindResIdUtils.findDrawableIdByString(CityManageActivity.this, "d" + weather.now.cond.code);
                    bean.setTemp(temp);
                    bean.setResId(resId);
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(CityManageActivity.this, "天气更新完毕", Toast.LENGTH_SHORT).show();
                    CacheUtils.putCache(CityManageActivity.this, city, responseInfo.result);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(CityManageActivity.this, "更新天气失败，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
        }
        return false;
    }

    private void back() {
        if (isEdited) {
            doEdit();
        } else {
            if (mCityList.size() > 0) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                exitBy2Click();
            }
        }
    }

    private Boolean isExit = false;

    private void exitBy2Click() {
        Timer timer = null;
        if (!isExit) {
            isExit = true;
            Toast.makeText(this, "请添加城市或再按一次退出程序", Toast.LENGTH_SHORT).show();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            finish();
        }
    }
}
