package com.win.weather.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.win.weather.R;
import com.win.weather.bean.CityManageBean;
import com.win.weather.db.CityManageDao;
import com.win.weather.utils.PrefUtils;

import java.util.List;

/**
 * Created by idea on 2016-09-27.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ItemTouchHelper mItemTouchHelper;
    private List<CityManageBean> mList;
    private boolean isEdited;
    private long startTime;
    private static final long SPACE_TIME = 50;
    private CityManageDao dao;

    public MyRecyclerAdapter(Context context, ItemTouchHelper itemTouchHelper, List<CityManageBean> list) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mItemTouchHelper = itemTouchHelper;
        this.mList = list;
        this.dao = new CityManageDao(context);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        CityManageBean remove = mList.remove(fromPosition);
        mList.add(toPosition, remove);
        updateDatabase();
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_city_manage, parent, false);
        Resources resources = mContext.getResources();
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;
        int height = metric.heightPixels;
        float density = metric.density;
        int statusBarHeight1 = -1;
        //获取status_bar_height资源的ID
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = resources.getDimensionPixelSize(resourceId);
        }
        float left = resources.getDimension(R.dimen.city_rv_left_margin);
        float right = resources.getDimension(R.dimen.city_rv_right_margin);
        float top = resources.getDimension(R.dimen.city_rv_top_margin);
        float actionbar_height = resources.getDimension(R.dimen.actionbar_height);
        float remain_height = resources.getDimension(R.dimen.remain_height);
        float span = resources.getDimension(R.dimen.city_rv_span);

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = (int) ((width - left - right) / 3);
        layoutParams.height = (int) ((height - span * 4 - actionbar_height - statusBarHeight1 - remain_height - 20 * density - top) / 3);
        view.setLayoutParams(layoutParams);

        final ItemViewHolder holder = new ItemViewHolder(view);

        if (viewType == 0) {
            holder.tv_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    CityManageBean cm = mList.get(position);
                    if (!cm.isDefault()) {
                        for (CityManageBean management : mList) {
                            if (management.isDefault()) {
                                management.setDefault(false);
                                dao.update(management);
                            }
                        }
                        cm.setDefault(true);
                        dao.update(cm);
                        notifyDataSetChanged();
                    }
                }
            });

            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    CityManageBean cm = mList.get(position);
                    boolean selected = cm.isDefault();
                    mList.remove(position);
                    if (selected) {
                        if (mList.size() > 0) {
                            mList.get(0).setDefault(true);
                        }
                    }
                    updateDatabase();
                    if (PrefUtils.getString(mContext, cm.getName(), null) != null) {
                        PrefUtils.putString(mContext, cm.getName(), null);
                    }
                    notifyItemRemoved(position);
                    notifyItemChanged(0);
                }
            });

            holder.ll_move.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!isEdited) {
                        onStatusChangeListener.enterEdit();
                    }
                    mItemTouchHelper.startDrag(holder);
                    return true;
                }
            });

            holder.ll_move.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (isEdited) {
                        switch (MotionEventCompat.getActionMasked(event)) {
                            case MotionEvent.ACTION_DOWN:
                                startTime = System.currentTimeMillis();
                                break;
                            case MotionEvent.ACTION_MOVE:
                                if (System.currentTimeMillis() - startTime > SPACE_TIME) {
                                    mItemTouchHelper.startDrag(holder);
                                }
                                break;
                            case MotionEvent.ACTION_CANCEL:
                            case MotionEvent.ACTION_UP:
                                startTime = 0;
                                break;
                        }
                    }
                    return false;
                }
            });

            holder.ll_move.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isEdited)
                        onStatusChangeListener.onItemClick(holder.getAdapterPosition());
                }
            });
        } else {
            holder.ll_move.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isEdited)
                        onStatusChangeListener.onAddItemClick();
                }
            });
        }
        return holder;
    }

    @Override
    public int getItemCount() {
        if (mList.size() < 9) {
            return mList.size() + (isEdited ? 0 : 1);
        }
        return 9;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mList.size()) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        if (position >= mList.size()) {
            holder.tv_city.setVisibility(View.INVISIBLE);
            holder.tv_temp.setVisibility(View.INVISIBLE);
            holder.iv_sky.setVisibility(View.INVISIBLE);
            holder.tv_select.setVisibility(View.INVISIBLE);
            holder.iv_delete.setVisibility(View.INVISIBLE);
            holder.iv_add.setVisibility(View.VISIBLE);
        } else {
            CityManageBean cm = mList.get(position);
            holder.tv_city.setText(cm.getName());
            holder.tv_temp.setText(cm.getTemp());
            holder.iv_sky.setImageResource(cm.getResId());
            holder.tv_select.setBackgroundResource(cm.isDefault() ? R.drawable.city_select_bg2 : R.drawable.city_select_bg1);
            holder.tv_select.setText(cm.isDefault() ? "默认" : "设为默认");
            holder.iv_delete.setVisibility(isEdited ? View.VISIBLE : View.INVISIBLE);
            holder.iv_add.setVisibility(View.INVISIBLE);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tv_city;
        TextView tv_select;
        TextView tv_temp;
        ImageView iv_delete;
        ImageView iv_sky;
        ImageView iv_add;
        LinearLayout ll_move;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tv_city = (TextView) itemView.findViewById(R.id.tv_city);
            tv_select = (TextView) itemView.findViewById(R.id.tv_select);
            tv_temp = (TextView) itemView.findViewById(R.id.tv_temp);
            iv_delete = (ImageView) itemView.findViewById(R.id.iv_delete);
            iv_sky = (ImageView) itemView.findViewById(R.id.iv_sky);
            iv_add = (ImageView) itemView.findViewById(R.id.iv_add);
            ll_move = (LinearLayout) itemView.findViewById(R.id.ll_move);
        }
    }

    public void setDeleteVisibility(RecyclerView parent, boolean isEdited) {
        this.isEdited = isEdited;
        for (int i = 0; i < mList.size(); i++) {
            View view = parent.getChildAt(i);
            ImageView iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
            if (iv_delete != null) {
                if (isEdited) {
                    iv_delete.setVisibility(View.VISIBLE);
                } else {
                    iv_delete.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    public void setAddItemVisibility(boolean isEdited) {
        if (mList.size() < 9) {
            if (isEdited) {
                notifyDataSetChanged();
            } else {
                notifyDataSetChanged();
            }
        }
    }

    private OnStatusChangeListener onStatusChangeListener;

    public void setOnStatusChangeListener(OnStatusChangeListener onStatusChangeListener) {
        this.onStatusChangeListener = onStatusChangeListener;
    }

    private void updateDatabase() {
        List<CityManageBean> list = dao.findAll();
        for (CityManageBean bean : list) {
            dao.delete(bean.getName());
        }
        for (int i = 0, len = mList.size(); i < len; i++) {
            CityManageBean bean = mList.get(i);
            bean.setPosition(i);
            dao.insert(bean);
        }
    }
}
