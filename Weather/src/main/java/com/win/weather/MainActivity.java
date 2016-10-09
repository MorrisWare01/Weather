package com.win.weather;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.win.weather.bean.CityManageBean;
import com.win.weather.db.CityManageDao;
import com.win.weather.fragment.MainFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements
        ViewPager.OnPageChangeListener, View.OnClickListener {
    private TextView tv_city;
    private ImageView iv_add;
    private ImageView iv_location;
    private ViewPager vp_weather;
    private LinearLayout ll_dots;
    private ImageView iv_white_dot;
    private List<MainFragment> mFragmentList;
    private List<CityManageBean> mCityList;
    private int width;
    private int position = -1;
    private int default_position = -1;
    private int location_position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        initViews();
        initData();
        initPosition();
        initDots();
    }

    private void initToolbar() {
        iv_add = (ImageView) findViewById(R.id.iv_add);
        tv_city = (TextView) findViewById(R.id.tv_city);
        iv_location = (ImageView) findViewById(R.id.iv_location);
    }

    private void initViews() {
        vp_weather = (ViewPager) findViewById(R.id.vp_weather);
        iv_add.setOnClickListener(this);
        vp_weather.addOnPageChangeListener(this);
    }

    private void initData() {
        CityManageDao dao = ((BaseApplication) getApplication()).cityManageDao;
        mFragmentList = new ArrayList<>();
        mCityList = dao.findAll();
        Collections.sort(mCityList);
        for (CityManageBean bean : mCityList) {
            mFragmentList.add(MainFragment.newInstance(bean.getCity()));
            if (bean.isDefault()) {
                default_position = bean.getPosition();
            }
            if (bean.getName().equals("自动定位")) {
                location_position = bean.getPosition();
            }
        }
        WeatherPagerAdapter pagerAdapter = new WeatherPagerAdapter(getSupportFragmentManager());
        vp_weather.setAdapter(pagerAdapter);
    }

    private void initPosition() {
        position = getIntent().getIntExtra("position", -1);
        position = position == -1 ? default_position : position;
        vp_weather.setCurrentItem(this.position);
        tv_city.setText(mCityList.get(this.position).getCity());
        iv_location.setVisibility(this.position == location_position ? View.VISIBLE : View.INVISIBLE);
    }

    private void initDots() {
        ll_dots = (LinearLayout) findViewById(R.id.ll_dots);
        iv_white_dot = (ImageView) findViewById(R.id.white_dot);
        if (mCityList.size() > 1) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int dot_width = (int) (metrics.density * 5);
            int dot2dot = (int) (10 * metrics.density);
            for (int i = 0; i < mCityList.size(); i++) {
                ImageView imageView = new ImageView(this);
                imageView.setBackgroundResource(R.drawable.shape_gray_dot);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dot_width, dot_width);
                if (i > 0) {
                    params.leftMargin = dot2dot;
                }
                imageView.setLayoutParams(params);
                ll_dots.addView(imageView);
            }
            ll_dots.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    width = ll_dots.getChildAt(1).getLeft() - ll_dots.getChildAt(0).getLeft();
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_white_dot.getLayoutParams();
                    params.leftMargin = position * width;
                    iv_white_dot.setLayoutParams(params);
                    ll_dots.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
        } else {
            ll_dots.setVisibility(View.INVISIBLE);
            iv_white_dot.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add:
                Intent intent = new Intent(this, CityManageActivity.class);
                intent.putExtra("cityList", (Serializable) mCityList);
                startActivity(intent);
                finish();
                break;
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mCityList.size() > 1) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_white_dot.getLayoutParams();
            params.leftMargin = (int) ((positionOffset + position) * width);
            iv_white_dot.setLayoutParams(params);
        }
    }

    @Override
    public void onPageSelected(int position) {
        tv_city.setText(mCityList.get(position).getCity());
        iv_location.setVisibility(position == location_position ? View.VISIBLE : View.INVISIBLE);
        for (MainFragment fragment : mFragmentList) {
            fragment.resetScroll();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(getApplicationContext(), "QQ分享成功", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "取消分享", Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(), "分享失败" + msg.obj, Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }

    };

    private class WeatherPagerAdapter extends FragmentPagerAdapter {

        public WeatherPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //super.destroyItem(container, position, object);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click();
        }
        return false;
    }

    private Boolean isExit = false;

    private void exitBy2Click() {
        Timer timer = null;
        if (!isExit) {
            isExit = true;
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
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
