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
    public View getContentView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.pow_add_image, null);
    }

    public L_M_AddImgPoW(Context context, View anchor, PhotoImporter photoImporter, PickPhotoCallback callback) {
        super(context, anchor);
        mPhotoImporter = photoImporter;
        mPickPhotoCallback = callback;

        tvImportFromCamera = mContentView.findViewById(R.id.tvImportFromCamera);// 拍照
        tvImportFromCamera.setOnClickListener(this);
        tvImportFromAlbum = mContentView.findViewById(R.id.tvImportFromAlbum);// 从相册选取
        tvImportFromAlbum.setOnClickListener(this);
        tvImportFromNet = mContentView.findViewById(R.id.tvImportFromNet);// 获取网络图片
        tvImportFromNet.setOnClickListener(this);
        tvImportBatch = mContentView.findViewById(R.id.tvImportBatch);// 批量导入图片
        tvImportBatch.setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.tvImportFromCamera:// 拍照
                dismiss();
                mPhotoImporter.getImageFromCamera(mContentView, mPickPhotoCallback);
                break;
            case R.id.tvImportFromAlbum:// 从相册选取
                dismiss();
                mPhotoImporter.getImageFromAlbum(mContentView, mPickPhotoCallback);
                break;
            case R.id.tvImportFromNet:// 获取网络图片
                dismiss();
                mPhotoImporter.getImageFromNet(mContentView, mPickPhotoCallback);
                break;
            case R.id.tvImportBatch:// 批量导入图片
                dismiss();
                mPhotoImporter.importImageBatch(mContentView, mPickPhotoCallback);
                break;
        }
    }


}
