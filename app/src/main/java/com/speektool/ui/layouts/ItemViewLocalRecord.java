package com.speektool.ui.layouts;

import roboguice.inject.InjectView;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.speektool.R;
import com.speektool.injectmodules.IInject;
import com.speektool.injectmodules.Layout;
import com.speektool.injectmodules.ViewInjectUtils;

/**
 * 课程记录条目
 * 
 * @author shaoshuai
 * 
 */
@Layout(R.layout.griditem_local_record)
public class ItemViewLocalRecord extends FrameLayout implements IInject {
	@InjectView(R.id.ivThumbnail)
	private ImageView ivThumbnail;
	@InjectView(R.id.tvRecordTitle)
	private TextView tvRecordTitle;

	@InjectView(R.id.uploadProgressLay)
	private View uploadProgressLay;

	@InjectView(R.id.progressBarUploadProgress)
	private ProgressBar progressBarUploadProgress;

	@InjectView(R.id.closeUpload)
	private ImageView closeUpload;

	/** 取消上传监听 */
	public void setCancelCLickListener(OnClickListener lsn) {
		closeUpload.setOnClickListener(lsn);
	}

	public ItemViewLocalRecord(Context context) {
		super(context);
		init();
	}

	private void init() {
		startInject();
		afterInject();
	}

	@Override
	public void startInject() {
		ViewInjectUtils.injectViews(this);
	}

	@Override
	public void afterInject() {
		// uploadProgressLay.setVisibility(View.GONE);
	}

	public void setThumbnail(Bitmap icon) {
		ivThumbnail.setImageBitmap(icon);
	}

	public void setTitle(String title) {
		tvRecordTitle.setText(title);
	}

	public ImageView getImageView() {
		return ivThumbnail;
	}

	public void setProgress(int progress) {
		uploadProgressLay.setVisibility(View.VISIBLE);
		progressBarUploadProgress.setProgress(progress);

	}

	public void setUploadingState(boolean isUploading) {
		if (isUploading)
			uploadProgressLay.setVisibility(View.VISIBLE);
		else
			uploadProgressLay.setVisibility(View.GONE);
	}

}
