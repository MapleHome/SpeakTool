package com.speaktool.tasks;

import android.text.TextUtils;

import com.speaktool.bean.UserBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TaskModifyUserInfo extends BaseRunnable<Integer, Void> {

    public interface ModifyUserInfoCallback {
        void onConnectFail();

        void onResponseFail();

        void onSuccess();
    }

    private ModifyUserInfoCallback mListener;
    private UserBean userBean;

    public TaskModifyUserInfo(ModifyUserInfoCallback listener, UserBean user) {
        mListener = listener;
        this.userBean = user;
    }

    @Override
    public void onPostExecute(Void result) {
        super.onPostExecute(result);
    }

    @Override
    public Void doBackground() {

        Map<String, String> params = new HashMap<>();
        params.put("uid", userBean.getId());
        params.put("realName", userBean.getNickName());
        params.put("intro", userBean.getIntroduce());
        Map<String, File> paramsFile = null;
        if (!TextUtils.isEmpty(userBean.getPortraitPath())) {
            File photo = new File(userBean.getPortraitPath());
            if (photo.exists()) {
                paramsFile = new HashMap<>();
                paramsFile.put("photo", photo);
            }
        }

        String result =
//				UniversalHttp.post(Const.USER_MODIFY_URL, params, paramsFile);
                null;
        if (TextUtils.isEmpty(result)) {
            mListener.onConnectFail();
            return null;
        }
        try {
            JSONObject response = new JSONObject(result);
            int resultcode = response.getInt("result");
            if (resultcode == 0) {
                mListener.onSuccess();
            } else {//
                mListener.onResponseFail();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            mListener.onResponseFail();
        }
        return null;
    }
}
