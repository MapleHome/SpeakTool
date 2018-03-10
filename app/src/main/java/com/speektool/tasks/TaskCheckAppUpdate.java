package com.speektool.tasks;

import java.lang.ref.WeakReference;
import java.util.Map;

import org.json.JSONObject;

import android.text.TextUtils;

import com.google.common.collect.Maps;
import com.speektool.Const;
import com.speektool.SpeekToolApp;
import com.speektool.manager.AppManager;

/**
 * 检查应用程序更新
 *
 * @author shaoshuai
 */
public class TaskCheckAppUpdate extends BaseRunnable<Integer, Void> {

    private final WeakReference<CheckAppUpdateCallback> mListener;

    /**
     * 检查更新回调
     */
    public static interface CheckAppUpdateCallback {
        /**
         * 需要强行更新
         */
        void onNeedForceUpdate(String updateUrl, String versionNameServer, String updateNote);

        /**
         * 推荐更新
         */
        void onReccomendUpdate(String updateUrl, String versionNameServer, String updateNote);

        /**
         * 失败
         */
        void onFail();

        /**
         * 不需要更新
         */
        void onNoNeedUpdate();
    }

    public TaskCheckAppUpdate(CheckAppUpdateCallback listener) {
        mListener = new WeakReference<CheckAppUpdateCallback>(listener);
    }

    @Override
    public Void doBackground() {
        Map<String, String> params = Maps.newHashMap();
        params.put("appSystem", AppManager.getCurrentAppSystemCode() + "");
        String result =
//				UniversalHttp.post(Const.UPDATE_URL, params);
                null;
        //http://www.speaktool.com/api/getLatestVersion.do?appSystem=20
        if (TextUtils.isEmpty(result)) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    CheckAppUpdateCallback listener = mListener.get();
                    if (null != listener) {
                        listener.onFail();
                    }
                }
            });
            return null;
        }
        try {
//			{
//			    "result": 0,
//			    "returnData": {
//			        "apiVersion": "1.1.1",
//			        "compatableApi": "1.1.0",
//			        "updateURL": "http://www.speaktool.com/apk/speaktool_v1_1_1.apk",
//			        "version": "1.1.1",
//			        "versionDesc": "1，精简绘图界面控件。2，修改分享界面，增加第三方合作平台分享和登录。3, 修正了一些bug"
//			    },
//			    "version": "1.0"
//			}

            JSONObject response = new JSONObject(result);
            int resultCode = response.getInt("result");
            if (resultCode == 0) {
                JSONObject returnData = response.getJSONObject("returnData");
                String compatableApi = returnData.getString("compatableApi");
                final String versionNameServer = returnData.getString("version");
                final String updateURL = returnData.getString("updateURL");
                final String updateNote = returnData.getString("versionDesc");
                if (appVersionStringCompare(compatableApi, Const.CURRENT_APPSERVER_API_VERSION) > 0) {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            CheckAppUpdateCallback listener = mListener.get();
                            if (null != listener) {
                                listener.onNeedForceUpdate(updateURL, versionNameServer, updateNote);
                            }
                        }
                    });
                } else {
                    if (appVersionStringCompare(versionNameServer,
                            AppManager.getCurrentAppVersionName(SpeekToolApp.app())) > 0) {

                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                CheckAppUpdateCallback listener = mListener.get();
                                if (null != listener) {
                                    listener.onReccomendUpdate(updateURL, versionNameServer, updateNote);
                                }
                            }
                        });
                    } else {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                CheckAppUpdateCallback listener = mListener.get();
                                if (null != listener) {
                                    listener.onNoNeedUpdate();
                                }
                            }
                        });
                    }
                }
            } else {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        CheckAppUpdateCallback listener = mListener.get();
                        if (null != listener) {
                            listener.onFail();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 应用版本对比
     *
     * @param left  like 1.1.0
     * @param right like 1.1.1
     * @return
     */
    private static int appVersionStringCompare(String left, String right) {
        String[] leftVal = left.split("\\.");
        String[] rightVal = right.split("\\.");
        if (leftVal.length != rightVal.length) {
            return 0;
        }
        for (int i = 0; i < leftVal.length; i++) {
            if (Integer.valueOf(leftVal[i]) > Integer.valueOf(rightVal[i])) {
                return 1;
            }
            if (Integer.valueOf(leftVal[i]) < Integer.valueOf(rightVal[i])) {
                return -1;
            }
        }
        return 0;
    }
}
