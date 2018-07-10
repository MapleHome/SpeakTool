package com.speaktool.impl.player;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.maple.msdialog.AlertDialog;
import com.speaktool.SpeakToolApp;
import com.speaktool.bean.LocalRecordBean;
import com.speaktool.bean.ScreenInfoBean;
import com.speaktool.service.PlayService;
import com.speaktool.utils.RecordFileUtils;

import java.io.File;

public class PlayProcess {
    /**
     * 播放动作
     */
    public static final String ACTION_PLAY = "action_play";
    /**
     * 杀死播放进程
     */
    public static final String ACTION_KILL_PLAY_PROCESS = "action_kill_play_process";
    /**
     * 预览
     */
    public static final String ACTION_PREVIEW = "action_preview";
    /**
     * 制作发行脚本
     */
    public static final String ACTION_MAKE_RELEASE_SCRIPT = "action_make_release_script";

    public static final String EXTRA_RECORD_DIR = "extra_record_dir";
    public static final String PERMISSION = "permission_play";
    public static final String EXTRA_PREVIEW_PAGE_ID = "extra_preview_pageId";
    public static final int MAKE_SUECESS = 1;
    public static final int MAKE_FAIL = 2;
    public static final String ACTION_MAKE_RESULT = "action_make_result";
    public static final String EXTRA_MAKE_RESULT = "extra_make_result";

    public static final String EXTRA_SCREEN_INFO = "extra_screen_info";
    public static final String EXTRA_ACTION = "extra_action";

    public static final String ACTION_PREVIEW_RESULT = "action_preview_result";

    public static void doLogic(final Context context, Intent intent) {
        if (intent == null) {
            stopPlayProcess(context);
            return;
        }
        String action = intent.getStringExtra(EXTRA_ACTION);
        if (ACTION_PLAY.equals(action)) {// 播放
            String dir = intent.getStringExtra(EXTRA_RECORD_DIR);
            LocalRecordBean item = new LocalRecordBean();
            item.setDuration(RecordFileUtils.getRecordDuration(dir));
            item.setRecordDir(dir);
            // 去画板界面
            toDrawPager(context, item);
        } else if (ACTION_KILL_PLAY_PROCESS.equals(action)) {
            stopPlayProcess(context);
        } else if (ACTION_PREVIEW.equals(action)) {// 预览

            final ScreenInfoBean info = (ScreenInfoBean) intent.getSerializableExtra(EXTRA_SCREEN_INFO);// 屏幕信息
            final String dirPath = intent.getStringExtra(EXTRA_RECORD_DIR);
            final int pageId = intent.getIntExtra(EXTRA_PREVIEW_PAGE_ID, -1);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (pageId < 0)
                            RecordFileUtils.makeReleaseScript(new File(dirPath), context, info);
                        else
                            RecordFileUtils.makeReleaseScript(new File(dirPath), pageId, context, info);
                        LocalRecordBean item = new LocalRecordBean();
                        item.setDuration(RecordFileUtils.getRecordDuration(dirPath));
                        item.setRecordDir(dirPath);
                        Intent makeResultIntent = new Intent(ACTION_PREVIEW_RESULT);
                        context.sendBroadcast(makeResultIntent);
                        //
                        toDrawPager(context, item);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Intent makeResultIntent = new Intent(ACTION_PREVIEW_RESULT);

                        context.sendBroadcast(makeResultIntent);
                        SpeakToolApp.getUiHandler().post(new Runnable() {

                            @Override
                            public void run() {
                                new AlertDialog(context)
                                        .setMessage("录音合成失败！请检查存储卡空间")
                                        .setLeftButton("确定", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                stopPlayProcess(context);
                                            }
                                        })
                                        .show();
                            }
                        });
                    }
                }

            }).start();
        } else if (ACTION_MAKE_RELEASE_SCRIPT.equals(action)) {
            final ScreenInfoBean info = (ScreenInfoBean) intent.getSerializableExtra(EXTRA_SCREEN_INFO);
            final String dirPath = intent.getStringExtra(EXTRA_RECORD_DIR);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        RecordFileUtils.makeReleaseScript(new File(dirPath), context, info);

                        Intent makeResultIntent = new Intent(ACTION_MAKE_RESULT);
                        makeResultIntent.putExtra(EXTRA_MAKE_RESULT, MAKE_SUECESS);
                        context.sendBroadcast(makeResultIntent);

                        stopPlayProcess(context);

                    } catch (Exception e) {
                        e.printStackTrace();
                        // loading.dismiss();
                        Intent makeResultIntent = new Intent(ACTION_MAKE_RESULT);
                        makeResultIntent.putExtra(EXTRA_MAKE_RESULT, MAKE_FAIL);
                        context.sendBroadcast(makeResultIntent);
                        //
                        stopPlayProcess(context);
                    }
                }
            }).start();
        }
    }

    /**
     * 去画板页面
     *
     * @param context
     * @param item
     */
    private static void toDrawPager(final Context context, LocalRecordBean item) {
//        Intent it = new Intent(context, DrawActivity.class);
//        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        it.putExtra(DrawActivity.EXTRA_PLAY_MODE, PlayMode.PLAY);
//        it.putExtra(DrawActivity.EXTRA_RECORD_BEAN, item);
//        context.startActivity(it);
    }

    private static void stopPlayProcess(Context context) {
        PlayService.killServiceProcess(context);
    }
}
