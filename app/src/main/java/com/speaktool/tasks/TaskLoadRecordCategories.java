package com.speaktool.tasks;

import android.text.TextUtils;

import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import com.speaktool.SpeakToolApp;
import com.speaktool.bean.SearchCategoryBean;
import com.speaktool.bean.UserBean;
import com.speaktool.dao.RecordCategoriesDatabase;
import com.speaktool.dao.UserDatabase;
import com.speaktool.utils.JsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class TaskLoadRecordCategories extends BaseRunnable<Integer, Void> {

    public interface RecordTypeLoadListener {

        void onRecordTypeLoaded(List<SearchCategoryBean> result);
    }

    private final WeakReference<RecordTypeLoadListener> mListener;
    private boolean isNeedAllType;

    public TaskLoadRecordCategories(RecordTypeLoadListener listener, boolean isNeedAllType) {

        mListener = new WeakReference<RecordTypeLoadListener>(listener);
        this.isNeedAllType = isNeedAllType;
    }

    @Override
    public void onPostExecute(Void result) {

        super.onPostExecute(result);
    }

    @Override
    public Void doBackground() {

        final List<SearchCategoryBean> recs = RecordCategoriesDatabase.getLocalCategories(SpeakToolApp.app(),
                isNeedAllType);
        uiHandler.post(new Runnable() {

            @Override
            public void run() {
                RecordTypeLoadListener listener = mListener.get();
                if (null != listener) {

                    listener.onRecordTypeLoaded(recs);
                }

            }
        });

        final UserBean session = UserDatabase.getUserLocalSession(SpeakToolApp.app());
        if (session == null || session.getLoginState() == UserBean.STATE_OUT) {
            return null;

        }

        Map<String, String> params = Maps.newHashMap();
        params.put("uid", session.getId());
        String result =
//				UniversalHttp.post(Const.COURSE_TYPES_URL, params);
                null;
        if (TextUtils.isEmpty(result)) {
            return null;
        }
        try {
            JSONObject response = new JSONObject(result);
            int resultcode = response.getInt("result");
            if (resultcode == 0) {
                // save session.
                JSONArray returnData = response.getJSONArray("returnData");
                // GLogger.e("lich", "search returnData:" +
                // returnData.toString());
                Type collectionType2 = new TypeToken<List<SearchCategoryBean>>() {
                }.getType();
                final List<SearchCategoryBean> serverTypeist = JsonUtil.fromJonGeneric(returnData.toString(),
                        collectionType2);
                if (serverTypeist == null || serverTypeist.isEmpty()) {

                    return null;
                }

                if (recs == null || recs.isEmpty()) {
                    uiHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            RecordTypeLoadListener listener = mListener.get();
                            if (null != listener) {

                                listener.onRecordTypeLoaded(serverTypeist);
                            }
                        }
                    });
                    return null;
                }
                for (SearchCategoryBean serverType : serverTypeist) {
                    if (!recs.contains(serverType))
                        recs.add(serverType);
                }
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        RecordTypeLoadListener listener = mListener.get();
                        if (null != listener) {
                            listener.onRecordTypeLoaded(recs);
                        }
                    }
                });
            } else {
                // ignore.
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }

        return null;

    }

}
