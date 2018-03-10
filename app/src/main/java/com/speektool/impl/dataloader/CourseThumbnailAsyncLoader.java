package com.speektool.impl.dataloader;

import android.graphics.Bitmap;

import com.google.common.base.Preconditions;
import com.speektool.Const;
import com.speektool.busevents.CourseThumbnailLoadedEvent;
import com.speektool.factory.AsyncDataLoaderFactory;
import com.speektool.injectmodules.UseFactory;
import com.speektool.utils.BitmapScaleUtil;
import com.speektool.utils.NetUtil;

import de.greenrobot.event.EventBus;

/**
 * 课程记录缩略图 异步加载
 * 
 * @author shaoshuai
 * 
 */
@UseFactory(AsyncDataLoaderFactory.class)
public class CourseThumbnailAsyncLoader extends
		ListItemAsyncDataLoader<String, Bitmap> {

	public CourseThumbnailAsyncLoader() {
		super();
	}
 
	@Override
	protected int getSizeOfValueBytes(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight();
	}

	@Override
	protected Bitmap getDataLogic(String key, Object... args) {
		Preconditions.checkNotNull(key);
		try {
			Bitmap bmp = null;
			if (NetUtil.isNetPath(key)) {
				bmp = BitmapScaleUtil.decodeSampledBitmapFromUrl(key,
						Const.MAX_MEMORY_BMP_CAN_ALLOCATE, "");
			} else {
				bmp = BitmapScaleUtil.decodeSampledBitmapFromPath(key,
						Const.MAX_MEMORY_BMP_CAN_ALLOCATE);
			}
			return bmp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void postFinishEvent(String key, Bitmap value, Object... args) {
		// 通过EventBus订阅者发送消息
		EventBus.getDefault().post(new CourseThumbnailLoadedEvent(key, value));
	}

	@Override
	protected void postErrorEvent(String key, Bitmap value, Object... args) {

	}

}
