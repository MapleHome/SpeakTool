package com.speaktool.ui.popupwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.speaktool.R;
import com.speaktool.impl.paint.EraserPaint;
import com.speaktool.tasks.MyColors;
import com.speaktool.ui.base.BasePopupWindow;
import com.speaktool.ui.layouts.StrokeWidthPreview;
import com.speaktool.utils.DensityUtils;

/**
 * 左侧——橡皮擦——更改擦除路径宽度
 *
 * @author shaoshuai
 */
public class L_EraserWayWitchPoW extends BasePopupWindow implements OnSeekBarChangeListener {
    private StrokeWidthPreview strokeWidthPreview;
    private SeekBar seekBarAdjustStrokeWidth;

    @Override
    public View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.pow_eraserclick, null);
    }

    public L_EraserWayWitchPoW(Context context, View anchor) {
        this(context, anchor, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public L_EraserWayWitchPoW(Context context, View anchor, int w, int h) {
        super(context, anchor, w, h);

        strokeWidthPreview = (StrokeWidthPreview) mRootView.findViewById(R.id.strokeWidthPreview);
        seekBarAdjustStrokeWidth = (SeekBar) mRootView.findViewById(R.id.seekBarAdjustStrokeWidth);
        /** dip==progress */
        int pxPro = EraserPaint.getGlobalPaintInfo().getStrokeWidth();
        strokeWidthPreview.preview(pxPro, MyColors.WHITE);// 实例画线

        int progress = (int) DensityUtils.px2dp(mContext, pxPro);
        seekBarAdjustStrokeWidth.setProgress(progress);
        seekBarAdjustStrokeWidth.setOnSeekBarChangeListener(this);

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int strokeWidthPix = DensityUtils.dp2px(mContext, progress);
        strokeWidthPreview.preview(strokeWidthPix, MyColors.WHITE);
        EraserPaint.getGlobalPaintInfo().setStrokeWidth(strokeWidthPix);// gloabl.
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
