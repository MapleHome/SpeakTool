package com.speaktool.view.popupwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.speaktool.R;
import com.speaktool.api.Draw;
import com.speaktool.utils.T;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 右侧功能栏——重录界面
 *
 * @author shaoshuai
 */
public class R_RerecordPoW extends BasePopupWindow {
    private Draw mDraw;

    @Override
    public View getContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.pow_rerecordclick, null);
        ButterKnife.bind(this, view);
        return view;
    }

    public R_RerecordPoW(Context context, View anchor, Draw draw) {
        super(context, anchor);
        mDraw = draw;
    }


    // 重录全部
    @OnClick(R.id.tvReRecordAll)
    void rerecordAll() {
        dismiss();
//        if (mDraw.getPageRecorder().isHaveRecordForAll()) {
//            mDraw.preChangePage(new Runnable() {
//                @Override
//                public void run() {
//                    mDraw.getPageRecorder().reRecordAll();
//                    mDraw.setActivePageSendcmd(mDraw.getPageAtPosition(0).getPageID());
//                }
//            });
//        } else {
//            T.showShort(mContext, "还没有录像！");
//        }
    }

    // 重录本页
    @OnClick(R.id.tvReRecordPage)
    void rerecordPage() {
        dismiss();
//        final int pageId = mDraw.getCurrentBoard().getPageID();// 当前画纸ID
//        if (mDraw.getPageRecorder().isHaveRecordForPage(pageId)) {
//            mDraw.preChangePage(new Runnable() {
//                @Override
//                public void run() {
//                    mDraw.getPageRecorder().reRecordPage(pageId);
//                }
//            });
//        } else {
//            T.showShort(mContext, "本页还没有录像！");
//        }
    }

}
