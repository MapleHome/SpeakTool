package com.speektool.factory;

import android.graphics.Bitmap;

import com.speektool.SpeekToolApp;
import com.speektool.api.AsyncDataLoader;
import com.speektool.bean.PicDataHolder;
import com.speektool.impl.dataloader.CourseThumbnailAsyncLoader;
import com.speektool.impl.dataloader.LocalPhotoDirLoader;
import com.speektool.impl.dataloader.LocalPicturesIconAsyncLoader;
import com.speektool.impl.dataloader.NetPicturesIconAsyncLoader;
import com.speektool.impl.dataloader.ThirdpartyItemLoader;

/**
 * 异步数据加载工厂
 * 
 * @author shaoshuai
 * 
 */
public class AsyncDataLoaderFactory {
	/** 新课程记录的缩略图 异步加载 */
	public static AsyncDataLoader<String, Bitmap> newCourseThumbnailAsyncLoader() {
		return new CourseThumbnailAsyncLoader();
	}

	/** 网络图片异步加载 */
	public static AsyncDataLoader<String, PicDataHolder> newNetPicturesIconAsyncLoader() {
		return new NetPicturesIconAsyncLoader();
	}

	/** 本地图片异步加载 */
	public static AsyncDataLoader<String, PicDataHolder> newLocalPicturesIconAsyncLoader() {
		return new LocalPicturesIconAsyncLoader();
	}

	/** 本地图片目录加载 */
	public static AsyncDataLoader<String, byte[]> newLocalPhotoDirLoader() {
		return new LocalPhotoDirLoader();
	}

	/** 第三方条目加载 */
	public static AsyncDataLoader<Object, Bitmap> newThirdpartyItemLoader() {
		return new ThirdpartyItemLoader(SpeekToolApp.app());
	}
}
