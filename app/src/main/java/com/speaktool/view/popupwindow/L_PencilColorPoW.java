package com.speaktool.view.popupwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.speaktool.R;
import com.speaktool.bean.PaintInfoBean;
import com.speaktool.impl.paint.DrawPaint;
import com.speaktool.tasks.TaskLoadPaintColors;
import com.speaktool.tasks.TaskLoadPaintColors.Callback;
import com.speaktool.ui.adapters.AdapterColors;
import com.speaktool.view.layouts.StrokeWidthPreview;
import com.speaktool.utils.DensityUtils;

import java.util.List;

/**
 * 左侧功能栏——更改画笔颜色和粗细
 *
 * @author shaoshuai
 */
public class L_PencilColorPoW extends BasePopupWindow implements OnItemClickListener, OnSeekBarChangeListener {
    private GridView gridViewColors;// 选择颜色
    private StrokeWidthPreview strokeWidthPreview;// 示例图
    private SeekBar seekBarAdjustStrokeWidth;// 改变画笔粗细

    private AdapterColors adapter;
    private View mPenView;

    @Override
    public View getContentView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pow_selectcolorclick, null);
        return view;
    }

    public L_PencilColorPoW(Context context, View anchor) {
        this(context, anchor, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public L_PencilColorPoW(Context context, View anchor, int w, int h) {
        super(context, anchor, w, h);
        this.mPenView = anchor;

        gridViewColors = (GridView) mRootView.findViewById(R.id.gridViewColors);
        strokeWidthPreview = (StrokeWidthPreview) mRootView.findViewById(R.id.strokeWidthPreview);
        seekBarAdjustStrokeWidth = (SeekBar) mRootView.findViewById(R.id.seekBarAdjustStrokeWidth);

        seekBarAdjustStrokeWidth.setOnSeekBarChangeListener(this);

        /** dip==progress */
        int progress = (int) DensityUtils.px2dp(mContext, DrawPaint.getGlobalPaintInfo().getStrokeWidth());
        seekBarAdjustStrokeWidth.setProgress(progress);
        //
        gridViewColors.setOnItemClickListener(this);
        adapter = new AdapterColors(context, null);
        gridViewColors.setAdapter(adapter);
        // 填充颜色
        new Thread(new TaskLoadPaintColors(new Callback() {
            @Override
            public void onLoaded(List<PaintInfoBean> colors) {
                adapter.refresh(colors);
            }
        })).start();
    }

    private void onDestory() {
        this.dismiss();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        PaintInfoBean bean = (PaintInfoBean) parent.getAdapter().getItem(position);
        DrawPaint.getGlobalPaintInfo().setColor(bean.getColor());
        DrawPaint.getGlobalPaintInfo().setIconResId(bean.getIconResId());
        DrawPaint.getGlobalPaintInfo().setIconResIdSelected(bean.getIconResIdSelected());

        ((ImageView) mPenView).setImageResource(bean.getIconResIdSelected());
        onDestory();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int strokeWidthPix = DensityUtils.dp2px(mContext, progress);
        strokeWidthPreview.preview(strokeWidthPix, DrawPaint.getGlobalPaintInfo().getColor());
        DrawPaint.getGlobalPaintInfo().setStrokeWidth(strokeWidthPix);// gloabl.
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

}
