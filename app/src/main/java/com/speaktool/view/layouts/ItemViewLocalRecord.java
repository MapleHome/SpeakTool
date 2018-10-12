package com.speaktool.view.layouts;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
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


    public ItemViewLocalRecord(Context context) {
        super(context);
        View view = View.inflate(getContext(), R.layout.griditem_local_record, this);
        ButterKnife.bind(this, view);
    }

    public void setTitle(String title) {
        tvRecordTitle.setText(title);
    }

    public ImageView getImageView() {
        return ivThumbnail;
    }

}
