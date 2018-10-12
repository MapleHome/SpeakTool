package com.speaktool.view.layouts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.speaktool.R;
import com.speaktool.view.gif.GifDrawable;
import com.speaktool.view.gif.GifImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MulticheckableView extends FrameLayout {
    @BindView(R.id.ivImage) GifImageView ivImage;
    @BindView(R.id.ivImageOverlay) ImageView ivImageOverlay;
    @BindView(R.id.ivImageOverlayFlag) ImageView ivImageOverlayFlag;
    @BindView(R.id.loadingImageView) View loadingImageView;

    public MulticheckableView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        View view = View.inflate(getContext(), R.layout.multicheckable_imageview, this);
        ButterKnife.bind(this, view);

        ivImageOverlay.setVisibility(View.GONE);
        ivImageOverlayFlag.setVisibility(View.GONE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /** set layout is square. */
        int sidelen = widthMeasureSpec > heightMeasureSpec ? heightMeasureSpec : widthMeasureSpec;
        super.onMeasure(sidelen, sidelen);
    }

    public void setLoading() {
        recycleBmp();
        loadingImageView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDetachedFromWindow() {

        recycleBmp();

        super.onDetachedFromWindow();
    }

    private void recycleBmp() {

        Drawable old = ivImage.getDrawable();

        if (old != null) {
            ivImage.setImageDrawable(null);
            if (old instanceof GifDrawable) {
                GifDrawable o = (GifDrawable) old;
                o.stop();
                o.recycle();

            } else if (old instanceof BitmapDrawable) {
                BitmapDrawable o = (BitmapDrawable) old;
                Bitmap bmp = o.getBitmap();
                if (bmp != null)
                    bmp.recycle();
            }

        }
    }

    public void setImage(Drawable d) {
        recycleBmp();
        ivImage.setImageDrawable(d);
        loadingImageView.setVisibility(View.GONE);
    }

    private boolean ischecked = false;

    public void check() {
        ivImageOverlay.setVisibility(View.VISIBLE);
        ivImageOverlayFlag.setVisibility(View.VISIBLE);
        ischecked = true;
    }

    public void uncheck() {
        ivImageOverlay.setVisibility(View.GONE);
        ivImageOverlayFlag.setVisibility(View.GONE);
        ischecked = false;
    }

    public boolean ischecked() {
        return ischecked;
    }

}
