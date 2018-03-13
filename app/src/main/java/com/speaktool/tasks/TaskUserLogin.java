package com.speaktool.tasks;

import com.google.common.collect.Maps;
import com.speaktool.Const;
import com.speaktool.SpeakToolApp;
import com.speaktool.bean.UserBean;
import com.speaktool.dao.UserDatabase;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * 用户登陆
 *
 * @author shaoshuai
 */
public class TaskUserLogin extends BaseRunnable<Integer, Void> {
    /**
     * 用户登陆回调
     */
    public interface UserLoginCallback {
        /**
         * 连接失败
         */
        void onConnectFail();

        /**
         * 登陆失败
         */
        void onLoginFail();

        /**
         * 登陆成功
         */
        void onLoginSuccess();
    }

    private final WeakReference<UserLoginCallback> mListener;
    private UserBean userBean;

    public TaskUserLogin(UserLoginCallback listener, UserBean userBean) {
        mListener = new WeakReference<UserLoginCallback>(listener);
        this.userBean = userBean;
    }

    @Override
    public void onPostExecute(Void result) {
        super.onPostExecute(result);
    }

    @Override
    public Void doBackground() {

        Map<String, String> params = Maps.newHashMap();
        params.put("account", userBean.getAccount());
        params.put("password", userBean.getPassword());
        // http://www.speaktool.com/api/userLogin.do
        // ======================用户登陆完成返回信息===================================
        // {
        // 		"result": 0,
        // 		"returnData": {
        // 			"email": "939078792@qq.com",
        // 			"intro": "更改赫兹日龙",
        // 			"photoURL": "userPhoto/7b2d5a11803b4363977bf8923dbd36a6.jpg",
        // 			"realName": "小可爱",
        // 			"token": "07b93b37323a48279aa67c1a48ad6780",
        // 			"uid": "7b2d5a11803b4363977bf8923dbd36a6"
        // 		},
        // 		"version": "1.0"
        // }

        if (0 == 0) {
            userBean.setId("7b2d5a11803b4363977bf8923dbd36a6");
            userBean.setNickName("小可爱");
            String portraitUrl = Const.SPEEKTOOL_SERVER__URL + "userPhoto/7b2d5a11803b4363977bf8923dbd36a6.jpg";
            userBean.setPortraitPath(portraitUrl);
            userBean.setIntroduce("更改赫兹日龙");
            userBean.setEmail("939078792@qq.com");
            //
            UserDatabase.saveUserLocalSession(userBean, SpeakToolApp.app());
            // 登陆成功
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    UserLoginCallback listener = mListener.get();
                    if (null != listener) {
                        listener.onLoginSuccess();
                    }
                }
            });
        } else {//
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    UserLoginCallback listener = mListener.get();
                    if (null != listener) {
                        listener.onLoginFail();// 登录失败
                    }
                }
            });
        }
        return null;
    }
}
