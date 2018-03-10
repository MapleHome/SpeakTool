package com.speektool.api;

import java.util.List;

import android.view.View;

public interface PhotoImporter {

	public static interface PickPhotoCallback {
		/** 单张图片路径 */
		void onPhotoPicked(String imgPath);

		/** 多选图片路径 */
		void onPhotoPicked(List<String> multiPickImgPaths);
	}

	/** 获取相机拍照 */
	void getImageFromCamera(View anchor, PickPhotoCallback callback);

	/** 获取相册照片 */
	void getImageFromAlbum(View anchor, PickPhotoCallback callback);

	/** 获取网络图片 */
	void getImageFromNet(View anchor, PickPhotoCallback callback);

	/** 批量导入图片 */
	void importImageBatch(View anchor, PickPhotoCallback callback);
}
