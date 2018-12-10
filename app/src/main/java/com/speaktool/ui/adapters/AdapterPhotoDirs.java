package com.speaktool.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;

import com.speaktool.R;
import com.speaktool.base.AbsAdapter;
import com.speaktool.bean.LocalPhotoDirBean;
import com.speaktool.view.layouts.ItemViewLocalPhotoDirs;

import java.util.List;

public class AdapterPhotoDirs extends AbsAdapter<LocalPhotoDirBean> {

    public AdapterPhotoDirs(Context ctx, List<LocalPhotoDirBean> datas) {
        super(ctx, datas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new ItemViewLocalPhotoDirs(mContext);
        }
        final ItemViewLocalPhotoDirs item = (ItemViewLocalPhotoDirs) convertView;
        Bitmap initBmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);
        item.setDirIcon(initBmp);

        LocalPhotoDirBean bean = (LocalPhotoDirBean) getItem(position);
        if (bean != null) {
            item.setTag(bean.getDirIconPath());
            item.setDirName(bean.getDirName());
            item.setDirIncludeCounts(bean.getIncludeImageCounts() + "");
        }
        return item;
    }

}
