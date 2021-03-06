//package com.speaktool.impl.player;
//
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.media.MediaPlayer.OnCompletionListener;
//import android.media.MediaPlayer.OnErrorListener;
//import android.media.MediaPlayer.OnPreparedListener;
//
//import com.speaktool.SpeakApp;
//import com.speaktool.bean.MusicBean;
//import com.speaktool.busevents.MusicStateChangedEvent;
//import com.speaktool.utils.T;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.util.Timer;
//import java.util.TimerTask;
//
//
//public class SoundPlayer {
//    private static SoundPlayer sSoundPlayer = new SoundPlayer();
//    private MusicBean mMusicBean;
//    private MediaPlayer mMediaPlayer;
//    private Timer progressTimer;
//    public static final int MAX_PROGRESS = 100;
//
//    public static SoundPlayer unique() {
//        return sSoundPlayer;
//    }

//    public void play(final MusicBean music) {
//
//        mMusicBean = music;
//        EventBus.getDefault().post(new MusicStateChangedEvent(mMusicBean.getPath(), MusicStateChangedEvent.STATE_ON));
//        mMediaPlayer = new MediaPlayer();
//        progressTimer = new Timer();
//        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mMediaPlayer.setVolume(0.2f, 0.2f);
//        // 播放完成监听
//        mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                EventBus.getDefault().post(
//                        new MusicStateChangedEvent(mMusicBean.getPath(), MusicStateChangedEvent.STATE_PROGRESS,
//                                MAX_PROGRESS));
//                stop();
//            }
//        });
//        // 当MediaPlayer调用prepare()方法时触发该监听器。
//        mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mMediaPlayer.start();
//                progressTimer.scheduleAtFixedRate(new TimerTask() {
//                    @Override
//                    public void run() {
//                        if (mMediaPlayer != null) {
//                            float c = mMediaPlayer.getCurrentPosition();
//                            float t = mMediaPlayer.getDuration();
//                            int p = (int) (c / t * MAX_PROGRESS);
//                            EventBus.getDefault().post(
//                                    new MusicStateChangedEvent(mMusicBean.getPath(),
//                                            MusicStateChangedEvent.STATE_PROGRESS, p));
//                        }
//                    }
//                }, 100, 1000);
//            }
//        });
//        // 错误监听
//        mMediaPlayer.setOnErrorListener(new OnErrorListener() {
//
//            @Override
//            public boolean onError(MediaPlayer mp, int what, int extra) {
//                T.showShort(SpeakApp.app(), "播放失败！");
//                return false;// false OnCompletionListener will be called.
//            }
//        });
//        //
//        try {
//            mMediaPlayer.setDataSource(mMusicBean.getPath());
//            mMediaPlayer.prepareAsync();
//        } catch (Exception e) {
//            T.showShort(SpeakApp.app(), "播放失败！");
//            e.printStackTrace();
//            stop();
//        }
//        //
//
//    }
//
//    public void stop() {
//        if (mMediaPlayer != null) {
//            EventBus.getDefault().post(new MusicStateChangedEvent(mMusicBean.getPath(), MusicStateChangedEvent.STATE_OFF));
//            progressTimer.cancel();
//            progressTimer = null;
//            //
//            mMediaPlayer.stop();
//            mMediaPlayer.release();
//            mMediaPlayer = null;
//            // mMusicBean = null;
//        }
//    }
//
//    public MusicBean getCurrentMusic() {
//        return mMusicBean;
//    }
//
//    public void seekTo(int p) {
//        if (mMediaPlayer != null) {
//            final int positionTimeMills = (int) (((float) p / MAX_PROGRESS) * mMediaPlayer.getDuration());
//            mMediaPlayer.seekTo(positionTimeMills);
//        }
//    }
//
//    public void playSwitchClick() {
//        if (mMusicBean == null) {
//            T.showShort(SpeakApp.app(), "未设置音乐！");
//            return;
//        }
//        if (mMediaPlayer == null) {
//            play(mMusicBean);
//            return;
//        }
//        if (mMediaPlayer.isPlaying()) {
//            mMediaPlayer.pause();
//            EventBus.getDefault().post(
//                    new MusicStateChangedEvent(mMusicBean.getPath(), MusicStateChangedEvent.STATE_OFF));
//            return;
//        }
//        mMediaPlayer.start();
//        EventBus.getDefault().post(new MusicStateChangedEvent(mMusicBean.getPath(), MusicStateChangedEvent.STATE_ON));
//    }
//
//    // 暂停
//    public void pause() {
//        if (mMediaPlayer == null || mMusicBean == null) {
//            return;
//        }
//        if (mMediaPlayer.isPlaying()) {
//            mMediaPlayer.pause();
//            EventBus.getDefault().post(
//                    new MusicStateChangedEvent(mMusicBean.getPath(), MusicStateChangedEvent.STATE_OFF));
//        }
//    }
//}
