package com.speektool.adapters;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;

import com.ishare_lib.utils.DeviceUtils;
import com.speektool.R;
import com.speektool.activity.MainActivity;
import com.speektool.api.AsyncDataLoader;
import com.speektool.api.CourseItem;
import com.speektool.base.AbsAdapter;
import com.speektool.service.UploadService;
import com.speektool.ui.layouts.ItemViewLocalRecord;

/**
 * 所有课程记录适配器
 * 
 * @author shaoshuai
 * 
 */
public class RecordsAdapter extends AbsAdapter<CourseItem> {
	/** 默认记录缩略图 */
	private final Bitmap initBmp;
	private AsyncDataLoader<String, Bitmap> mAppIconAsyncLoader;
	private MainActivity mMainActivity;

	public RecordsAdapter(Context ctx, List<CourseItem> datas, AsyncDataLoader<String, Bitmap> appIconAsyncLoader,
			MainActivity pMainActivity) {
		super(ctx, datas);

		initBmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.defalut_icon);
		mAppIconAsyncLoader = appIconAsyncLoader;
		mMainActivity = pMainActivity;
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
		item.setUploadingState(mMainActivity.isUploading(imagePath));
		// item.setUploadingState(false);
		Bitmap cache = mAppIconAsyncLoader.getCache(imagePath);
		if (cache == null) {
			item.setThumbnail(getDefBmp());
		} else {
			item.setThumbnail(cache);
		}
		item.setTitle(bean.getRecordTitle());
		item.setTag(imagePath);

		item.setCancelCLickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				item.setUploadingState(false);

				Intent requestUploadIntent = new Intent(mContext, UploadService.class);
				requestUploadIntent.putExtra(UploadService.EXTRA_ACTION, UploadService.ACTION_CANCEL_UPLOAD);
				requestUploadIntent.putExtra(UploadService.EXTRA_CANCEL_TAG, imagePath);
				mContext.startService(requestUploadIntent);
			}
		});
		return item;
	}

}
