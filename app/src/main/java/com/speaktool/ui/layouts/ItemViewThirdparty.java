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

public class ItemViewThirdparty extends FrameLayout {
    @BindView(R.id.ivLogo) ImageView ivLogo;
    @BindView(R.id.tvName) TextView tvName;

    public ItemViewThirdparty(Context context) {
        super(context);
        init();
    }

    private void init() {
        View view = View.inflate(getContext(), R.layout.item_thirdparty, this);
        ButterKnife.bind(this, view);
    }

    public void setLogo(Bitmap bmp) {
        ivLogo.setImageBitmap(bmp);
    }

    public void setName(String name) {
        tvName.setText(name);
    }

}
