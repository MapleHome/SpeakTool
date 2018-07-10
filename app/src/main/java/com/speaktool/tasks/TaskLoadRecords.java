package com.speaktool.tasks;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import com.speaktool.Const;
import com.speaktool.api.CourseItem;
import com.speaktool.bean.CourseSearchBean;
import com.speaktool.bean.LocalRecordBean;
import com.speaktool.bean.SearchCategoryBean;
import com.speaktool.bean.ServerRecordBean;
import com.speaktool.utils.JsonUtil;
import com.speaktool.utils.RecordFileUtils;

/**
 * 加载记录任务
 *
 * @author shaoshuai
 */
public class TaskLoadRecords extends BaseRunnable<Integer, List<CourseItem>> {

    private static final String tag = TaskLoadRecords.class.getSimpleName();

    private final WeakReference<RecordsUi> mListener;
    private CourseSearchBean mCourseSearchBean;
    private boolean isNeedLoadLocalRecord;
    private final List<CourseItem> baseCourses;

    /**
     * 记录UI接口
     */
    public static interface RecordsUi {

        void onRecordsLoaded(List<CourseItem> datas);

        /**
         * 没有更多数据
         */
        void onNoMoreData(List<CourseItem> datas);

        /**
         * 失败
         */
        void onFail(List<CourseItem> datas);

        /**
         * 没有登陆
         */
        void onNotLogin(List<CourseItem> datas);

    }

    /**
     * 加载课程记录
     *
     * @param listener
     * @param searchBean
     * @param isNeedLoadLocalRecord 是否需要加载本地记录
     * @param baseCourses
     */
    public TaskLoadRecords(RecordsUi listener, CourseSearchBean searchBean, boolean isNeedLoadLocalRecord,
                           List<CourseItem> baseCourses) {

        Preconditions.checkNotNull(baseCourses, "baseCourses 不能为空.");

        mListener = new WeakReference<RecordsUi>(listener);
        mCourseSearchBean = searchBean;
        this.isNeedLoadLocalRecord = isNeedLoadLocalRecord;
        this.baseCourses = baseCourses;

    }

    @Override
    public void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    public void onPostExecute(List<CourseItem> result) {
        super.onPostExecute(result);
    }

    @Override
    public List<CourseItem> doBackground() {
        // 是否需要加载本地记录
        if (isNeedLoadLocalRecord) {
            final List<CourseItem> locRecs = getLocalRecords(mCourseSearchBean);
            if (locRecs != null) {
                baseCourses.addAll(locRecs);
            }
        }
        //
        if (TextUtils.isEmpty(mCourseSearchBean.getUid())) {// not login.
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    RecordsUi listener = mListener.get();
                    if (null != listener) {
                        listener.onNotLogin(baseCourses);
                    }
                }
            });
            return baseCourses;
        }
        Map<String, String> params = Maps.newHashMap();
        params.put("uid", mCourseSearchBean.getUid() + "");
        params.put("page", mCourseSearchBean.getPageSize() + "");
        params.put("keywords", mCourseSearchBean.getKeywords());
        if (mCourseSearchBean.getCategory().getCategoryId() == SearchCategoryBean.CID_ALL) {
            params.put("categoryName", null);
        } else {
            params.put("categoryName", mCourseSearchBean.getCategory().getCategoryName());
        }
        params.put("courseType", mCourseSearchBean.getCourseType() + "");
        params.put("pageNum", mCourseSearchBean.getPageNumber() + "");

        String result =
//				UniversalHttp.post(Const.COURSE_SEARCH_URL, params);
                null;
        Log.e(tag, "result:" + result);
        if (TextUtils.isEmpty(result)) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    RecordsUi listener = mListener.get();
                    if (null != listener) {
                        listener.onFail(baseCourses);
                    }
                }
            });
            return baseCourses;
        }
        //
        try {
            JSONObject response = new JSONObject(result);
            int resultcode = response.getInt("result");
            if (resultcode == 0) {
                JSONArray returnData = response.getJSONArray("returnData");
                Type collectionType2 = new TypeToken<List<ServerRecordBean>>() {
                }.getType();
                List<ServerRecordBean> serverCourseList = JsonUtil.fromJonGeneric(returnData.toString(),
                        collectionType2);
                if (serverCourseList == null || serverCourseList.isEmpty()) {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            RecordsUi listener = mListener.get();
                            if (null != listener) {
                                listener.onNoMoreData(baseCourses);
                            }
                        }
                    });
                } else {
                    combineServerRecordsToLocal(baseCourses, serverCourseList);
                    if (serverCourseList.size() < mCourseSearchBean.getPageSize()) {
                        Log.e(tag, "没有更多数据");
                        uiHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                RecordsUi listener = mListener.get();
                                if (null != listener) {
                                    listener.onNoMoreData(baseCourses);
                                }
                            }
                        });

                    } else {
                        uiHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                RecordsUi listener = mListener.get();
                                if (null != listener) {
                                    listener.onRecordsLoaded(baseCourses);
                                }
                            }
                        });
                    }
                }
            } else {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        RecordsUi listener = mListener.get();
                        if (null != listener) {
                            listener.onFail(baseCourses);
                        }
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
            uiHandler.post(new Runnable() {

                @Override
                public void run() {
                    RecordsUi listener = mListener.get();
                    if (null != listener) {
                        listener.onFail(baseCourses);
                    }

                }
            });
        }

        return baseCourses;
    }

    /**
     * 添加服务器记录到本地
     *
     * @param localDatas
     * @param serverCourseList
     */
    private void combineServerRecordsToLocal(List<CourseItem> localDatas, List<ServerRecordBean> serverCourseList) {

        for (CourseItem server : serverCourseList) {
            Log.e(tag, "服务器记录名称:" + server.getRecordTitle());
            int index = localDatas.indexOf(server);
            if (index == -1)
                localDatas.add(server);
            else {// 重复数据
                localDatas.get(index).setShareUrl(server.getShareUrl());
                localDatas.get(index).setCourseId(server.getCourseId());
            }
        }// for end.
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
        LocalRecordBean item = null;
        for (File dir : files) {
            if (!isHaveReleaseScript(dir)) {
                // delete dir todo.
                RecordFileUtils.deleteDirectory(dir);
                continue;
            }
            item = new LocalRecordBean();
            item.setRecordDir(dir.getAbsolutePath());

            boolean ret = setInfo(item, dir);
            if (ret == false) {
                RecordFileUtils.deleteDirectory(dir);
                continue;
            }
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
//        File soundfile = new File(dir, Const.RELEASE_SOUND_NAME);
        return mJsonFile.exists();
//        && soundfile.exists();
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
