package com.speaktool.tasks;

import com.speaktool.SpeakToolApp;
import com.speaktool.bean.SearchCategoryBean;
import com.speaktool.dao.RecordCategoriesDatabase;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 加载笔记分类
 *
 * @author maple
 * @time 2018/5/27.
 */
public class TaskLoadRecordCategories extends BaseRunnable<Integer, Void> {

    public interface RecordTypeLoadListener {
        void onRecordTypeLoaded(List<SearchCategoryBean> result);
    }

    private final WeakReference<RecordTypeLoadListener> mListener;
    private boolean isNeedAllType;

    public TaskLoadRecordCategories(RecordTypeLoadListener listener, boolean isNeedAllType) {
        mListener = new WeakReference<>(listener);
        this.isNeedAllType = isNeedAllType;
    }

    @Override
    public void onPostExecute(Void result) {
        super.onPostExecute(result);
    }

    @Override
    public Void doBackground() {
        final List<SearchCategoryBean> recs = RecordCategoriesDatabase.getLocalCategories(SpeakToolApp.app(), isNeedAllType);
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                RecordTypeLoadListener listener = mListener.get();
                if (null != listener) {
                    listener.onRecordTypeLoaded(recs);
                }
            }
        });
        return null;
    }

}
