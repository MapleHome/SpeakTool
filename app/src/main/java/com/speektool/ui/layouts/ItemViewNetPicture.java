package com.speektool.ui.layouts;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import roboguice.inject.InjectView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.speektool.R;
import com.speektool.injectmodules.IInject;
import com.speektool.injectmodules.Layout;
import com.speektool.injectmodules.ViewInjectUtils;

@Layout(R.layout.item_netpic)
public class ItemViewNetPicture extends FrameLayout implements IInject {
	@InjectView(R.id.ivGif)
	private GifImageView ivGif;
	@InjectView(R.id.loadingImageView)
	private ProgressBar loadingImageView;

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		/** set layout is square. */
		int sidelen = widthMeasureSpec > heightMeasureSpec ? heightMeasureSpec : widthMeasureSpec;
		super.onMeasure(sidelen, sidelen);
	}

	public Bitmap getThumbBitmap() {
		Drawable d = ivGif.getDrawable();
		if (d != null && d instanceof BitmapDrawable) {
			return ((BitmapDrawable) d).getBitmap();
		} else {
			return null;
		}
	}

	public ItemViewNetPicture(Context context) {
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
	}

	@Override
	protected void onDetachedFromWindow() {
		recycleBmp();
		super.onDetachedFromWindow();
	}

	private void recycleBmp() {
		Drawable old = ivGif.getDrawable();
		if (old != null) {
			ivGif.setImageDrawable(null);
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

		ivGif.setImageDrawable(d);
		ivGif.setVisibility(View.VISIBLE);
		loadingImageView.setVisibility(View.GONE);

	}

	public void setLoading() {
		recycleBmp();

		ivGif.setVisibility(View.GONE);
		loadingImageView.setVisibility(View.VISIBLE);
	}
}
