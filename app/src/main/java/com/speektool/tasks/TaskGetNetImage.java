package com.speektool.tasks;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;

import com.speektool.Const;
import com.speektool.utils.BitmapScaleUtil;

/**
 * 加载网络图片
 * 
 * @author shaoshuai
 * 
 */
public class TaskGetNetImage extends BaseRunnable<Integer, Bitmap> {

	public static interface NetImageLoadListener {

		void onNetImageLoaded(Bitmap result);
	}

	private final WeakReference<NetImageLoadListener> mListener;

	private String mImageUrl;

	public TaskGetNetImage(NetImageLoadListener listener, String url) {

		mListener = new WeakReference<NetImageLoadListener>(listener);
		mImageUrl = url;

	}

	@Override
	public void onPostExecute(Bitmap result) {
		NetImageLoadListener listener = mListener.get();
		if (null != listener) {

			listener.onNetImageLoaded(result);
		}
		super.onPostExecute(result);
	}

	@Override
	public Bitmap doBackground() {

		Bitmap bmp = BitmapScaleUtil.decodeSampledBitmapFromUrl(mImageUrl, Const.MAX_MEMORY_BMP_CAN_ALLOCATE, "");

		return bmp;
	}

}