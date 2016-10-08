package com.win.weather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.win.weather.R;
import com.win.weather.bean.LifeIndexBean;

import java.util.List;

/**
 * Created by idea on 2016-09-30.
 */
public class LifeIndexAdapter extends BaseAdapter {
    private static final int[] pics = {
            R.drawable.ic_life_info_item_chuanyi, R.drawable.ic_life_info_item_ziwaixian,
            R.drawable.ic_life_info_item_ganmao, R.drawable.ic_life_info_item_yundong,
            R.drawable.ic_life_info_item_xiche, R.drawable.ic_life_info_item_kongtiao,
            R.drawable.ic_life_info_item_diaoyu, R.drawable.ic_life_info_item_xianxing};

    private static final String[] names = {
            "穿衣指数", "紫外线指数", "感冒指数", "运动指数",
            "洗车指数", "空调指数", "垂钓指数", "尾号限行"
    };

    private LayoutInflater mInflater;
    private List<LifeIndexBean> mList;

    public LifeIndexAdapter(Context context, List<LifeIndexBean> list) {
        this.mInflater = LayoutInflater.from(context);
        this.mList = list;
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
            convertView = mInflater.inflate(R.layout.item_life_index, parent, false);
            viewHolder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_short = (TextView) convertView.findViewById(R.id.tv_short);
            viewHolder.tv_long = (TextView) convertView.findViewById(R.id.tv_long);
            viewHolder.iv_status = (ImageView) convertView.findViewById(R.id.iv_status);
            convertView.setTag(viewHolder);
            viewHolder.iv_pic.setImageResource(pics[position]);
            viewHolder.tv_name.setText(names[position]);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        LifeIndexBean bean = mList.get(position);
        String short_name = bean.getShort_name();
        String long_name = bean.getLong_name();
        viewHolder.tv_short.setText(short_name == null ? "暂无" : short_name);
        viewHolder.tv_long.setText(long_name == null ? "暂无" : long_name);
        return convertView;
    }

    private class ViewHolder {
        ImageView iv_pic;
        TextView tv_name;
        TextView tv_short;
        TextView tv_long;
        ImageView iv_status;
    }

}
