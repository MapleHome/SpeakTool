package com.speaktool.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;

import com.bumptech.glide.Glide;
import com.speaktool.api.CourseItem;
import com.speaktool.ui.base.AbsAdapter;
import com.speaktool.utils.DeviceUtils;
import com.speaktool.view.layouts.ItemViewLocalRecord;

import java.io.File;
import java.util.List;

/**
 * 所有课程记录适配器
 *
 * @author shaoshuai
 */
public class RecordsAdapter extends AbsAdapter<CourseItem> {

    public RecordsAdapter(Context ctx, List<CourseItem> datas) {
        super(ctx, datas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new ItemViewLocalRecord(mContext);
        }
        ItemViewLocalRecord item = (ItemViewLocalRecord) convertView;
        // 重置高度
        int h = parent.getRootView().getWidth() / 3;
        if (DeviceUtils.isHengPing(mContext)) {// 横屏
            h = parent.getRootView().getWidth() / 6;
        }
        LayoutParams lp = (LayoutParams) convertView.getLayoutParams();
        if (lp == null)
            lp = new LayoutParams(-1, h);
        else
            lp.height = h;
        item.setLayoutParams(lp);
        CourseItem bean = (CourseItem) getItem(position);
        if (bean != null) {
            item.setTitle(bean.getRecordTitle());

            String imagePath = bean.getThumbnailImgPath();
            Glide.with(mContext)
                    .load(new File(imagePath))
                    .into(item.getImageView());
            item.setTag(imagePath);
        }
        return item;
    }

}
