package com.win.weather.adapter;

import android.content.Context;

import com.win.weather.R;
import com.win.weather.bean.ForecastBean;
import com.win.weather.common.CommonAdapter;
import com.win.weather.common.ViewHolder;

import java.util.List;

/**
 * Created by idea on 2016-10-05.
 */
public class ForecastAdapter extends CommonAdapter<ForecastBean> {

    public ForecastAdapter(Context context, List<ForecastBean> data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, ForecastBean forecastBean) {
        holder.setText(R.id.tv_week, forecastBean.getWeek()).setText(R.id.tv_temp, forecastBean.getTemp());
        holder.setImageResource(R.id.iv_sky, forecastBean.getDrawableId());
        holder.setText(R.id.tv_sky, forecastBean.getSky());
    }
}
