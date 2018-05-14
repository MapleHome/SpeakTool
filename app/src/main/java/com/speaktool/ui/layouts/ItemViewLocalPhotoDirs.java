package com.speaktool.ui.layouts;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.speaktool.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemViewLocalPhotoDirs extends FrameLayout {
    @BindView(R.id.ivDirIcon) ImageView ivDirIcon;
    @BindView(R.id.tvDirName) TextView tvDirName;
    @BindView(R.id.tvDirIncludeCounts) TextView tvDirIncludeCounts;

    public ItemViewLocalPhotoDirs(Context context) {
        super(context);
        init();
    }

    private void init() {
        View view = View.inflate(getContext(), R.layout.item_photodir, this);
        ButterKnife.bind(this, view);
    }

    public void setDirIcon(Bitmap bmp) {
        ivDirIcon.setImageBitmap(bmp);
    }

    public void setDirName(String dirName) {
        tvDirName.setText(dirName);
    }

    public void setDirIncludeCounts(String includeCounts) {
        tvDirIncludeCounts.setText(includeCounts);
    }

}
