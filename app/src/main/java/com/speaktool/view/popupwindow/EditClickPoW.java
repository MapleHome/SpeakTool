package com.speaktool.view.popupwindow;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.speaktool.R;
import com.speaktool.api.FocusedView;
import com.speaktool.busevents.CloseEditPopupWindowEvent;
import com.speaktool.impl.shapes.EditWidget;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 文本编辑框
 *
 * @author shaoshuai
 */
public class EditClickPoW extends BasePopupWindow {
    private EditWidget mWordEdit;

    @Override
    public View getContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.pow_edit_bar, null);
        ButterKnife.bind(this, view);
        return view;
    }

    public EditClickPoW(Context context, EditWidget edit) {
        super(context, edit.getPage().view());
        EventBus.getDefault().register(this);
        mWordEdit = edit;

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
                        onDestroy();
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

    @OnClick(R.id.imgtv_delete)
    void onClickDelete() {
        mWordEdit.delete();
        onDestroy();
    }

    @OnClick(R.id.imgtv_copy)
    void onClickCopy() {
        onDestroy();
        mWordEdit.copy();
    }

    @OnClick(R.id.imgtv_edit)
    void onClickEdit() {
        onDestroy();
        mWordEdit.intoEdit(false);
    }

    @OnClick(R.id.imgtv_scaleBig)
    void onScaleBig() {
        mWordEdit.scaleBig();
    }

    @OnClick(R.id.imgtv_scaleSmall)
    void onScaleSmall() {
        mWordEdit.scaleSmall();
    }

    @OnClick(R.id.imgtv_color)
    void showColorWindow(View view) {
        new PickFontColorsPoW(mContext, parentView, view, mWordEdit)
                .showPopupWindow(WeiZhi.Top);
    }

    @OnClick(R.id.imgtv_lock)
    void onSwitchLock() {
        mWordEdit.switchLock();
        onDestroy();
    }

    @Subscribe
    public void onEventMainThread(CloseEditPopupWindowEvent event) {
        onDestroy();
    }

    private void onDestroy() {
        ((FocusedView) mWordEdit).exitFocus();
        EventBus.getDefault().unregister(this);
        dismiss();
    }

}
