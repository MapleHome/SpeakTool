package com.speaktool.service;

import android.graphics.Bitmap;

import com.speaktool.SpeakToolApp;
import com.speaktool.api.AsyncDataLoader;
import com.speaktool.bean.PicDataHolder;
import com.speaktool.impl.dataloader.CourseThumbnailAsyncLoader;
import com.speaktool.impl.dataloader.LocalPhotoDirLoader;
import com.speaktool.impl.dataloader.LocalPicturesIconAsyncLoader;
import com.speaktool.impl.dataloader.NetPicturesIconAsyncLoader;
import com.speaktool.impl.dataloader.ThirdpartyItemLoader;

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
		return new ThirdpartyItemLoader(SpeakToolApp.app());
	}
}
