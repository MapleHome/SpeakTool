package com.speaktool.tasks;

import android.text.TextUtils;

import com.google.common.collect.Maps;
import com.speaktool.SpeakToolApp;
import com.speaktool.bean.UserBean;
import com.speaktool.utils.NetUtil;
import com.speaktool.utils.T;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * 检查第三方用户是否存在
 *
 * @author shaoshuai
 */
public class TaskCheckThirdpartyUserExist extends BaseRunnable<Integer, Void> {
    private final WeakReference<CheckThirdpartyUserExistCallback> mListener;
    private UserBean thirdPartyUserBean;

    /**
     * 检查第三方用户是否存在，回调接口
     */
    public static interface CheckThirdpartyUserExistCallback {
        /**
         * 连接失败
         */
        void onConnectFail();

        /**
         * 检查失败
         */
        void onCheckFail();

        /**
         * 存在
         */
        void onExist(UserBean thirdPartyUserBean);

        /**
         * 不存在
         */
        void onNotExist(UserBean thirdPartyUserBean);

    }

    /**
     * 检查第三方用户是否存在
     *
     * @param listener           - 用户是否存在接口回调
     * @param thirdPartyUserBean - 要检测的用户
     */
    public TaskCheckThirdpartyUserExist(CheckThirdpartyUserExistCallback listener, UserBean thirdPartyUserBean) {
        mListener = new WeakReference<CheckThirdpartyUserExistCallback>(listener);
        this.thirdPartyUserBean = thirdPartyUserBean;
    }

    @Override
    public void onPostExecute(Void result) {
        super.onPostExecute(result);
    }

    @Override
    public Void doBackground() {
        if (!NetUtil.isHaveNet(SpeakToolApp.app())) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    T.showShort(SpeakToolApp.app(), "网络不可用！");
                    CheckThirdpartyUserExistCallback listener = mListener.get();
                    if (null != listener) {
                        listener.onConnectFail();
                    }
                }
            });
            return null;
        }
        Map<String, String> params = Maps.newHashMap();
        params.put("widgetsUserid", thirdPartyUserBean.getWidgetUserId());
        params.put("userType", thirdPartyUserBean.getType() + "");
        // 开始检查
        String result =
//				UniversalHttp.post(Const.USER_CHECK_EXIST, params);
                null;
        if (TextUtils.isEmpty(result)) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    CheckThirdpartyUserExistCallback listener = mListener.get();
                    if (null != listener) {
                        listener.onConnectFail();// 连接失败
                    }
                }
            });
            return null;
        }
        try {
            JSONObject response = new JSONObject(result);
            int resultcode = response.getInt("result");
            if (resultcode == 0) {// 存在
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        CheckThirdpartyUserExistCallback listener = mListener.get();
                        if (null != listener) {
                            listener.onExist(thirdPartyUserBean);// 存在
                        }
                    }
                });

            } else if (resultcode == 1) {// 不存在
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        CheckThirdpartyUserExistCallback listener = mListener.get();
                        if (null != listener) {
                            listener.onNotExist(thirdPartyUserBean);// 不存在
                        }
                    }
                });
            } else {// 其他
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        CheckThirdpartyUserExistCallback listener = mListener.get();
                        if (null != listener) {
                            listener.onCheckFail();// 检查失败
                        }
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    CheckThirdpartyUserExistCallback listener = mListener.get();
                    if (null != listener) {
                        listener.onCheckFail();// 检查失败
                    }
                }
            });
        }
        return null;
    }
}
