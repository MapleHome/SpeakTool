package com.speaktool.impl.dataloader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.speaktool.ui.custom.gif.GifDrawable;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.google.common.base.Preconditions;
import com.speaktool.Const;
import com.speaktool.bean.PicDataHolder;
import com.speaktool.busevents.LocalPictureThumbnailLoadedEvent;
import com.speaktool.service.AsyncDataLoaderFactory;
import com.speaktool.service.UseFactory;
import com.speaktool.utils.BitmapScaleUtil;
import com.speaktool.utils.LocalFileUtils;

import de.greenrobot.event.EventBus;

/**
 * 本地图片异步加载
 * 
 * @author shaoshuai
 * 
 */
@UseFactory(AsyncDataLoaderFactory.class)
public class LocalPicturesIconAsyncLoader extends ListItemAsyncDataLoader<String, PicDataHolder> {
	private static final String tag = LocalPicturesIconAsyncLoader.class.getSimpleName();

	public LocalPicturesIconAsyncLoader() {
		super();
	}

	@Override
	protected int getSizeOfValueBytes(String key, PicDataHolder value) {
		if (isGif(key)) {
			return value.gif.length;
		} else {
			return value.bpScaled.length;
		}
	}

	private static boolean isGif(String key) {
		return BitmapScaleUtil.isGif(key);
	}

	@Override
	protected PicDataHolder getDataLogic(String key, Object... args) {
		Preconditions.checkNotNull(key);
		try {
			if (isGif(key)) {

				PicDataHolder data = new PicDataHolder();
				data.gif = LocalFileUtils.loadFile(key);
				if (data.gif == null)
					return null;
				return data;
			} else {

				PicDataHolder data = new PicDataHolder();
				Bitmap bmp = BitmapScaleUtil.decodeSampledBitmapFromPath(key, Const.MAX_MEMORY_BMP_CAN_ALLOCATE);
				if (bmp == null)
					return null;

				ByteArrayOutputStream outstream = new ByteArrayOutputStream(bmp.getRowBytes() * bmp.getHeight());
				bmp.compress(CompressFormat.JPEG, 100, outstream);
				outstream.flush();

				data.bpScaled = outstream.toByteArray();
				outstream.close();
				if (data.bpScaled == null)
					return null;
				return data;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void postFinishEvent(String key, PicDataHolder value, Object... args) {
		Drawable d = null;
		if (BitmapScaleUtil.isGif(key)) {
			try {
				d = new GifDrawable(value.gif);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Bitmap bpScaled = BitmapFactory.decodeByteArray(value.bpScaled, 0, value.bpScaled.length);
			d = new BitmapDrawable(bpScaled);
		}
		if (d != null)
			EventBus.getDefault().post(new LocalPictureThumbnailLoadedEvent(key, d, false));
		else
			postErrorEvent(key, value);
	}

	@Override
	protected void postErrorEvent(String key, PicDataHolder value, Object... args) {
		// 通过EventBus订阅者发送消息
		EventBus.getDefault().post(new LocalPictureThumbnailLoadedEvent(key, null, true));
	}
}
