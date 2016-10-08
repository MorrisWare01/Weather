package com.win.weather.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.win.weather.R;

/**
 * Created by idea on 2016-10-01.
 */
public class WeatherInfoView extends LinearLayout {
    private Drawable pic;
    private String name;

    private ImageView iv_pic;
    private TextView tv_name;
    private TextView tv_info;

    public WeatherInfoView(Context context) {
        this(context, null);
    }

    public WeatherInfoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeatherInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WeatherInfoView);
        int count = ta.getIndexCount();
        for (int i = 0; i < count; i++) {
            int index = ta.getIndex(i);
            switch (index) {
                case R.styleable.WeatherInfoView_weather_info_pic:
                    pic = ta.getDrawable(index);
                    break;
                case R.styleable.WeatherInfoView_weather_info_name:
                    name = ta.getString(index);
                    break;
            }
        }
        ta.recycle();
        initViews(context);
        setClickable(true);
    }

    private void initViews(Context context) {
        View.inflate(context, R.layout.item_weather_info, this);
        iv_pic = (ImageView) findViewById(R.id.iv_pic);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_info = (TextView) findViewById(R.id.tv_info);
        iv_pic.setImageDrawable(pic);
        tv_name.setText(name);
    }

    public void setName(String text) {
        tv_name.setText(text);
    }

    public void setInfo(String text) {
        tv_info.setText(text);
    }
}
