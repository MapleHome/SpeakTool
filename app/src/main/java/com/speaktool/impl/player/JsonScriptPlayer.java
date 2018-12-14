package com.speaktool.impl.player;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.SystemClock;
import android.util.Log;

import com.speaktool.Const;
import com.speaktool.api.Draw;
import com.speaktool.bean.LocalRecordBean;
import com.speaktool.bean.TransformShapeData;
import com.speaktool.busevents.PlayTimeChangedEvent;
import com.speaktool.impl.cmd.ICmd;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;


/**
 * JSON脚本播放器
 *
 * @author Maple Shao
 */
@SuppressWarnings("rawtypes")
public class JsonScriptPlayer {
    private JsonScriptParser parser;
    private Draw draw;
    private MediaPlayer mSoundPlayer;

    private File mJsonFile;
    private int mPlayDuration;// 音频总时长

    private volatile boolean isExit = false;
    private volatile boolean isRequestStopPlayThread = false;
    private volatile boolean isSounFinish = true;
    private volatile boolean isPlayComplete = false;// 是否播放完
    private volatile boolean isUserPlaying = false;

    public File getScreenInfoFile(File dir) {
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getAbsolutePath().endsWith(Const.CMD_FILE_SUFFIX);
            }
        });
        if (files == null || files.length < 1)
            return null;
        return files[0];
    }

    public JsonScriptPlayer(LocalRecordBean rec, Draw draw) {
        this.draw = draw;
        String recordDirPath = rec.getRecordDir();

        draw.setRecordDir(recordDirPath);
        File recordDir = new File(recordDirPath);

        parser = new JsonScriptParser(draw.context(), getScreenInfoFile(recordDir));
        // 内容文件 release.txt
        mJsonFile = new File(recordDir, Const.RELEASE_JSON_SCRIPT_NAME);
        if (!mJsonFile.exists()) {
            throw new IllegalArgumentException("脚本文件不存在！");
        }

        mSoundPlayer = new MediaPlayer();
        mSoundPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mSoundPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isSounFinish = true;
            }
        });
        // 录音文件 release.mp3
        File soundfile = new File(recordDir, Const.RELEASE_SOUND_NAME);
        if (soundfile.exists()) {
            try {
                mSoundPlayer.setDataSource(soundfile.getAbsolutePath());
                mSoundPlayer.prepare();
                mPlayDuration = mSoundPlayer.getDuration();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("播放文件不存在！");
        }


        startCmdPlayThread();
        startRefreshProgressUi();
    }

    private void showLoading() {
        // Intent it = new Intent(draw.context(), DialogActivity.class);
        // it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // draw.context().startActivity(it);
    }

    private void closeLoading() {
        // draw.postTaskToUiThread(new Runnable() {
        // @Override
        // public void run() {
        // Intent it = new Intent(DialogActivity.ACTION_CLOSE_DIALOG);
        // draw.context().sendBroadcast(it);
        // }
        // });

    }

    public void exitPlayer() {
        isExit = true;
        isSounFinish = true;
        isRequestStopPlayThread = true;

        mRefreshProgressTimer.cancel();
        mRefreshProgressTimer = null;

        mSoundPlayer.stop();
        mSoundPlayer.release();
        mSoundPlayer = null;

    }

    private Timer mRefreshProgressTimer;

    private void startRefreshProgressUi() {
        mRefreshProgressTimer = new Timer();
        mRefreshProgressTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if (!isSounFinish) {
                    long currentPosition = getPlayerCurrentPosition();
                    EventBus.getDefault().post(new PlayTimeChangedEvent(currentPosition, mPlayDuration));
                } else {
                    EventBus.getDefault().post(new PlayTimeChangedEvent(mPlayDuration, mPlayDuration));
                }
            }
        }, 500, 1000);
    }

    public void play() {
        play(-1);
    }

    private List<ICmd> orgCmds;

    private void play(final int seekPosition) {
        showLoading();
        runTask(new Runnable() {
            @Override
            public void run() {
                isUserPlaying = true;
                isRequestStopPlayThread = false;
                isPlayComplete = false;
                isSounFinish = false;
                //
                if (orgCmds == null) {
                    orgCmds = parser.jsonFileToCmds(mJsonFile.getAbsolutePath());
                }
                List<ICmd> seekedCmds = getSeekedCmds(orgCmds, seekPosition);
                final int size = seekedCmds.size();
                draw.postTaskToUiThread(new Runnable() {
                    @Override
                    public void run() {
                        draw.resetAllViews();
                        draw.onPlayStart();
                    }
                });

                for (int i = 0; i < size; i++) {
                    ICmd cd = seekedCmds.get(i);
                    long cmdOrgTime = cd.getTime();
                    if (isRequestStopPlayThread)
                        return;
                    // sound cannot delete when rerecord.
                    if (cmdOrgTime < seekPosition) {// seek
                        cd.setTime(ICmd.TIME_DELETE_FLAG);
                        ICmd copy = cd.copy();
                        if (copy != null) {
                            cd.setTime(cmdOrgTime);
                            cd = copy;
                        }
                    }
                    if (cd.getTime() == ICmd.TIME_DELETE_FLAG && mSoundPlayer.isPlaying()) {
                        mSoundPlayer.pause();
                    } else {
                        if (!isSounFinish && cd.getTime() != ICmd.TIME_DELETE_FLAG && isUserPlaying)
                            mSoundPlayer.start();
                    }

                    if (cd.getTime() != ICmd.TIME_DELETE_FLAG) {// normal cmd.
                        draw.postTaskToUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeLoading();
                                draw.hideViewFlipperOverlay();
                                draw.getCurrentBoard().refresh();
                            }
                        });
                    }
                    while (!isRequestStopPlayThread && !isSounFinish && getPlayerCurrentPosition() < cd.getTime()) {
                        // 声音可能失败。
                        if (isRequestStopPlayThread) {
                            cd.setTime(cmdOrgTime);// 还原时间。
                            return;
                        }
                        SystemClock.sleep(300);
                    }// while end.
                    cd.run(draw, null);
                    cd.setTime(cmdOrgTime);// 还原时间。
                }
                //for end,all cmd finish.
                draw.postTaskToUiThread(new Runnable() {// 所有CMD完成后刷新
                    @Override
                    public void run() {
                        closeLoading();
                        draw.hideViewFlipperOverlay();
                        draw.getCurrentBoard().refresh();
                    }
                });

                if (!isSounFinish)
                    mSoundPlayer.start();
                while (!isRequestStopPlayThread && !isSounFinish) {
                    // wait sound.
                    if (isRequestStopPlayThread)
                        return;
                    SystemClock.sleep(100);
                }
                if (isRequestStopPlayThread)
                    return;
                EventBus.getDefault().post(new PlayTimeChangedEvent(mPlayDuration, mPlayDuration));
                isPlayComplete = true;
                isUserPlaying = false;
                isSounFinish = true;
                //
                draw.onPlayComplete();
            }
        });
    }

    private static List<ICmd> getSeekedCmds(List<ICmd> orgCmds, int seekPosition) {
        if (seekPosition <= 0)
            return orgCmds;
        List<ICmd> filteredCmds = new ArrayList<>();
        Map<Integer, Boolean> transformCmd = new HashMap<>();
        for (int j = orgCmds.size() - 1; j >= 0; j--) {
            ICmd cmd = orgCmds.get(j);
            if (cmd.getTime() <= seekPosition) {// unrecord.
                // filter repeat transform cmds.
                if (cmd.getType().equals(ICmd.TYPE_TRANSFORM_SHAPE)) {
                    TransformShapeData data = (TransformShapeData) cmd.getData();
                    if (!transformCmd.containsKey(data.getShapeID())) {
                        transformCmd.put(data.getShapeID(), true);
                        filteredCmds.add(0, cmd);
                    } else {
                        Log.e("JsonScriptPlayer", "repeat cmd>>>>>>>>>>>>>>>>>>>>>>");
                    }
                } else {// not transform.
                    filteredCmds.add(0, cmd);
                }
            } else {// record cmd.
                filteredCmds.add(0, cmd);
            }
        }// for cmds end.
        return filteredCmds;
    }

    private long getPlayerCurrentPosition() {
        if (isPlayComplete)
            return mPlayDuration;
        else
            return mSoundPlayer.getCurrentPosition();
    }

    private final BlockingDeque<Runnable> mLinkedBlockingDeque = new LinkedBlockingDeque<Runnable>(1);

    private void startCmdPlayThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isExit) {
                    try {
                        Runnable tk = mLinkedBlockingDeque.take();// block.
                        tk.run();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            }
        }).start();
    }

    private void runTask(Runnable task) {
        try {
            mLinkedBlockingDeque.clear();
            mLinkedBlockingDeque.put(task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        if (mSoundPlayer != null) {
            mSoundPlayer.pause();
        }
        isUserPlaying = false;
    }

    public void goOn() {
        if (mSoundPlayer != null) {
            mSoundPlayer.start();
        }
        isUserPlaying = true;
    }

    public boolean isPlaying() {
        return isUserPlaying;
    }

    public static final int MAX_PROGRESS = 1000;

    public void seekTo(final long position) {
        //seekforward or seekback.
        isRequestStopPlayThread = true;
        final int positionTimeMills = (int) (((float) position / MAX_PROGRESS) * mPlayDuration);
        if (position > 0) {// seek.
            mSoundPlayer.seekTo(positionTimeMills);
        } else {// replay.
            mSoundPlayer.seekTo(0);
        }
        mSoundPlayer.pause();
        isUserPlaying = false;
        draw.removeAllHandlerTasks();
        draw.postTaskToUiThread(new Runnable() {
            @Override
            public void run() {
                draw.showViewFlipperOverlay();
                play(positionTimeMills);
            }
        });
    }

    // 是否播放完成
    public boolean isPlayComplete() {
        return isPlayComplete;
    }

    public void rePlay() {
        seekTo(0);
    }
}
