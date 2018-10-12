package com.speaktool.ui.base;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public abstract class AbsListScrollListener implements OnScrollListener {

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case SCROLL_STATE_IDLE:
                whenIdle();
                break;
            default:
                whenFling();
                break;
        }
    }

    public abstract void whenFling();

    public abstract void whenIdle();

    protected int mfirstVisibleItem;
    protected int mlastvisibleItem;

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        setVisibleItems(firstVisibleItem, firstVisibleItem + visibleItemCount - 1);
    }

    public final void setVisibleItems(int first, int last) {
        mfirstVisibleItem = first;
        mlastvisibleItem = last;
    }
}
