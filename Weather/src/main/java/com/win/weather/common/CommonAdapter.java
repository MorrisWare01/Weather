package com.win.weather.common;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by idea on 2016/7/27 0027.
 */

public abstract class CommonAdapter<T> extends BaseAdapter {
    protected Context context;
    protected List<T> data;
    protected int layoutId;

    public CommonAdapter(Context context, List<T> data, int layoutId) {
        this.context = context;
        this.data = data;
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = ViewHolder.get(context, view, viewGroup, i, layoutId);

        convert(holder, data.get(i));

        return holder.getConvertView();
    }

    public abstract void convert(ViewHolder holder, T t);
}
