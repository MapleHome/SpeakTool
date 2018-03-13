package com.speaktool.ui.activity;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.speaktool.R;
import com.speaktool.ui.dialogs.ProgressDialogOffer;

/**
 * Url视频播放
 * 
 * @author shaoshuai
 * 
 */
@ContentView(R.layout.activity_videoplay)
public class PlayUrlVideoActivity extends RoboActivity {
	@InjectView(R.id.videoView)
	private VideoView videoView;

	private VideoView mVideoView;
	private Dialog mLoadingDialog;

	public static final String EXTRA_VIDEO_URL = "video_url";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		/* 设置播放视频时候不需要的部分 *//* 以下代码需要写在setContentView();之前 */
		/* 去掉title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		/* 设置全屏 */
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		/* 设置屏幕常亮 *//* flag：标记 ； */
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);// inject finish.

		initView();
	}

	private void initView() {
		String videoUrl = getIntent().getStringExtra(EXTRA_VIDEO_URL);
		/* 获取组件对象 */
		mVideoView = (VideoView) findViewById(R.id.videoView);
		/* 获取MediaController对象，控制媒体播放 */
		MediaController mc = new MediaController(this);
		mVideoView.setMediaController(mc);
		/* 设置URI ， 指定数据 */
		mVideoView.setVideoURI(Uri.parse(videoUrl));
		mVideoView.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				mLoadingDialog.dismiss();
			}
		});
		mLoadingDialog = ProgressDialogOffer.offerDialogAsActivity(this, getString(R.string.loading));
		mLoadingDialog.show();
		/* 开始播放视频 */
		mVideoView.start();
		/* 请求获取焦点 */
		mVideoView.requestFocus();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onPause() {
		mVideoView.pause();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (mLoadingDialog.isShowing())
			mLoadingDialog.dismiss();
		mVideoView.stopPlayback();
		mVideoView = null;
		super.onDestroy();
	}
}
