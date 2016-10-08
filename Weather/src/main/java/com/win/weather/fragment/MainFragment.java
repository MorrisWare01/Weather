package com.win.weather.fragment;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.win.weather.R;
import com.win.weather.adapter.ForecastAdapter;
import com.win.weather.bean.ForecastBean;
import com.win.weather.bean.WeatherBean;
import com.win.weather.global.GlobalConstants;
import com.win.weather.utils.CacheUtils;
import com.win.weather.utils.FindResIdUtils;
import com.win.weather.utils.ListViewUtils;
import com.win.weather.utils.MyDateUtils;
import com.win.weather.utils.NetworkUtils;
import com.win.weather.view.LifeIndexView;
import com.win.weather.view.WeatherInfoView;
import com.win.weather.view.WeatherScrollView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by idea on 2016-10-02.
 */
public class MainFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private int[] liv_ids = {R.id.liv_drsg, R.id.liv_uv, R.id.liv_flu, R.id.liv_sport, R.id.liv_cw, R.id.liv_trav};
    private int[] wiv_ids = {R.id.wiv_sun_rise, R.id.wiv_sun_set, R.id.wiv_wind_speed, R.id.wiv_wind_direct, R.id.wiv_flu, R.id.wiv_hum, R.id.wiv_pres};
    private String city;

    private Activity activity;
    private WeatherScrollView mScrollView;
    private ImageView iv_today_sky;
    private ImageView iv_tomorrow_sky;
    private TextView tv_today_temp;
    private TextView tv_tomorrow_temp;
    private TextView tv_aqi;
    private TextView tv_aqi_txt;
    private TextView tv_temp;
    private TextView tv_sky;
    private TextView tv_publish;
    private ListView lv_forecast;
    private LifeIndexView[] livs = new LifeIndexView[liv_ids.length];
    private WeatherInfoView[] wivs = new WeatherInfoView[wiv_ids.length];
    private RelativeLayout rl_content;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView tv_du;

    public static MainFragment newInstance(String city) {
        MainFragment fragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putString("city", city);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        city = getArguments().getString("city");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initScrollView(view);
        TextView textView = (TextView) view.findViewById(R.id.tv_date);
        textView.setText(city);
        initContentView(view);
        initViews(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData() {
        String cacheJson = CacheUtils.getCache(activity, city);
        if (cacheJson == null) {
            getJsonFromAPI();
        } else {
            Date date = parseData(cacheJson);
            if ((new Date().getTime() - date.getTime()) > 60 * 60 * 1000) {
                getJsonFromAPI();
            }
        }
    }

    private void getJsonFromAPI() {
        String url = GlobalConstants.WEATHER_URL + "?city=" + city + "&key=" + GlobalConstants.WEATHER_KEY;
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(final ResponseInfo<String> responseInfo) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        parseData(responseInfo.result);
                        CacheUtils.putCache(activity, city, responseInfo.result);
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(activity, "已更新到最新天气", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 1000);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(activity, "更新失败,请联系客服", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Date parseData(String json) {
        Date date = null;
        Gson gson = new Gson();
        WeatherBean weatherBean = gson.fromJson(json, WeatherBean.class);
        if (weatherBean.weathers.size() > 0) {
            WeatherBean.Weather weather = weatherBean.weathers.get(0);
            if (weather.status != null && weather.status.equals("ok")) {
                parseNavigation(weather.daily_forecast);
                parseAqi(weather.aqi);
                parseNow(weather.now, weather.basic.update.loc);
                parseForecast(weather.daily_forecast);
                parseInfo(weather);
                parseLifeIndex(weather.suggestion);
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(weather.basic.update.loc);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return date == null ? new Date() : date;
    }

    private void parseNavigation(ArrayList<WeatherBean.Weather.DailyForecast> dailyForecast) {
        WeatherBean.Weather.DailyForecast todayForecast = dailyForecast.get(0);
        String today_temp = todayForecast.tmp.min + "~" + todayForecast.tmp.max + "℃";
        int todayResId = FindResIdUtils.findDrawableIdByString(activity, "d" + todayForecast.cond.code_d);
        iv_today_sky.setImageResource(todayResId);
        tv_today_temp.setText(today_temp);
        WeatherBean.Weather.DailyForecast tomorrowForecast = dailyForecast.get(1);
        String tomorrow_temp = tomorrowForecast.tmp.min + "~" + tomorrowForecast.tmp.max + "℃";
        int tomorrowResId = FindResIdUtils.findDrawableIdByString(activity, "d" + tomorrowForecast.cond.code_d);
        iv_tomorrow_sky.setImageResource(tomorrowResId);
        tv_tomorrow_temp.setText(tomorrow_temp);
    }

    private void parseAqi(WeatherBean.Weather.AqiBean aqiBean) {
        if (aqiBean != null) {
            String aqi = aqiBean.city.aqi;
            String qlty = aqiBean.city.qlty;
            if (aqi != null) {
                tv_aqi.setText(aqi);
            }
            if (qlty != null) {
                if (qlty.equals("优")) {
                    tv_aqi_txt.setBackgroundColor(ContextCompat.getColor(activity, R.color.green));
                } else if (qlty.equals("良")) {
                    tv_aqi_txt.setBackgroundColor(ContextCompat.getColor(activity, R.color.yellow));
                } else if (qlty.equals("轻度污染")) {
                    tv_aqi_txt.setBackgroundColor(ContextCompat.getColor(activity, R.color.orange));
                } else if (qlty.equals("中度污染")) {
                    tv_aqi_txt.setBackgroundColor(ContextCompat.getColor(activity, R.color.red));
                } else if (qlty.equals("重度污染")) {
                    tv_aqi_txt.setBackgroundColor(ContextCompat.getColor(activity, R.color.purple));
                } else if (qlty.equals("严重污染")) {
                    tv_aqi_txt.setBackgroundColor(ContextCompat.getColor(activity, R.color.dark_red));
                } else {
                    tv_aqi_txt.setBackgroundColor(0x00000000);
                }
                tv_aqi_txt.setText(qlty.length() < 3 ? "空气" + qlty : qlty);
            }
        }
    }

    private void parseNow(WeatherBean.Weather.NowBean now, String loc) {
        if (now != null) {
            tv_publish.setText(loc + " 发布");

            if (now.tmp != null) {
                tv_temp.setText(now.tmp);
                tv_du.setText("°  C");
            } else {
                tv_du.setText(null);
            }
            if (now.cond != null && now.cond.txt != null) {
                tv_sky.setText(now.cond.txt);
            }
        }
    }

    private void parseForecast(ArrayList<WeatherBean.Weather.DailyForecast> dailyForecast) {
        List<ForecastBean> list = new ArrayList<>();
        for (WeatherBean.Weather.DailyForecast forecast : dailyForecast) {
            try {
                String week = MyDateUtils.getWeekOfDate(new SimpleDateFormat("yyyy-MM-dd").parse(forecast.date));
                String temp = forecast.tmp.min + "~" + forecast.tmp.max + "℃";
                String sky = forecast.cond.txt_d;
                int resId = FindResIdUtils.findDrawableIdByString(activity, "d" + forecast.cond.code_d);
                list.add(new ForecastBean(week, resId, sky, temp));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (list.size() > 0) {
            list.get(0).setWeek("今天");
            lv_forecast.setAdapter(new ForecastAdapter(activity, list, R.layout.item_forecast));
            ListViewUtils.setListViewHeightBasedOnChildren(lv_forecast);
        }
    }

    private void parseInfo(WeatherBean.Weather weather) {
        WeatherBean.Weather.DailyForecast forecast = weather.daily_forecast.get(0);
        if (forecast.astro != null) {
            wivs[0].setInfo(forecast.astro.sr);
            wivs[1].setInfo(forecast.astro.ss);
        }
        if (forecast.wind != null) {
            wivs[2].setInfo(forecast.wind.spd + " km/h");
            wivs[3].setInfo(forecast.wind.dir);
        }
        if (weather.now != null) {
            wivs[4].setInfo(weather.now.fl + "℃");
            wivs[5].setInfo(weather.now.hum + "%");
            wivs[6].setInfo(weather.now.pres + " hpa");
        }
    }

    private void parseLifeIndex(WeatherBean.Weather.SuggestionBean suggestion) {
        if (suggestion != null) {
            livs[0].setShortText(suggestion.drsg.brf);
            livs[0].setLongText(suggestion.drsg.txt);
            livs[1].setShortText(suggestion.uv.brf);
            livs[1].setLongText(suggestion.uv.txt);
            livs[2].setShortText(suggestion.flu.brf);
            livs[2].setLongText(suggestion.flu.txt);
            livs[3].setShortText(suggestion.sport.brf);
            livs[3].setLongText(suggestion.sport.txt);
            livs[4].setShortText(suggestion.cw.brf);
            livs[4].setLongText(suggestion.cw.txt);
            livs[5].setShortText(suggestion.trav.brf);
            livs[5].setLongText(suggestion.trav.txt);
        }
    }

    private void initScrollView(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        mScrollView = (WeatherScrollView) view.findViewById(R.id.sv_content);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_dark, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
    }

    private void initContentView(View view) {
        rl_content = (RelativeLayout) view.findViewById(R.id.rl_content);
        ViewGroup.LayoutParams layoutParams = rl_content.getLayoutParams();
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        Resources resources = getResources();
        //获取status_bar_height资源的ID
        int statusBarHeight1 = -1;
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = resources.getDimensionPixelSize(resourceId);
        }
        float actionbar_height = resources.getDimension(R.dimen.main_actionbar_height);
        layoutParams.height = (int) (metric.heightPixels - statusBarHeight1 - actionbar_height);
        rl_content.setLayoutParams(layoutParams);
    }

    private void initViews(View view) {
        iv_today_sky = (ImageView) view.findViewById(R.id.iv_today_sky);
        iv_tomorrow_sky = (ImageView) view.findViewById(R.id.iv_tomorrow_sky);
        tv_today_temp = (TextView) view.findViewById(R.id.tv_today_temp);
        tv_tomorrow_temp = (TextView) view.findViewById(R.id.tv_tomorrow_temp);
        tv_aqi = (TextView) view.findViewById(R.id.tv_aqi);
        tv_aqi_txt = (TextView) view.findViewById(R.id.tv_aqi_txt);
        lv_forecast = (ListView) view.findViewById(R.id.lv_forecast);
        for (int i = 0; i < wiv_ids.length; i++) {
            wivs[i] = (WeatherInfoView) view.findViewById(wiv_ids[i]);
        }
        for (int i = 0; i < liv_ids.length; i++) {
            livs[i] = (LifeIndexView) view.findViewById(liv_ids[i]);
            livs[i].setOnClickListener(this);
        }
        tv_temp = (TextView) view.findViewById(R.id.tv_temp);
        tv_du = (TextView) view.findViewById(R.id.tv_du);
        tv_sky = (TextView) view.findViewById(R.id.tv_sky);
        tv_publish = (TextView) view.findViewById(R.id.tv_publish);
        Typeface fontFace = Typeface.createFromAsset(activity.getAssets(), "fonts/HelveticaNeueLTPro-Lt.ttf");
        Typeface fontFace1 = Typeface.createFromAsset(activity.getAssets(), "fonts/HelveticaNeueLTPro-Th_zz.ttf");
        tv_temp.setTypeface(fontFace);
        tv_du.setTypeface(fontFace1, Typeface.BOLD);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.liv_drsg:
                toggle(0);
                break;
            case R.id.liv_uv:
                toggle(1);
                break;
            case R.id.liv_flu:
                toggle(2);
                break;
            case R.id.liv_sport:
                toggle(3);
                break;
            case R.id.liv_cw:
                toggle(4);
                break;
            case R.id.liv_trav:
                toggle(5);
                break;
        }
    }

    @Override
    public void onRefresh() {
        if (NetworkUtils.isNetworkConnected(activity)) {
            getJsonFromAPI();
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            Toast.makeText(activity, "更新失败,网络不可用", Toast.LENGTH_SHORT).show();
        }
    }

    public void toggle(int index) {
        for (int i = 0; i < liv_ids.length; i++) {
            if (index != i) {
                livs[i].setVisibility(View.GONE);
            }
        }
        livs[index].toggle();
    }

    public void resetScroll() {
        if (mScrollView != null && rl_content.getLayoutParams().height != 0) {
            mScrollView.scrollTo(mScrollView.getScrollX(), 0);
        }
    }

}
