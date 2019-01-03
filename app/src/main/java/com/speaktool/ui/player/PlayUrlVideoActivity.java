package com.speaktool.ui.player;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.speaktool.R;
import com.speaktool.view.dialogs.LoadingDialog;

import androidx.fragment.app.FragmentActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Url视频播放
 *
 * @author shaoshuai
 */
public class PlayUrlVideoActivity extends FragmentActivity {
    public static final String EXTRA_VIDEO_URL = "video_url";

    @BindView(R.id.videoView) VideoView videoView;

    private VideoView mVideoView;
    private Dialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mLoadingDialog = new LoadingDialog(this, getString(R.string.loading));
        mLoadingDialog.show();
        mVideoView.start();// 开始播放视频
        mVideoView.requestFocus();// 请求获取焦点
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
