package com.win.weather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.win.weather.R;
import com.win.weather.bean.CityBean;

import java.util.List;

/**
 * Created by idea on 2016-09-29.
 */
public class CityQueryAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<CityBean> mList;

    public CityQueryAdapter(Context context, List<CityBean> mList) {
        this.mInflater = LayoutInflater.from(context);
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_city_query, parent, false);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_province = (TextView) convertView.findViewById(R.id.tv_province);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        CityBean bean = mList.get(position);
        viewHolder.tv_name.setText(bean.getName());
        viewHolder.tv_province.setText(bean.getProvince());
        return convertView;
    }

    class ViewHolder {
        TextView tv_name;
        TextView tv_province;
    }
}
