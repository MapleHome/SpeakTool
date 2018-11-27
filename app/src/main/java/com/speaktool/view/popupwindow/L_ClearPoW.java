package com.speaktool.view.popupwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.speaktool.R;
import com.speaktool.api.Draw;
import com.speaktool.bean.ClearPageData;
import com.speaktool.ui.Draw.DrawActivity;
import com.speaktool.utils.T;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 顶部功能栏——更多功能——清除本页内容
 *
 * @author shaoshuai
 */
public class L_ClearPoW extends BasePopupWindow {
    private DrawActivity drawActivity;
    private Draw mDraw;
    int pageId;

    @Override
    public View getContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.pow_clearclick, null);
        ButterKnife.bind(this, view);
        return view;
    }

    public L_ClearPoW(Context context, View anchor, Draw draw, DrawActivity drawAct) {
        super(context, anchor);
        mDraw = draw;
        drawActivity = drawAct;
        pageId = mDraw.getCurrentBoard().getPageID();
    }


    // 删除界面
    @OnClick(R.id.tvDelPage)
    void onDeletePage() {
        dismiss();
        drawActivity.deletePager();
    }

    // 清除本页所有内容
    @OnClick(R.id.tvClearPagePenAndContents)
    void onClearPagePenAndContents() {
        dismiss();
        mDraw.clearPageClick(pageId, ClearPageData.OPT_CLEAR_ALL);
    }

    // 清除本页绘画笔记
    @OnClick(R.id.tvClearPagePen)
    void onClearPagePen() {
        dismiss();
        mDraw.clearPageClick(pageId, ClearPageData.OPT_CLEAR_NOTES);
    }

    // 清除本页录音
    @OnClick(R.id.tvClearPageRecords)
    void onClearPageRecords() {
        dismiss();
        if (mDraw.getPageRecorder().isHaveRecordForPage(pageId)) {
            mDraw.preChangePage(new Runnable() {
                @Override
                public void run() {
                    mDraw.getPageRecorder().deletePageRecord(pageId);
                }
            });
        } else {
            T.showShort(mContext, "本页还没有录像！");
        }
    }

}
