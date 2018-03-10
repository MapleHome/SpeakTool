package com.speektool.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.google.common.base.Preconditions;
import com.speektool.R;
import com.speektool.SpeekToolApp;
import com.speektool.adapters.AdapterThirdpartyList;
import com.speektool.api.AsyncDataLoader;
import com.speektool.base.AbsListScrollListener;
import com.speektool.bean.ThirdParty;
import com.speektool.busevents.ThirdpartyLoadEvent;
import com.speektool.factory.AsyncDataLoaderFactory;
import com.speektool.ui.layouts.ItemViewThirdparty;
import com.speektool.utils.DensityUtils;
import com.speektool.utils.DisplayUtil;

import java.util.List;

import de.greenrobot.event.EventBus;

public class ThirdpartyListDialog extends Dialog implements View.OnClickListener, OnDismissListener {

    private Button btCancel;
    private ListView lvThirdpartyList;
    private AdapterThirdpartyList mAdapterThirdpartyList;
    private final AsyncDataLoader<Object, Bitmap> mThirdpartyItemLoader = AsyncDataLoaderFactory
            .newThirdpartyItemLoader();

    public ThirdpartyListDialog(Context context) {
        this(context, R.style.dialogTheme);
    }

    public ThirdpartyListDialog(Context context, int theme) {
        super(context, theme);
        Preconditions.checkArgument(context instanceof Activity, "context must be Activity in Dialog.");
        this.setCanceledOnTouchOutside(true);

        this.setOnDismissListener(this);

    }

    private static final int THIRD_PARTYLIST_DIALOG__WIDTH = DensityUtils.dp2px(SpeekToolApp.app(), 300);// pix
    private static final int THIRD_PARTYLIST_DIALOG_HEIGHT = DensityUtils.dp2px(SpeekToolApp.app(), 350);// pixv

    private static Point getDialogSize(Context context) {
        Point screen = DisplayUtil.getScreenSize(context);
        int stH = DisplayUtil.getStatusbarHeightPix(context);
        int w = screen.x > THIRD_PARTYLIST_DIALOG__WIDTH ? THIRD_PARTYLIST_DIALOG__WIDTH : screen.x - stH;
        int h = screen.y > THIRD_PARTYLIST_DIALOG_HEIGHT ? THIRD_PARTYLIST_DIALOG_HEIGHT : screen.y - stH;
        return new Point(w, h);
    }

    /**
     * called when show.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_thirdpartylist);
        View rootLay = findViewById(R.id.rootShareListDialog);
        // reset dialog size.
        Point sz = getDialogSize(getContext());
        int w = sz.x;
        int h = sz.y;
        ViewGroup.LayoutParams lp1 = rootLay.getLayoutParams();
        lp1.height = h;
        lp1.width = w;
        rootLay.setLayoutParams(lp1);
        btCancel = (Button) findViewById(R.id.btCancel);
        lvThirdpartyList = (ListView) findViewById(R.id.lvThirdpartyList);
        mAdapterThirdpartyList = new AdapterThirdpartyList(getContext(), null);
        lvThirdpartyList.setAdapter(mAdapterThirdpartyList);
        btCancel.setOnClickListener(this);
        lvThirdpartyList.setOnScrollListener(mThirdpartyOnScrollListener);

    }

    public void refreshListData(List<ThirdParty> datas) {
        mAdapterThirdpartyList.refresh(datas);
        SpeekToolApp.getUiHandler().post(new Runnable() {
            @Override
            public void run() {
                int first = lvThirdpartyList.getFirstVisiblePosition();
                int last = lvThirdpartyList.getLastVisiblePosition();

                mThirdpartyOnScrollListener.setVisibleItems(first, last);
                mThirdpartyOnScrollListener.whenIdle();
            }
        });
    }

    public void setListItemClickListener(OnItemClickListener listener) {
        lvThirdpartyList.setOnItemClickListener(listener);
    }

    public void onEventMainThread(ThirdpartyLoadEvent event) {
        ItemViewThirdparty item = (ItemViewThirdparty) lvThirdpartyList.findViewWithTag(event.getKey());
        if (item != null) {
            item.setLogo(event.getVal());
        }
    }

    @Override
    public void onClick(View v) {
        this.dismiss();
    }

    private AbsListScrollListener mThirdpartyOnScrollListener = new AbsListScrollListener() {
        @Override
        public void whenIdle() {
            final int itemcount = lvThirdpartyList.getAdapter().getCount() - 1;
            final int min = Math.min(itemcount, mlastvisibleItem);// bug

            for (int i = mfirstVisibleItem; i <= min; i++) {
                ThirdParty bean = (ThirdParty) lvThirdpartyList.getAdapter().getItem(i);
                ItemViewThirdparty item = null; // indexoutof.

                if (bean.getIconType() == ThirdParty.ICON_TYPE_NET) {
                    item = (ItemViewThirdparty) lvThirdpartyList.findViewWithTag(bean.getIconUrl());
                } else if (bean.getIconType() == ThirdParty.ICON_TYPE_RES) {
                    item = (ItemViewThirdparty) lvThirdpartyList.findViewWithTag(bean.getIconResId());
                }
                if (item == null)
                    continue;
                Bitmap cache = null;
                if (bean.getIconType() == ThirdParty.ICON_TYPE_NET) {
                    cache = mThirdpartyItemLoader.load(bean.getIconUrl(), bean.getIconType());
                } else if (bean.getIconType() == ThirdParty.ICON_TYPE_RES) {
                    cache = mThirdpartyItemLoader.load(bean.getIconResId(), bean.getIconType());
                }
                //
                if (cache != null) {
                    item.setLogo(cache);
                } else {
                    item.setLogo(mAdapterThirdpartyList.getDefBmp());
                }
            }
        }

        @Override
        public void whenFling() {
            mThirdpartyItemLoader.cancelAll();
        }
    };

    @Override
    public void onDismiss(DialogInterface dialog) {
        mThirdpartyItemLoader.destroy();
        EventBus.getDefault().unregister(this);
    }
}
