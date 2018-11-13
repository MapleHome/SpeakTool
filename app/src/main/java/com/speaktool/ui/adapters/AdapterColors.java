package com.speaktool.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.speaktool.bean.PaintInfoBean;
import com.speaktool.base.AbsAdapter;

import java.util.List;

public class AdapterColors extends AbsAdapter<PaintInfoBean> {

    public AdapterColors(Context ctx, List<PaintInfoBean> datas) {
        super(ctx, datas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new ImageView(mContext);
            convertView.setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT));
        }
        ImageView item = (ImageView) convertView;

        PaintInfoBean bean = (PaintInfoBean) getItem(position);
        if (bean != null)
            item.setImageResource(bean.getIconResId());

        return item;
    }

}
