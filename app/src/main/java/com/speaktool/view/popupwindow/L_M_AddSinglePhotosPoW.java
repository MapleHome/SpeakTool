package com.speaktool.view.popupwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.speaktool.R;
import com.speaktool.api.PhotoImporter.PickPhotoCallback;
import com.speaktool.bean.LocalPhotoDirBean;
import com.speaktool.ui.adapters.AdapterPhotoDirs;
import com.speaktool.ui.adapters.AdapterPhotos;

import org.greenrobot.eventbus.EventBus;


/**
 * 左侧功能栏——更多——添加图片——挑选单张照片
 *
 * @author shaoshuai
 */
public class L_M_AddSinglePhotosPoW extends BasePopupWindow implements OnClickListener,
        OnDismissListener, OnTouchListener {
    private ViewFlipper viewFlipperPhotos;

    protected PickPhotoCallback mDraw;
    private ListView listViewPhotoDirs;
    private GridView gridViewPhotos;
    private View llBack;
    private TextView tvSecondPageTitle;

    protected TextView tvSecondPageFinish;
    private View llBackOverlay;
    private View llFinishOverlay;
    protected AdapterPhotos mAdapterPhotos;

    private static final int FIRST_PAGE = 0;
    private static final int SECOND_PAGE = 1;

    @Override
    public View getContentView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.pow_pickphotos, null);
    }

    public L_M_AddSinglePhotosPoW(Context context, View anchor, PickPhotoCallback draw) {
        super(context, anchor);
        EventBus.getDefault().register(this);

        mDraw = draw;

        viewFlipperPhotos = (ViewFlipper) mContentView.findViewById(R.id.viewFlipperPhotos);
        listViewPhotoDirs = (ListView) mContentView.findViewById(R.id.listViewPhotoDirs);

        // 第二页
        llBack = mContentView.findViewById(R.id.llBack);// 返回
        gridViewPhotos = (GridView) mContentView.findViewById(R.id.gridViewPhotos);
        llBackOverlay = mContentView.findViewById(R.id.llBackOverlay);
        tvSecondPageTitle = (TextView) mContentView.findViewById(R.id.tvSecondPageTitle);
        tvSecondPageFinish = (TextView) mContentView.findViewById(R.id.tvSecondPageFinish);

        llFinishOverlay = mContentView.findViewById(R.id.llFinishOverlay);

        initAnim(anchor.getContext());

        AdapterPhotoDirs mAdapterPhotoDirs = new AdapterPhotoDirs(mContext, null);
        listViewPhotoDirs.setAdapter(mAdapterPhotoDirs);

        mAdapterPhotos = new AdapterPhotos(anchor.getContext(), null);
        gridViewPhotos.setAdapter(mAdapterPhotos);

        setOnDismissListener(this);
        listViewPhotoDirs.setOnItemClickListener(listItemClickListener);
        gridViewPhotos.setOnItemClickListener(getOnGridItemClickListener());
        llBack.setOnClickListener(this);
        llBack.setOnTouchListener(this);
        tvSecondPageFinish.setOnClickListener(this);
        tvSecondPageFinish.setOnTouchListener(this);

    }


    private Animation intoAnim_in;
    private Animation intoAnim_out;
    private Animation backAnim_in;
    private Animation backAnim_out;

    private void initAnim(Context context) {
        intoAnim_in = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        intoAnim_out = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right);
        backAnim_in = AnimationUtils.loadAnimation(context, R.anim.viewfliper_slide_in_right);
        backAnim_out = AnimationUtils.loadAnimation(context, R.anim.viewfliper_slide_out_left);
    }

    private void setIntoAnimation() {
        viewFlipperPhotos.setInAnimation(backAnim_in);
        viewFlipperPhotos.setOutAnimation(backAnim_out);
    }

    private void setBackAnimation() {
        viewFlipperPhotos.setInAnimation(intoAnim_in);
        viewFlipperPhotos.setOutAnimation(intoAnim_out);
    }

    protected OnItemClickListener getOnGridItemClickListener() {
        return gridItemClickListener;
    }

    private OnItemClickListener gridItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String imgPath = (String) parent.getAdapter().getItem(position);
            mDraw.onPhotoPicked(imgPath);
            //
            dismiss();
        }
    };

    private OnItemClickListener listItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            LocalPhotoDirBean item = (LocalPhotoDirBean) parent.getAdapter().getItem(position);
            setIntoAnimation();
            viewFlipperPhotos.setDisplayedChild(SECOND_PAGE);

            tvSecondPageTitle.setText(item.getDirName());
            mAdapterPhotos = new AdapterPhotos(mContext, item.getImagePathList());
            gridViewPhotos.setAdapter(mAdapterPhotos);
        }
    };

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.llBack:
                setBackAnimation();
                viewFlipperPhotos.setDisplayedChild(FIRST_PAGE);
                break;
        }
    }

    @Override
    public void onDismiss() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                down(v);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                up(v);
                break;
        }
        return false;
    }

    private void up(View v) {
        if (v == llBack)
            llBackOverlay.setVisibility(View.GONE);
        else if (v == tvSecondPageFinish) {
            llFinishOverlay.setVisibility(View.GONE);
        }
    }

    private void down(View v) {
        if (v == llBack)
            llBackOverlay.setVisibility(View.VISIBLE);
        else if (v == tvSecondPageFinish) {
            llFinishOverlay.setVisibility(View.VISIBLE);
        }
    }

}
