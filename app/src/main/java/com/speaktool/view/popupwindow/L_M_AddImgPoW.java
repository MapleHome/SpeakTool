package com.speaktool.view.popupwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.speaktool.R;
import com.speaktool.api.PhotoImporter;
import com.speaktool.api.PhotoImporter.PickPhotoCallback;

/**
 * 左侧功能栏——更多功能——添加图片窗体
 *
 * @author shaoshuai
 */
public class L_M_AddImgPoW extends BasePopupWindow implements OnClickListener {

    protected View tvImportFromCamera;// 拍照
    private View tvImportFromAlbum;// 相册
    protected View tvImportFromNet;// 网络
    protected View tvImportBatch;// 批量

    private PhotoImporter mPhotoImporter;
    private PickPhotoCallback mPickPhotoCallback;

    @Override
    public View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.pow_add_image, null);
    }

    public L_M_AddImgPoW(Context context, View anchor, PhotoImporter photoImporter, PickPhotoCallback callback) {
        this(context, anchor, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, photoImporter,
                callback);
    }

    public L_M_AddImgPoW(Context context, View anchor, int w, int h, PhotoImporter photoImporter,
                         PickPhotoCallback callback) {
        super(context, anchor, w, h);
        mPhotoImporter = photoImporter;
        mPickPhotoCallback = callback;

        tvImportFromCamera = mRootView.findViewById(R.id.tvImportFromCamera);// 拍照
        tvImportFromCamera.setOnClickListener(this);
        tvImportFromAlbum = mRootView.findViewById(R.id.tvImportFromAlbum);// 从相册选取
        tvImportFromAlbum.setOnClickListener(this);
        tvImportFromNet = mRootView.findViewById(R.id.tvImportFromNet);// 获取网络图片
        tvImportFromNet.setOnClickListener(this);
        tvImportBatch = mRootView.findViewById(R.id.tvImportBatch);// 批量导入图片
        tvImportBatch.setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.tvImportFromCamera:// 拍照
                onDestory();
                mPhotoImporter.getImageFromCamera(mRootView, mPickPhotoCallback);
                break;
            case R.id.tvImportFromAlbum:// 从相册选取
                onDestory();
                mPhotoImporter.getImageFromAlbum(mRootView, mPickPhotoCallback);
                break;
            case R.id.tvImportFromNet:// 获取网络图片
                onDestory();
                mPhotoImporter.getImageFromNet(mRootView, mPickPhotoCallback);
                break;
            case R.id.tvImportBatch:// 批量导入图片
                onDestory();
                mPhotoImporter.importImageBatch(mRootView, mPickPhotoCallback);
                break;
        }
    }

    private void onDestory() {
        this.dismiss();
    }

}
