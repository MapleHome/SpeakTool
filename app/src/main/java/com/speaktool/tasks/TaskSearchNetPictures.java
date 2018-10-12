package com.speaktool.tasks;

import android.text.TextUtils;

import com.speaktool.bean.NetPictureBean;
import com.speaktool.utils.BitmapScaleUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskSearchNetPictures extends BaseRunnable<Integer, Void> {

    public interface SearchNetPicturesCallback {
        void onConnectFail();

        void onFail();

        void onSuccess(List<NetPictureBean> ret);
    }

    private static String SEARCH_URL = "http://pic.sogou.com/pics";
    private final WeakReference<SearchNetPicturesCallback> mListener;
    private String searchKey;
    private int startIndex;

    public TaskSearchNetPictures(SearchNetPicturesCallback listener, String searchKey, int startIndex) {
        mListener = new WeakReference<SearchNetPicturesCallback>(listener);
        this.searchKey = searchKey;
        this.startIndex = startIndex;
    }

    @Override
    public void onPostExecute(Void result) {

        super.onPostExecute(result);
    }

    @Override
    public Void doBackground() {

        Map<String, String> params = new HashMap<>();
        params.put("query", searchKey);
        params.put("start", startIndex + "");
        params.put("reqType", "ajax");
        String result =
//				UniversalHttp.get(SEARCH_URL, params);
                null;
        if (TextUtils.isEmpty(result)) {
            uiHandler.post(new Runnable() {

                @Override
                public void run() {
                    SearchNetPicturesCallback listener = mListener.get();
                    if (null != listener) {
                        listener.onConnectFail();
                    }
                }
            });
            return null;
        }
        try {

            JSONObject response = new JSONObject(result);
            final List<NetPictureBean> ret = new ArrayList<>();
            String totalItems = response.getString("totalItems");
            if ("0".equals(totalItems)) {
                uiHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        SearchNetPicturesCallback listener = mListener.get();
                        if (null != listener) {
                            listener.onSuccess(ret);
                        }
                    }
                });
                return null;
            }

            JSONArray items = response.getJSONArray("items");
            if (items == null || items.length() <= 0) {
                uiHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        SearchNetPicturesCallback listener = mListener.get();
                        if (null != listener) {
                            listener.onSuccess(ret);
                        }
                    }
                });
                return null;
            }

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = (JSONObject) items.get(i);

                NetPictureBean bean = new NetPictureBean();
                bean.picUrl = item.getString("pic_url");
                if (BitmapScaleUtil.isGif(bean.picUrl))
                    bean.thumbUrl = bean.picUrl;
                else
                    bean.thumbUrl = item.getString("thumbUrl");
                ret.add(bean);

            }
            uiHandler.post(new Runnable() {

                @Override
                public void run() {
                    SearchNetPicturesCallback listener = mListener.get();
                    if (null != listener) {
                        listener.onSuccess(ret);
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            uiHandler.post(new Runnable() {

                @Override
                public void run() {
                    SearchNetPicturesCallback listener = mListener.get();
                    if (null != listener) {
                        listener.onFail();
                    }
                }
            });
        }

        return null;

    }

}
