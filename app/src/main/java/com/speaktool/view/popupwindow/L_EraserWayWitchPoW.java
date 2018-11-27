package com.speaktool.view.popupwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.speaktool.R;
import com.speaktool.impl.paint.EraserPaint;
import com.speaktool.tasks.MyColors;
import com.speaktool.utils.DensityUtils;
import com.speaktool.view.layouts.StrokeWidthPreview;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 左侧——橡皮擦——更改擦除路径宽度
 *
 * @author shaoshuai
 */
public class L_EraserWayWitchPoW extends BasePopupWindow {
    @BindView(R.id.strokeWidthPreview) StrokeWidthPreview strokeWidthPreview;
    @BindView(R.id.sb_StrokeWidth) SeekBar seekBarAdjustStrokeWidth;

    @Override
    public View getContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.pow_eraserclick, null);
        ButterKnife.bind(this, view);
        return view;
    }

    public L_EraserWayWitchPoW(Context context, View anchor) {
        super(context, anchor);

        /** dip==progress */
        int pxPro = EraserPaint.getGlobalPaintInfo().getStrokeWidth();
        strokeWidthPreview.preview(pxPro, MyColors.WHITE);// 实例画线

        int progress = (int) DensityUtils.px2dp(mContext, pxPro);
        seekBarAdjustStrokeWidth.setProgress(progress);
        seekBarAdjustStrokeWidth.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int strokeWidthPix = DensityUtils.dp2px(mContext, progress);
                strokeWidthPreview.preview(strokeWidthPix, MyColors.WHITE);
                EraserPaint.getGlobalPaintInfo().setStrokeWidth(strokeWidthPix);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


}
