package com.speaktool.tasks;

import com.speaktool.Const;
import com.speaktool.api.CourseItem;
import com.speaktool.bean.LocalRecordBean;
import com.speaktool.utils.RecordFileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

/**
 * 加载记录任务
 *
 * @author shaoshuai
 */
public class TaskLoadRecords extends BaseRunnable<Integer, List<CourseItem>> {
    private RecordsUi mListener;

    public interface RecordsUi {
        void onRecordsLoaded(List<CourseItem> datas);
    }

    public TaskLoadRecords(RecordsUi listener) {
        mListener = listener;
    }

    @Override
    public void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    public void onPostExecute(List<CourseItem> result) {
        super.onPostExecute(result);
        mListener.onRecordsLoaded(result);
    }

    @Override
    public List<CourseItem> doBackground() {
        return getLocalRecords();
    }

    /**
     * 获取本地课程记录
     */
    private List<CourseItem> getLocalRecords() {
        File basedir = new File(Const.RECORD_DIR);// 本地保存记录的根目录
        if (!basedir.exists())
            return null;
        File[] files = basedir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        if (files == null)
            return null;
        List<CourseItem> recs = new ArrayList<>();
        for (File dir : files) {
            LocalRecordBean item = new LocalRecordBean();
            // release.txt 和 release.mp3 是否存在
            File mJsonFile = new File(dir, Const.RELEASE_JSON_SCRIPT_NAME);
            File audioFile = new File(dir, Const.RELEASE_SOUND_NAME);
            if (mJsonFile.exists()) {
                item.setRecordDir(dir.getAbsolutePath());
            } else {
                RecordFileUtils.deleteDirectory(dir);
                continue;
            }
            // 解析info.txt文件信息
            boolean ret = setInfo(item, dir);
            if (ret) {
                recs.add(item);
            } else {
                RecordFileUtils.deleteDirectory(dir);
                continue;
            }

        }
        Collections.sort(recs, new Comparator<CourseItem>() {
            @Override
            public int compare(CourseItem lhs, CourseItem rhs) {
                long lt = lhs.getCreateTime();
                long rt = rhs.getCreateTime();
                return lt > rt ? -1 : lt < rt ? 1 : 0;
            }
        });
        return recs;
    }

    // 解析info文件
    private boolean setInfo(LocalRecordBean item, File dir) {
        File infofile = new File(dir, Const.INFO_FILE_NAME);
        if (!infofile.exists()) {
            return false;
        }
        try {
            Properties p = new Properties();
            FileInputStream ins = new FileInputStream(infofile);
            p.load(ins);
            String title = p.getProperty(LocalRecordBean.TITLE);
            String thumbnailName = p.getProperty(LocalRecordBean.THUMBNAIL_NAME);
            String tab = p.getProperty(LocalRecordBean.TAB);
            String categoryName = p.getProperty(LocalRecordBean.CATEGORY_NAME);
            String introduce = p.getProperty(LocalRecordBean.INTRODUCE);
            String shareUrl = p.getProperty(LocalRecordBean.SHARE_URL);
            String courseId = p.getProperty(LocalRecordBean.COURSE_ID);
            ins.close();

            String thumbnailImgPath = String.format("%s%s%s", dir.getAbsolutePath(), File.separator, thumbnailName);
            //
            item.setRecordTitle(title != null ? title : "unknown");
            item.setThumbnailImgPath(thumbnailImgPath != null ? thumbnailImgPath : "");
            item.setTab(tab != null ? tab : "");
            item.setType(categoryName != null ? categoryName : "");
            item.setIntroduce(introduce != null ? introduce : "");
            item.setShareUrl(shareUrl);
            item.setCourseId(courseId != null ? courseId : "");
            item.setCreateTime(infofile.lastModified());
            // 设置录音时间
            long duration = RecordFileUtils.getRecordDuration(dir.getAbsolutePath());
            item.setDuration(Long.valueOf(duration));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
