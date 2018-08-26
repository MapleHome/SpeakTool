package com.speaktool.ui.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.speaktool.R;
import com.speaktool.utils.FormatUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 视频播放控制器视图
 *
 * @author Maple Shao
 */
public class VideoPlayControllerView extends FrameLayout {
    @BindView(R.id.tvProgress) TextView tvProgress;// 开始时间
    @BindView(R.id.pbProgress) SeekBar pbProgress;// 进度条
    @BindView(R.id.tvTotalDuration) TextView tvTotalDuration;// 总时间

    public VideoPlayControllerView(Context context) {
        super(context);
        init();
    }

    public VideoPlayControllerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPlayControllerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    private void init() {
        View view = View.inflate(getContext(), R.layout.videoplay_controller_layout_new, this);
        ButterKnife.bind(this, view);

        setMaxProgress(1000);
    }

    public void setProgressText(long progress) {
        String curPro = FormatUtils.getFormatTimeSimple(progress);
        tvProgress.setText(curPro);
    }

    public void setTotalDuration(long totalDurationText) {
        String totalPro = FormatUtils.getFormatTimeSimple(totalDurationText);
        tvTotalDuration.setText(totalPro);
    }

    /**
     * 更新进度
     *
     * @param progress 当前进度
     */
    public void setProgress(int progress) {
        pbProgress.setProgress(progress);
    }

    public void setMaxProgress(int maxProgress) {
        pbProgress.setMax(maxProgress);
    }

    /**
     * 进度改变监听
     *
     * @param lsn
     */
    public void setSeekListener(OnSeekBarChangeListener lsn) {
        pbProgress.setOnSeekBarChangeListener(lsn);
    }
}
