package com.speaktool.ui.Player;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.speaktool.R;
import com.speaktool.view.dialogs.ProgressDialogOffer;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Url视频播放
 *
 * @author shaoshuai
 */
public class PlayUrlVideoActivity extends FragmentActivity {
    @BindView(R.id.videoView) VideoView videoView;

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
        setContentView(R.layout.activity_videoplay);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        String videoUrl = getIntent().getStringExtra(EXTRA_VIDEO_URL);
        /* 获取MediaController对象，控制媒体播放 */
        MediaController mc = new MediaController(this);
        mVideoView.setMediaController(mc);
        mVideoView.setVideoURI(Uri.parse(videoUrl)); // 设置URI ， 指定数据
        mVideoView.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mLoadingDialog.dismiss();
            }
        });
        mLoadingDialog = ProgressDialogOffer.offerDialogAsActivity(this, getString(R.string.loading));
        mLoadingDialog.show();
        mVideoView.start();// 开始播放视频
        mVideoView.requestFocus();// 请求获取焦点
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
