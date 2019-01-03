package com.speaktool.tasks;

import com.speaktool.Const;
import com.speaktool.ui.draw.RecordBean;
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
public class TaskLoadRecords extends BaseRunnable<Integer, List<RecordBean>> {
    private RecordsUi mListener;

    public interface RecordsUi {
        void onRecordsLoaded(List<RecordBean> datas);
    }

    public TaskLoadRecords(RecordsUi listener) {
        mListener = listener;
    }

    @Override
    public void onPostExecute(List<RecordBean> result) {
        super.onPostExecute(result);
        mListener.onRecordsLoaded(result);
    }

    @Override
    public List<RecordBean> doBackground() {
        return getLocalRecords();
    }

    /**
     * 获取本地课程记录
     */
    private List<RecordBean> getLocalRecords() {
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

        List<RecordBean> recs = new ArrayList<>();
        for (File dir : files) {
            File infoFile = new File(dir, Const.INFO_FILE_NAME);
            File mCmdFile = new File(dir, Const.RELEASE_JSON_SCRIPT_NAME);
            File audioFile = new File(dir, Const.RELEASE_SOUND_NAME);
            // 解析info.txt文件信息
            RecordBean infoBean = RecordFileAnalytic.analyticInfoFile(dir);
            if (infoBean != null && mCmdFile.exists()) {
                infoBean.dir = dir.getAbsolutePath();
                recs.add(infoBean);
            } else {
                FileUtils.deleteDir(dir);
            }
        }
        Collections.sort(recs, new Comparator<RecordBean>() {
            @Override
            public int compare(RecordBean lhs, RecordBean rhs) {
                return Long.compare(rhs.createTime, lhs.createTime);
            }
        });
        return recs;
    }

}
