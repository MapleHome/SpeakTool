package com.speaktool.view.layouts;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.speaktool.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 课程记录条目
 *
 * @author shaoshuai
 */
public class ItemViewLocalRecord extends FrameLayout {
    @BindView(R.id.ivThumbnail) ImageView ivThumbnail;
    @BindView(R.id.tvRecordTitle) TextView tvRecordTitle;
    @BindView(R.id.uploadProgressLay) View uploadProgressLay;
    @BindView(R.id.progressBarUploadProgress) ProgressBar progressBarUploadProgress;
    @BindView(R.id.closeUpload) ImageView closeUpload;

    /**
     * 取消上传监听
     */
    public void setCancelCLickListener(OnClickListener lsn) {
        closeUpload.setOnClickListener(lsn);
    }

    public ItemViewLocalRecord(Context context) {
        super(context);
        init();
    }

    private void init() {
        View view = View.inflate(getContext(), R.layout.griditem_local_record, this);
        ButterKnife.bind(this, view);

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
