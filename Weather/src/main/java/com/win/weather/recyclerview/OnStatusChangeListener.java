package com.win.weather.recyclerview;

/**
 * Created by idea on 2016-09-28.
 */
public interface OnStatusChangeListener {
    void enterEdit();

    void onItemClick(int position);

    void onAddItemClick();
}
