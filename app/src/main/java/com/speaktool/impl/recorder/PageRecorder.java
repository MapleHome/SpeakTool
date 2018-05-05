package com.speaktool.impl.recorder;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.speaktool.Const;
import com.speaktool.api.Draw;
import com.speaktool.bean.LocalRecordBean;
import com.speaktool.bean.RecordUploadBean;
import com.speaktool.bean.ScreenInfoBean;
import com.speaktool.bean.ScriptData;
import com.speaktool.impl.cmd.ICmd;
import com.speaktool.utils.FileIOUtils;
import com.speaktool.utils.JsonUtil;
import com.speaktool.utils.MD5Util;
import com.speaktool.utils.RecordFileUtils;
import com.speaktool.utils.ScreenFitUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 画纸页面记录器
 *
 * @author shaoshuai
 */
@SuppressWarnings("rawtypes")
public class PageRecorder {
    private File dir;
    private File cmdFile;
    private File soundFile;

    private List<ICmd> cmdList = new ArrayList<ICmd>();

    private static final int MAX_CMD_SIZE = 1000;

    private Draw draw;

    public long totalTimeNow() {
        return RecordFileUtils.getRecordDuration(dir.getAbsolutePath());
    }

    public PageRecorder(Draw draw) {
        this.draw = draw;
        String dirpath = String.format("%s%s", Const.RECORD_DIR, Long.toHexString(System.currentTimeMillis()));
        dir = new File(dirpath);
        if (!dir.exists())
            dir.mkdirs();

    }

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
        String timeMills = String.valueOf(System.currentTimeMillis());
        String cmdfilename;
        if (!draw.getRecorderContext().isRunning())
            cmdfilename = String.format("%s_%s_%s%s", pageId, Const.UN_RECORD_FILE_FLAG, timeMills,
                    Const.CMD_FILE_SUFFIX);
        else
            cmdfilename = String.format("%s_%s%s", pageId, timeMills, Const.CMD_FILE_SUFFIX);

        cmdFile = new File(dir, cmdfilename);

        if (!cmdFile.exists())
            try {
                cmdFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                if (e.getMessage().contains("space"))
                    return RecordError.SDCARD_NO_ENOUGH_SPACE;
                else
                    return RecordError.SDCARD_CANNOT_WRITE;
            }
        if (draw.getRecorderContext().isRunning()) {
            soundFile = new File(dir, String.format("%s%s%s%s", pageId, "_", timeMills, Const.SOUND_FILE_SUFFIX));
            if (!soundFile.exists())
                try {
                    soundFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    if (e.getMessage().contains("space"))
                        return RecordError.SDCARD_NO_ENOUGH_SPACE;
                    else
                        return RecordError.SDCARD_CANNOT_WRITE;
                }
        }
        return RecordError.SUCCESS;
    }

    private SoundRecorder mSoundRecorder;

    public RecordError recordPage(int pageId) {
        RecordError ret = createFiles(pageId);
        if (ret != RecordError.SUCCESS)
            return ret;

        if (draw.getRecorderContext().isRunning()) {
            mSoundRecorder = new SoundRecorder();
            mSoundRecorder.startRecord(soundFile.getAbsolutePath());
        }
        return ret;
    }

    /**
     * 保存当前课程界面
     *
     * @return
     */
    public RecordError saveCurrentPageRecord() {

        RecordError ret = saveCmdsToDisk();
        if (ret != RecordError.SUCCESS)
            return ret;
        if (mSoundRecorder != null) {
            mSoundRecorder.destroy();
            mSoundRecorder = null;
        }
        // logicTime.stop();
        cmdList.clear();
        return ret;

    }

    public void reRecordAll() {
        SoundRecorder.resetRefreshUiTime(0);
        if (!draw.getRecorderContext().isRunning())
            draw.getRecorderContext().continuing();
        deleteAllRecordFiles();
    }

    /**
     * 删除所有记录文件
     */
    private void deleteAllRecordFiles() {
        if (mSoundRecorder != null) {
            mSoundRecorder.destroy();
            mSoundRecorder = null;
        }
        File[] files = dir.listFiles();
        if (files == null)
            return;
        String flag = String.format("_%s_", Const.UN_RECORD_FILE_FLAG);
        for (File f : files) {
            if (f.getName().endsWith(Const.CMD_FILE_SUFFIX) || f.getName().endsWith(Const.SOUND_FILE_SUFFIX)) {
                if (!f.getName().contains(Const.UN_RECORD_FILE_FLAG))
                    f.renameTo(new File(dir, f.getName().replace("_", flag)));
            }
        }// for end.
    }

    public void reRecordPage(int pageId) {
        if (!draw.getRecorderContext().isRunning())
            draw.getRecorderContext().continuing();
        deletePageRecord(pageId);
    }

    public boolean isHaveRecordForPage(int pageId) {
        final String page = pageId + "_";
        File[] files = dir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                if (pathname.getName().endsWith(Const.SOUND_FILE_SUFFIX) && pathname.getName().startsWith(page)
                        && !pathname.getName().contains(Const.UN_RECORD_FILE_FLAG))
                    return true;
                else
                    return false;
            }
        });
        if (files == null || files.length < 1)
            return false;
        else
            return true;
    }

    /**
     * 是否记录所有
     */
    public boolean isHaveRecordForAll() {
        File[] files = dir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                if (pathname.getName().endsWith(Const.SOUND_FILE_SUFFIX)
                        && !pathname.getName().contains(Const.UN_RECORD_FILE_FLAG))
                    return true;
                else
                    return false;
            }
        });
        if (files == null || files.length < 1)
            return false;
        else
            return true;
    }

    public void deletePageRecord(int pageId) {
        if (mSoundRecorder != null) {
            mSoundRecorder.destroy();
            mSoundRecorder = null;
        }
        /**
         * reduce ui time.
         */
        long pageDuration = RecordFileUtils.getPageRecordDuration(pageId, dir.getAbsolutePath(), false);
        SoundRecorder.resetRefreshUiTime(SoundRecorder.getRefreshUiTime() - pageDuration);
        File[] files = dir.listFiles();
        if (files == null)
            return;
        String page = pageId + "_";
        String flag = String.format("_%s_", Const.UN_RECORD_FILE_FLAG);
        for (File f : files) {
            if (f.getName().startsWith(page)) {
                if (f.getName().endsWith(Const.CMD_FILE_SUFFIX) || f.getName().endsWith(Const.SOUND_FILE_SUFFIX)) {
                    /***
                     * sound cannot be deleted,because will use to count time
                     * for releaseScript.
                     */
                    if (!f.getName().contains(Const.UN_RECORD_FILE_FLAG))
                        f.renameTo(new File(dir, f.getName().replace("_", flag)));
                }
            }
        }// for.s
    }

    public void record(final ICmd cmd, int pageId) {
        if (cmdList.size() == MAX_CMD_SIZE) {
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

        ScriptData scriptData = new ScriptData();
        ScreenInfoBean info = ScreenFitUtil.getCurrentDeviceInfo();
        scriptData.setDensity(info.density);
        scriptData.setInputScreenWidth(info.w);
        scriptData.setInputScreenHeight(info.h);
        scriptData.setInputRate(60);
        scriptData.setVersion(1);
        scriptData.setWbEvents(cmdList);
        //
        String cmdjson = JsonUtil.toJson(scriptData);
        try {
            FileIOUtils.writeFile(cmdFile, cmdjson);
            File file = new File(Const.RECORD_DIR, "cmdfile.text");
            FileIOUtils.writeFile(file, cmdjson);
            Log.e("PageRecorder", "保存CMD到文件成功,\n cmdjson:" + cmdjson + "\n filename:" + cmdFile.getAbsolutePath());
            return RecordError.SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
            if (e.getMessage().contains("space"))
                return RecordError.SDCARD_NO_ENOUGH_SPACE;
            else
                return RecordError.SDCARD_CANNOT_WRITE;
        }
    }

    public long recordTimeNow() {
        return SoundRecorder.getCurrentTime();
    }

    /**
     * 设置记录信息
     *
     * @param info
     * @return
     */
    public boolean setRecordInfos(RecordUploadBean info) {
        try {
            // logicTime.stop();
            Properties p = new Properties();
            String title = info.getTitle();
            if (TextUtils.isEmpty(title))
                title = " ";
            p.put(LocalRecordBean.TITLE, title);
            String thumnailName = info.getThumbNailName();
            if (TextUtils.isEmpty(thumnailName))
                thumnailName = "unknown";
            p.put(LocalRecordBean.THUMBNAIL_NAME, thumnailName);
            String tab = info.getTab();
            if (TextUtils.isEmpty(tab))
                tab = " ";
            p.put(LocalRecordBean.TAB, tab);
            String categoryName = info.getType();
            if (TextUtils.isEmpty(categoryName))
                categoryName = " ";
            p.put(LocalRecordBean.CATEGORY_NAME, categoryName);
            String introduce = info.getIntroduce();
            if (TextUtils.isEmpty(introduce))
                introduce = " ";
            p.put(LocalRecordBean.INTRODUCE, introduce);
            //
            ScreenInfoBean screen = ScreenFitUtil.getCurrentDeviceInfo();
            p.put(LocalRecordBean.MAKE_WINDOW_WIDTH, screen.w + "");
            p.put(LocalRecordBean.MAKE_WINDOW_HEIGHT, screen.h + "");
            //
            p.put(LocalRecordBean.COURSE_ID, MD5Util.getUUID());
            // shareUrl.
            File infofile = new File(dir, Const.INFO_FILE_NAME);
            if (!infofile.exists())
                infofile.createNewFile();
            FileOutputStream os = new FileOutputStream(infofile);
            p.store(os, null);
            os.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PageRecorder", "设置记录信息时错误.请检查SD卡\n" + e.getMessage());
            return false;
        }
    }

    /**
     * 删除记录目录
     */
    public void deleteRecordDir() {
        if (dir == null || !dir.exists())
            return;
        if (mSoundRecorder != null)
            mSoundRecorder.destroy();
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

    public boolean isSdcardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
