package com.speaktool.tasks;

import com.speaktool.Const;
import com.speaktool.api.CourseItem;
import com.speaktool.bean.LocalRecordBean;
import com.speaktool.utils.FileUtils;
import com.speaktool.utils.record.RecordFileAnalytic;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        //  本地保存记录的根目录 /spktl/records/
        File basedir = new File(Const.RECORD_DIR);
        if (!basedir.exists())
            return null;
        File[] files = basedir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        if (files == null || files.length == 0)
            return null;

        List<CourseItem> recs = new ArrayList<>();
        for (File dir : files) {
            File infoFile = new File(dir, Const.INFO_FILE_NAME);
            File mCmdFile = new File(dir, Const.RELEASE_JSON_SCRIPT_NAME);
            File audioFile = new File(dir, Const.RELEASE_SOUND_NAME);
            // 解析info.txt文件信息
            LocalRecordBean infoBean = RecordFileAnalytic.analyticInfoFile(dir);
            if (infoBean != null && mCmdFile.exists()) {
                infoBean.setRecordDir(dir.getAbsolutePath());
                recs.add(infoBean);
            } else {
                FileUtils.deleteDir(dir);
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

}
