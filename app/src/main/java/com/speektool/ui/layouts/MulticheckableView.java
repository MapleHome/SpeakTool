package com.speektool.ui.layouts;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.speektool.R;

public class MulticheckableView extends FrameLayout {
	@ViewInject(R.id.ivImage)
	private GifImageView ivImage;
	@ViewInject(R.id.ivImageOverlay)
	private ImageView ivImageOverlay;
	@ViewInject(R.id.ivImageOverlayFlag)
	private ImageView ivImageOverlayFlag;
	@ViewInject(R.id.loadingImageView)
	private View loadingImageView;

	public MulticheckableView(Context context) {
		super(context);
		initView();
	}

	private void initView() {
		View view = View.inflate(getContext(), R.layout.multicheckable_imageview, this);
		ViewUtils.inject(this, view);

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
