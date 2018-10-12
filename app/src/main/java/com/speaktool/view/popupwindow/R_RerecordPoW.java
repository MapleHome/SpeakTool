package com.speaktool.view.popupwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.speaktool.R;
import com.speaktool.api.Draw;
import com.speaktool.utils.T;

/**
 * 右侧功能栏——重录界面
 *
 * @author shaoshuai
 */
public class R_RerecordPoW extends BasePopupWindow implements OnClickListener {

    private Draw mDraw;

    @Override
    public View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.pow_rerecordclick, null);
    }

    public R_RerecordPoW(Context context, View anchor, Draw draw) {
        this(context, anchor, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, draw);
    }

    public R_RerecordPoW(Context context, View anchor, int w, int h, Draw draw) {
        super(context, anchor, w, h);
        mDraw = draw;

        mRootView.findViewById(R.id.tvReRecordPage).setOnClickListener(this);
        mRootView.findViewById(R.id.tvReRecordAll).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvReRecordPage:// 重录本页
                final int pageId = mDraw.getCurrentBoard().getPageID();// 当前画纸ID
                if (!mDraw.getPageRecorder().isHaveRecordForPage(pageId)) {
                    T.showShort(mContext, "本页还没有录像！");
                    break;
                }
                dismiss();
                mDraw.preChangePage(new Runnable() {
                    @Override
                    public void run() {
                        mDraw.getPageRecorder().reRecordPage(pageId);
                    }
                });
                break;
            case R.id.tvReRecordAll:// 重录全部
                if (!mDraw.getPageRecorder().isHaveRecordForAll()) {
                    T.showShort(mContext, "还没有录像！");
                    break;
                }
                dismiss();
                mDraw.preChangePage(new Runnable() {
                    @Override
                    public void run() {
                        mDraw.getPageRecorder().reRecordAll();
                        mDraw.setActivePageSendcmd(mDraw.getPageAtPosition(0).getPageID());
                    }
                });
                break;
        }
    }

}
