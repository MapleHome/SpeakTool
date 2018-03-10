package com.speektool.tasks;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.google.common.collect.Maps;
import com.speektool.Const;
import com.speektool.SpeekToolApp;
import com.speektool.bean.UserBean;
import com.speektool.dao.UserDatabase;

public class TaskModifyUserInfo extends BaseRunnable<Integer, Void> {

    public  interface ModifyUserInfoCallback {
        void onConnectFail();

        void onResponseFail();

        void onSuccess();
    }

    private final WeakReference<ModifyUserInfoCallback> mListener;
    private UserBean userBean;

    public TaskModifyUserInfo(ModifyUserInfoCallback listener, UserBean user) {
        mListener = new WeakReference<ModifyUserInfoCallback>(listener);
        this.userBean = user;
    }

    @Override
    public void onPostExecute(Void result) {
        super.onPostExecute(result);
    }

    @Override
    public Void doBackground() {

        Map<String, String> params = Maps.newHashMap();
        params.put("uid", userBean.getId());
        params.put("realName", userBean.getNickName());
        params.put("intro", userBean.getIntroduce());
        Map<String, File> paramsFile = null;
        if (!TextUtils.isEmpty(userBean.getPortraitPath())) {
            File photo = new File(userBean.getPortraitPath());
            if (photo.exists()) {
                paramsFile = Maps.newHashMap();
                paramsFile.put("photo", photo);
            }
        }

        String result =
//				UniversalHttp.post(Const.USER_MODIFY_URL, params, paramsFile);
                null;
        if (TextUtils.isEmpty(result)) {
            uiHandler.post(new Runnable() {

                @Override
                public void run() {
                    ModifyUserInfoCallback listener = mListener.get();
                    if (null != listener) {
                        listener.onConnectFail();
                    }
                }
            });
            return null;
        }
        try {
            JSONObject response = new JSONObject(result);
            int resultcode = response.getInt("result");
            if (resultcode == 0) {
                UserDatabase.saveUserLocalSession(userBean, SpeekToolApp.app());
                //
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ModifyUserInfoCallback listener = mListener.get();
                        if (null != listener) {
                            listener.onSuccess();
                        }
                    }
                });
            } else {//
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ModifyUserInfoCallback listener = mListener.get();
                        if (null != listener) {
                            listener.onResponseFail();
                        }
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    ModifyUserInfoCallback listener = mListener.get();
                    if (null != listener) {
                        listener.onResponseFail();
                    }
                }
            });
        }
        return null;
    }
}
