package com.speaktool.impl.recorder;

import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.maple.recorder.recording.AudioChunk;
import com.maple.recorder.recording.AudioRecordConfig;
import com.maple.recorder.recording.MsRecorder;
import com.maple.recorder.recording.PullTransport;
import com.maple.recorder.recording.Recorder;
import com.speaktool.Const;
import com.speaktool.api.Draw;
import com.speaktool.bean.ScreenInfoBean;
import com.speaktool.bean.ScriptData;
import com.speaktool.impl.cmd.ICmd;
import com.speaktool.utils.ScreenFitUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 画纸页面记录器
 *
 * @author shaoshuai
 */
@SuppressWarnings("rawtypes")
public class PageRecorder {
    private static final int MAX_CMD_SIZE = 1000;

    private File dir;
    private File cmdFile;
    //    private File soundFile;
    private File releaseSoundFile;
    private List<ICmd> cmdList = new ArrayList<ICmd>();
    private Draw draw;

    public PageRecorder(Draw draw) {
        this.draw = draw;
        // 创建记录目录 /spktl/records/1674a49413e/
        String dirpath = String.format("%s%s", Const.RECORD_DIR, Long.toHexString(System.currentTimeMillis()));

        dir = new File(dirpath);
        if (!dir.exists())
            dir.mkdirs();

        releaseSoundFile = new File(dir, Const.RELEASE_SOUND_NAME);
        if (!releaseSoundFile.exists()) {
            try {
                releaseSoundFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public RecordError recordPage(int pageId) {
        RecordError ret = createFiles(pageId);
        if (ret != RecordError.SUCCESS)
            return ret;

        if (draw.getRecorderContext().isRunning()) {
            startRecorder();
        }
        return ret;
    }

    // 创建临时CMD文件和录音文件
    private RecordError createFiles(int pageId) {
        if (!isSdcardExist()) {
            return RecordError.SDCARD_NOT_EXIST;
        }
        if (!dir.exists()) {
            boolean isSuccess = dir.mkdirs();
            if (!isSuccess) {
                return RecordError.SDCARD_CANNOT_WRITE;
            }
        }
        boolean isRunning = draw.getRecorderContext().isRunning();
        String timeMills = String.valueOf(System.currentTimeMillis());
        // 确定CMD文件名
        String cmdFileName;
        if (isRunning) {
            // pageId + currentMillis + ".txt"
            cmdFileName = String.format("%s_%s%s", pageId, timeMills, Const.CMD_FILE_SUFFIX);
        } else {
            // pageId + "#" + currentMillis + ".txt"
            cmdFileName = String.format("%s_%s_%s%s", pageId, Const.UN_RECORD_FILE_FLAG, timeMills, Const.CMD_FILE_SUFFIX);
        }
        cmdFile = new File(dir, cmdFileName);
        // 创建CMD文件
        if (!cmdFile.exists()) {
            try {
                cmdFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                if (e.getMessage().contains("space"))
                    return RecordError.SDCARD_NO_ENOUGH_SPACE;
                else
                    return RecordError.SDCARD_CANNOT_WRITE;
            }
        }
        // 创建录音文件
//        if (isRunning) {
//            // pageId + "_" + currentMillis + ".amr"
//            String mp3FileName = String.format("%s%s%s%s", pageId, "_", timeMills, Const.SOUND_FILE_SUFFIX);
//            soundFile = new File(dir, mp3FileName);
//            if (!soundFile.exists()) {
//                try {
//                    soundFile.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    if (e.getMessage().contains("space"))
//                        return RecordError.SDCARD_NO_ENOUGH_SPACE;
//                    else
//                        return RecordError.SDCARD_CANNOT_WRITE;
//                }
//            }
//        }
        return RecordError.SUCCESS;
    }


    /**
     * 是否记录所有
     */
    public boolean isHaveRecordForAll() {
        File[] files = dir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return (
//                        pathname.getName().endsWith(Const.SOUND_FILE_SUFFIX) &&
//                                !pathname.getName().contains(Const.UN_RECORD_FILE_FLAG) &&
                        pathname.getName().contains(Const.RELEASE_SOUND_NAME)
                );
            }
        });
        return (files != null && files.length > 0);
    }

    /**
     * 保存当前课程界面
     *
     * @return
     */
    public RecordError saveCurrentPageRecord() {
        stopRecorder();
        // logicTime.stop();
        RecordError ret = saveCmdsToDisk();
        if (ret == RecordError.SUCCESS) {
            cmdList.clear();
        }
        return ret;

    }

    public void record(final ICmd cmd, int pageId) {
        if (cmdList.size() == MAX_CMD_SIZE) {
            // saveCmdsToDisk()
            // cmdList.clear();
            // cmdList.add(cmd);
            // recordPage

            // save to disk.
            draw.preChangePage(new Runnable() {
                public void run() {
                    cmdList.add(cmd);
                }
            });
        } else {
            cmdList.add(cmd);
        }
    }

    /**
     * 保存CMDS到磁盘
     *
     * @return
     */
    private RecordError saveCmdsToDisk() {
        if (!isSdcardExist()) {
            return RecordError.SDCARD_NOT_EXIST;
        }

        ScreenInfoBean info = ScreenFitUtil.getCurrentDeviceInfo();

        ScriptData scriptData = new ScriptData();
        scriptData.setDensity(info.density);
        scriptData.setInputScreenWidth(info.width);
        scriptData.setInputScreenHeight(info.height);
        scriptData.setInputRate(60);
        scriptData.setVersion(1);
        scriptData.setWbEvents(cmdList);
        //
        String cmdjson = new Gson().toJson(scriptData);
        try {
            BufferedWriter bufw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(cmdFile, true)));
            bufw.write(cmdjson + "\n");
            bufw.close();
            Log.e("PageRecorder", "保存CMD到文件成功,\n cmdjson:" + cmdjson + "\n filename:" + cmdFile.getAbsolutePath());
            return RecordError.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("space"))
                return RecordError.SDCARD_NO_ENOUGH_SPACE;
            else
                return RecordError.SDCARD_CANNOT_WRITE;
        }
    }

    public long recordTimeNow() {
        return getCurrentTime();
    }

    /**
     * 删除记录目录
     */
    public void deleteRecordDir() {
        if (dir == null || !dir.exists())
            return;
        stopRecorder();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files)
                f.delete();
        }
        cmdList.clear();
        dir.delete();
    }

    /**
     * 获取记录路径
     */
    public String getRecordDir() {
        return dir.getAbsolutePath();
    }

    public File getDir() {
        return dir;
    }

    public boolean isSdcardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    //----------------------------------------------------------------------------------

    private RecordWorldTime logicTime;
    private Recorder recorder;

    private void startRecorder() {
        if (logicTime == null) {
            logicTime = new RecordWorldTime(0, true);
        }
        logicTime.goRun();

        if (recorder == null) {
            recorder = MsRecorder.wav(
                    releaseSoundFile,
                    new AudioRecordConfig.Default(),
                    new PullTransport.Default(new PullTransport.OnAudioChunkPulledListener() {
                        @Override
                        public void onAudioChunkPulled(AudioChunk audioChunk) {
                            Log.e("[ audio ]", "max amplitude : " + audioChunk.maxAmplitude());
                        }
                    }));
            recorder.startRecording();
            recorder.pauseRecording();
        }
        // TODO 开始录音。。
        recorder.resumeRecording();
    }

    private void stopRecorder() {
        if (logicTime != null)
            logicTime.pause();
        // TODO 暂停录音
        if (recorder != null) {
            recorder.pauseRecording();
        }
    }

    public long getCurrentTime() {
        if (logicTime == null)
            return ICmd.TIME_DELETE_FLAG;
        return logicTime.now();
    }

    public void closeWorldTimer() {// do at draw activity finish.
        if (logicTime != null) {
            logicTime.stop();
            logicTime = null;
        }
        if (recorder != null) {
            try {
                recorder.stopRecording();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




    // 重录所有
//    public void reRecordAll() {
////        SoundRecorder.resetRefreshUiTime(0);
//        resetRefreshUiTime(0);
//        if (!draw.getRecorderContext().isRunning())
//            draw.getRecorderContext().continuing();
//        deleteAllRecordFiles();
//        // cmdList.clear(); TODO 为什么没清空？
//    }

//    /**
//     * 删除所有记录文件
//     */
//    private void deleteAllRecordFiles() {
//        stopRecorder();
//        File[] files = dir.listFiles();
//        if (files == null)
//            return;
//        String flag = String.format("_%s_", Const.UN_RECORD_FILE_FLAG);
//        for (File f : files) {
//            if (f.getName().endsWith(Const.CMD_FILE_SUFFIX) || f.getName().endsWith(Const.SOUND_FILE_SUFFIX)) {
//                if (!f.getName().contains(Const.UN_RECORD_FILE_FLAG))
//                    f.renameTo(new File(dir, f.getName().replace("_", flag)));
//            }
//        }// for end.
//    }
//
//    public void reRecordPage(int pageId) {
//        if (!draw.getRecorderContext().isRunning())
//            draw.getRecorderContext().continuing();
//        deletePageRecord(pageId);
//    }
//
//    public void deletePageRecord(int pageId) {
//        stopRecorder();
//        /**
//         * reduce ui time.
//         */
//        long pageDuration = RecordFileUtils.getPageRecordDuration(pageId, dir.getAbsolutePath(), false);
////        SoundRecorder.resetRefreshUiTime(SoundRecorder.getRefreshUiTime() - pageDuration);
//        resetRefreshUiTime(getRefreshUiTime() - pageDuration);
//        File[] files = dir.listFiles();
//        if (files == null)
//            return;
//        String page = pageId + "_";
//        String flag = String.format("_%s_", Const.UN_RECORD_FILE_FLAG);
//        for (File f : files) {
//            if (f.getName().startsWith(page)) {
//                if (f.getName().endsWith(Const.CMD_FILE_SUFFIX) || f.getName().endsWith(Const.SOUND_FILE_SUFFIX)) {
//                    /***
//                     * sound cannot be deleted,because will use to count time
//                     * for releaseScript.
//                     */
//                    if (!f.getName().contains(Const.UN_RECORD_FILE_FLAG))
//                        f.renameTo(new File(dir, f.getName().replace("_", flag)));
//                }
//            }
//        }// for.s
//    }
//
//    public boolean isHaveRecordForPage(int pageId) {
//        final String page = pageId + "_";
//        File[] files = dir.listFiles(new FileFilter() {
//            public boolean accept(File pathname) {
//                return (pathname.getName().endsWith(Const.SOUND_FILE_SUFFIX)
//                        && pathname.getName().startsWith(page)
//                        && !pathname.getName().contains(Const.UN_RECORD_FILE_FLAG));
//            }
//        });
//        return (files != null && files.length > 0);
//    }


}
