package com.speaktool.view.popupwindow;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.speaktool.R;
import com.speaktool.api.PhotoImporter.PickPhotoCallback;
import com.speaktool.view.layouts.MulticheckableView;

import java.util.ArrayList;
import java.util.List;

/**
 * 挑选多张照片
 *
 * @author shaoshuai
 */
public class L_M_AddBatchPhotosPoW extends L_M_AddSinglePhotosPoW {

    public L_M_AddBatchPhotosPoW(Context context, View anchor, PickPhotoCallback draw) {
        super(context, anchor, draw);
        tvSecondPageFinish.setVisibility(View.VISIBLE);
    }

    @Override
    protected OnItemClickListener getOnGridItemClickListener() {
        return new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MulticheckableView multiview = (MulticheckableView) view;
                if (multiview.ischecked()) {
                    multiview.uncheck();
                    mAdapterPhotos.getCheckedList().remove(Integer.valueOf(position));
                } else {
                    multiview.check();
                    mAdapterPhotos.getCheckedList().add(position);
                }
            }
        };
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.llBack:
                mAdapterPhotos.getCheckedList().clear();
                super.onClick(arg0);
                break;
            case R.id.tvSecondPageFinish:
                dismiss();
                onOkClicked();
                break;
        }
    }

    private void onOkClicked() {
        if (mAdapterPhotos.getCheckedList().isEmpty())
            return;
        List<String> checkedImage = new ArrayList<>();
        for (int i = 0; i < mAdapterPhotos.getCheckedList().size(); i++) {
            int position = mAdapterPhotos.getCheckedList().get(i);
            checkedImage.add((String) mAdapterPhotos.getItem(position));
        }
        mDraw.onPhotoPicked(checkedImage);
    }
}
