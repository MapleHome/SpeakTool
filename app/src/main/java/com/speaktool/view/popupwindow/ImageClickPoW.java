package com.speaktool.view.popupwindow;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.speaktool.R;
import com.speaktool.api.FocusedView;
import com.speaktool.busevents.CloseEditPopupWindowEvent;
import com.speaktool.impl.shapes.ImageWidget;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
 * 图片编辑框
 *
 * @author shaoshuai
 */
public class ImageClickPoW extends BasePopupWindow implements OnClickListener {
    private ImageWidget mImage;

    @Override
    public View getContentView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.pow_imageclick, null);
    }


    public ImageClickPoW(Context context, ImageWidget edit) {
        super(context, edit.getPage().view());
        mImage = edit;
        EventBus.getDefault().register(this);

        mContentView.findViewById(R.id.imgtv_delete).setOnClickListener(this);
        mContentView.findViewById(R.id.imgtv_copy).setOnClickListener(this);
        mContentView.findViewById(R.id.imgtv_rotate).setOnClickListener(this);
        mContentView.findViewById(R.id.imgtv_widthAutoFit).setOnClickListener(this);
        mContentView.findViewById(R.id.imgtv_heightAutoFit).setOnClickListener(this);
        mContentView.findViewById(R.id.imgtv_lock).setOnClickListener(this);

        mPopupWindow.setFocusable(false);// 是否具有获取焦点的能力
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(false);// 外部触摸

        mContentView.setFocusable(true);
        mContentView.setFocusableInTouchMode(true);
        mContentView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU) {
                    if (isShow) {
                        onDestory();
                        isShow = false;
                    } else {
                        isShow = true;
                    }
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgtv_delete:
                mImage.delete();
                onDestory();
                break;
            case R.id.imgtv_copy:
                onDestory();
                mImage.copy();
                break;
            case R.id.imgtv_rotate:
                mImage.rotate();
                break;
            case R.id.imgtv_widthAutoFit:
                mImage.widthAutoFit();
                break;
            case R.id.imgtv_heightAutoFit:
                mImage.heightAutoFit();
                break;
            case R.id.imgtv_lock:
                mImage.switchLock();
                onDestory();
                break;
        }
    }

    @Subscribe
    public void onEventMainThread(CloseEditPopupWindowEvent event) {
        onDestory();
    }

    private void onDestory() {
        ((FocusedView) mImage).exitFocus();
        EventBus.getDefault().unregister(this);
        dismiss();
    }

}
