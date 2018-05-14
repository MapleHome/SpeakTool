package com.speaktool.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;

import com.speaktool.R;
import com.speaktool.api.AsyncDataLoader;
import com.speaktool.api.CourseItem;
import com.speaktool.ui.base.AbsAdapter;
import com.speaktool.ui.layouts.ItemViewLocalRecord;
import com.speaktool.utils.DeviceUtils;

import java.util.List;

/**
 * 所有课程记录适配器
 *
 * @author shaoshuai
 */
public class RecordsAdapter extends AbsAdapter<CourseItem> {
    /**
     * 默认记录缩略图
     */
    private final Bitmap initBmp;
    private AsyncDataLoader<String, Bitmap> mAppIconAsyncLoader;

    public RecordsAdapter(Context ctx, List<CourseItem> datas, AsyncDataLoader<String, Bitmap> appIconAsyncLoader) {
        super(ctx, datas);

        initBmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);
        mAppIconAsyncLoader = appIconAsyncLoader;
    }

    public Bitmap getDefBmp() {
        return initBmp;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new ItemViewLocalRecord(mContext);
        }
        final ItemViewLocalRecord item = (ItemViewLocalRecord) convertView;
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
        //
        CourseItem bean = (CourseItem) getItem(position);
        if (bean == null) {
            return item;
        }
        final String imagePath = bean.getThumbnailImgPath();
        item.setUploadingState(false);
        // item.setUploadingState(false);
        Bitmap cache = mAppIconAsyncLoader.getCache(imagePath);
        if (cache == null) {
            item.setThumbnail(getDefBmp());
        } else {
            item.setThumbnail(cache);
        }
        item.setTitle(bean.getRecordTitle());
        item.setTag(imagePath);

        return item;
    }

}
