package com.speaktool.view.popupwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.speaktool.R;
import com.speaktool.impl.api.Draw;
import com.speaktool.impl.bean.CopyPageData;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 右侧功能栏——添加新页面
 *
 * @author shaoshuai
 */
public class R_AddNewPagePoW extends BasePopupWindow {
    private Draw mDraw;

    @Override
    public View getContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.pow_new_page, null);
        ButterKnife.bind(this, view);
        return view;
    }

    public R_AddNewPagePoW(Context context, View anchor, Draw draw) {
        super(context, anchor);
        mDraw = draw;
    }

    // 新建空白页
    @OnClick(R.id.tvNewEmptyPage)
    void newEmptyPage() {
        dismiss();
        mDraw.preChangePage(new Runnable() {
            @Override
            public void run() {
                mDraw.newEmptyBoardClick();
            }
        });
    }

    // 复制本页不含笔记
    @OnClick(R.id.tvCopyPageJustViews)
    void tvCopyPageJustViews() {
        dismiss();
        mDraw.preChangePage(new Runnable() {
            @Override
            public void run() {
                mDraw.copyPageClick(CopyPageData.OPT_COPY_VIEWS);
            }
        });
    }

    // 复制本页包含笔记
    @OnClick(R.id.tvCopyPageAll)
    void tvCopyPageAll() {
        dismiss();
        mDraw.preChangePage(new Runnable() {
            @Override
            public void run() {
                mDraw.copyPageClick(CopyPageData.OPT_COPY_ALL);
            }
        });
    }

}
