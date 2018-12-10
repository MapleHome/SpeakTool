package com.speaktool.utils.record;

import android.text.TextUtils;
import android.util.Log;

import com.speaktool.Const;
import com.speaktool.bean.LocalRecordBean;
import com.speaktool.bean.RecordUploadBean;
import com.speaktool.bean.ScreenInfoBean;
import com.speaktool.utils.MD5Util;
import com.speaktool.utils.RecordFileUtils;
import com.speaktool.utils.ScreenFitUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Record info.txt文件解析
 *
 * @author maple
 * @time 2018/11/13
 */
public class RecordFileAnalytic {
    public static final String TITLE = "title";
    public static final String THUMBNAIL_NAME = "thumnailName";
    public static final String TAB = "tab";
    public static final String CATEGORY_NAME = "categoryName";
    public static final String INTRODUCE = "introduce";
    public static final String COURSE_ID = "courseId";
    //
    public static final String MAKE_WINDOW_WIDTH = "makeWindowWidth";
    public static final String MAKE_WINDOW_HEIGHT = "makeWindowHeight";
    public static final String SHARE_URL = "shareUrl";

    /**
     * 解析info文件
     *
     * @param dir
     * @return
     */
    public static LocalRecordBean analyticInfoFile(File dir) {
        File infoFile = new File(dir, Const.INFO_FILE_NAME);
        if (!infoFile.exists()) {
            return null;
        }
        try {
            Properties p = new Properties();
            FileInputStream ins = new FileInputStream(infoFile);
            p.load(ins);
            String title = p.getProperty(TITLE);
            String thumbnailName = p.getProperty(THUMBNAIL_NAME);
            String tab = p.getProperty(TAB);
            String categoryName = p.getProperty(CATEGORY_NAME);
            String introduce = p.getProperty(INTRODUCE);
            String shareUrl = p.getProperty(SHARE_URL);
            String courseId = p.getProperty(COURSE_ID);
            ins.close();

            String thumbnailImgPath = String.format("%s%s%s", dir.getAbsolutePath(), File.separator, thumbnailName);
            //
            LocalRecordBean item = new LocalRecordBean();
            item.setRecordTitle(title != null ? title : "unknown");
            item.setThumbnailImgPath(thumbnailImgPath != null ? thumbnailImgPath : "");
            item.setTab(tab != null ? tab : "");
            item.setType(categoryName != null ? categoryName : "");
            item.setIntroduce(introduce != null ? introduce : "");
            item.setShareUrl(shareUrl);
            item.setCourseId(courseId != null ? courseId : "");
            item.setCreateTime(infoFile.lastModified());
            // 设置录音时间
            long duration = RecordFileUtils.getRecordDuration(dir.getAbsolutePath());
            item.setDuration(Long.valueOf(duration));
            return item;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     *
     * @param dir
     * @param info
     * @return
     */
    public static boolean setRecordInfos(File dir, RecordUploadBean info) {
        try {
            // logicTime.stop();
            Properties p = new Properties();
            String title = info.getTitle();
            if (TextUtils.isEmpty(title))
                title = " ";
            p.put(TITLE, title);
            String thumnailName = info.getThumbNailName();
            if (TextUtils.isEmpty(thumnailName))
                thumnailName = "unknown";
            p.put(THUMBNAIL_NAME, thumnailName);
            String tab = info.getTab();
            if (TextUtils.isEmpty(tab))
                tab = " ";
            p.put(TAB, tab);
            String categoryName = info.getType();
            if (TextUtils.isEmpty(categoryName))
                categoryName = " ";
            p.put(CATEGORY_NAME, categoryName);
            String introduce = info.getIntroduce();
            if (TextUtils.isEmpty(introduce))
                introduce = " ";
            p.put(INTRODUCE, introduce);
            //
            ScreenInfoBean screen = ScreenFitUtil.getCurrentDeviceInfo();
            p.put(MAKE_WINDOW_WIDTH, screen.width + "");
            p.put(MAKE_WINDOW_HEIGHT, screen.height + "");
            //
            p.put(COURSE_ID, MD5Util.getUUID());
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

}
