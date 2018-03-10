package com.speektool.ui.layouts;

import roboguice.inject.InjectView;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.speektool.R;
import com.speektool.injectmodules.IInject;
import com.speektool.injectmodules.Layout;
import com.speektool.injectmodules.ViewInjectUtils;

/**
 * 视频播放控制器视图
 * 
 * @author Maple Shao
 * 
 */
@Layout(R.layout.videoplay_controller_layout_new)
public class VideoPlayControllerView extends FrameLayout implements IInject {
	@InjectView(R.id.ivPlayPause)
	private ImageView ivPlayPause;// 播放暂停
	@InjectView(R.id.tvProgress)
	private TextView tvProgress;// 开始时间
	@InjectView(R.id.pbProgress)
	private SeekBar pbProgress;// 进度条
	@InjectView(R.id.tvTotalDuration)
	private TextView tvTotalDuration;// 总时间

	public VideoPlayControllerView(Context context) {
		super(context);
		init();
	}

	public VideoPlayControllerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public VideoPlayControllerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		startInject();
		afterInject();
	}

	@Override
	public void startInject() {
		ViewInjectUtils.injectViews(this);
	}

	@Override
	public void afterInject() {

	}

	/**
	 * 更新进度
	 * 
	 * @param progress
	 *            当前进度
	 */
	public void setProgress(int progress) {
		pbProgress.setProgress(progress);
	}

	public void setProgressText(String progress) {
		tvProgress.setText(progress);
	}

	public void setTotalDuration(String totalDurationText) {
		tvTotalDuration.setText(totalDurationText);
	}

	public void setPlayPauseIcon(int resid) {
		ivPlayPause.setImageResource(resid);
	}

	/**
	 * 播放暂停监听
	 * 
	 * @param l
	 */
	public void setPlayPauseClickListener(OnClickListener l) {
		ivPlayPause.setOnClickListener(l);
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
