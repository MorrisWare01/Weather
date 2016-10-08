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
public class LifeIndexView extends LinearLayout {
    private TextView tv_name;
    private TextView tv_short;
    private TextView tv_long;
    private ImageView iv_pic;
    private ImageView iv_status;

    private Drawable pic;
    private String name;
    private String short_text = "暂无";
    private String long_text = "暂无";

    public LifeIndexView(Context context) {
        this(context, null);
    }

    public LifeIndexView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LifeIndexView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LifeIndexView);
        int count = ta.getIndexCount();
        for (int i = 0; i < count; i++) {
            int index = ta.getIndex(i);
            switch (index) {
                case R.styleable.LifeIndexView_life_index_pic:
                    pic = ta.getDrawable(index);
                    break;
                case R.styleable.LifeIndexView_life_index_name:
                    name = ta.getString(index);
                    break;
                case R.styleable.LifeIndexView_life_index_short_text:
                    short_text = ta.getString(index);
                    break;
                case R.styleable.LifeIndexView_life_index_long_text:
                    long_text = ta.getString(index);
                    break;
            }
        }
        ta.recycle();
        initViews(context);
    }

    private void initViews(Context context) {
        View view = View.inflate(context, R.layout.item_life_index, this);
        iv_pic = (ImageView) view.findViewById(R.id.iv_pic);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_short = (TextView) view.findViewById(R.id.tv_short);
        tv_long = (TextView) view.findViewById(R.id.tv_long);
        iv_status = (ImageView) view.findViewById(R.id.iv_status);
        iv_pic.setImageDrawable(pic);
        tv_name.setText(name);
        tv_short.setText(short_text);
        tv_long.setText(long_text);
    }

    public void toggle() {
        if (tv_long.getVisibility() == VISIBLE) {
            tv_long.setVisibility(GONE);
            iv_status.setImageResource(R.drawable.arrow_close);
        } else {
            tv_long.setVisibility(VISIBLE);
            iv_status.setImageResource(R.drawable.arrow_open);
        }
    }

    public void setVisibility(int visibility) {
        tv_long.setVisibility(visibility);
        iv_status.setImageResource(visibility == VISIBLE ? R.drawable.arrow_open : R.drawable.arrow_close);
    }

    public void setShortText(String text) {
        tv_short.setText(text);
    }

    public void setLongText(String text) {
        tv_long.setText(text);
    }

}
