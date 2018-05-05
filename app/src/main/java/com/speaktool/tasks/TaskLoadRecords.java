package com.speaktool.tasks;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.speaktool.Const;
import com.speaktool.api.CourseItem;
import com.speaktool.bean.CourseSearchBean;
import com.speaktool.bean.LocalRecordBean;
import com.speaktool.bean.SearchCategoryBean;
import com.speaktool.utils.RecordFileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.lang.ref.WeakReference;
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
    private final WeakReference<RecordsUi> mListener;
    private CourseSearchBean mCourseSearchBean;

    /**
     * 记录UI接口
     */
    public interface RecordsUi {
        void onRecordsLoaded(List<CourseItem> datas);
    }

    /**
     * 加载课程记录
     *
     * @param listener
     * @param searchBean
     * @param baseCourses
     */
    public TaskLoadRecords(RecordsUi listener, CourseSearchBean searchBean, List<CourseItem> baseCourses) {
        Preconditions.checkNotNull(baseCourses, "baseCourses 不能为空.");

        mListener = new WeakReference<>(listener);
        mCourseSearchBean = searchBean;
    }

    @Override
    public void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    public void onPostExecute(final List<CourseItem> result) {
        super.onPostExecute(result);
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                RecordsUi listener = mListener.get();
                if (null != listener) {
                    listener.onRecordsLoaded(result);
                }
            }
        });
    }

    @Override
    public List<CourseItem> doBackground() {
        List<CourseItem> locRecs = getLocalRecords(mCourseSearchBean);
        return locRecs;
    }


    /**
     * 获取本地课程记录
     *
     * @param searchBean
     * @return
     */
    private static List<CourseItem> getLocalRecords(CourseSearchBean searchBean) {
        Preconditions.checkNotNull(searchBean, "searchBean 不能为空.");
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
        List<CourseItem> recs = Lists.newArrayList();
        for (File dir : files) {
            if (isHaveReleaseScript(dir)) {
                LocalRecordBean item = new LocalRecordBean();
                item.setRecordDir(dir.getAbsolutePath());
                boolean ret = setInfo(item, dir);
                if (ret) {
                    String title = item.getRecordTitle();
                    if (title == null)
                        title = "";
                    String keywords = searchBean.getKeywords();
                    if (searchBean.getCategory().getCategoryId() == SearchCategoryBean.CID_ALL) {
                        if (keywords == null || title.contains(keywords))
                            recs.add(item);
                    } else {
                        if (searchBean.getCategory().getCategoryName().equals(item.getType())
                                && (keywords == null || title.contains(keywords)))
                            recs.add(item);
                    }
                }

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

    private static boolean isHaveReleaseScript(File dir) {
        File mJsonFile = new File(dir, Const.RELEASE_JSON_SCRIPT_NAME);
        File soundfile = new File(dir, Const.RELEASE_SOUND_NAME);
        return mJsonFile.exists() && soundfile.exists();
    }

    private static boolean setInfo(LocalRecordBean item, File dir) {
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

            long duration = RecordFileUtils.getRecordDuration(dir.getAbsolutePath());
            item.setDuration(Long.valueOf(duration));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
