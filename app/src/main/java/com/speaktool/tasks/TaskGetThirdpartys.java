package com.speaktool.tasks;

import android.text.TextUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.speaktool.R;
import com.speaktool.bean.ThirdParty;
import com.speaktool.bean.UserBean;
import com.speaktool.utils.MD5Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

public class TaskGetThirdpartys extends BaseRunnable<Integer, Void> {

    public interface TaskGetThirdpartysCallback {

        void onThirdpartyLoaded(List<ThirdParty> result);

        void onConnectFail();

        void onResponseFail();
    }

    public enum PartyType {
        LOGIN, SHARE
    }

    private final WeakReference<TaskGetThirdpartysCallback> mListener;
    private PartyType mPartyType;
    private String companyIdIfExist;

    public TaskGetThirdpartys(TaskGetThirdpartysCallback listener, PartyType type, String companyIdIfExist) {
        mListener = new WeakReference<TaskGetThirdpartysCallback>(listener);
        mPartyType = type;
        this.companyIdIfExist = companyIdIfExist;
    }

    @Override
    public Void doBackground() {
        switch (mPartyType) {
            case LOGIN:
                getThirdPartyListForLogin();
                break;
            case SHARE:
                getThirdPartyListForShare(companyIdIfExist);
                break;
        }
        return null;
    }

    private static final String APP_ID = "jiangjiang";
    private static final String APP_KEY = MD5Util.MD5("JiangjiangOnceAs");


    private void getThirdPartyListForLogin() {
        final List<ThirdParty> partys = Lists.newArrayList();

        Map<String, String> params = Maps.newHashMap();
        params.put("appID", APP_ID);
        params.put("appKey", APP_KEY);
        String result =
//				UniversalHttp.post(GET_THIRDPARTY_URL, params);
                null;
        if (TextUtils.isEmpty(result)) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    TaskGetThirdpartysCallback listener = mListener.get();
                    if (null != listener) {
                        listener.onConnectFail();
                    }
                }
            });
            return;
        }
        try {
            JSONObject response = new JSONObject(result);
            int resultcode = response.getInt("result");
            if (resultcode == 0) {
                JSONObject returnData = response.getJSONObject("returnData");
                JSONArray companys = returnData.getJSONArray("companys");
                if (companys != null && companys.length() > 0) {
                    for (int i = 0; i < companys.length(); i++) {
                        JSONObject company = (JSONObject) companys.get(i);
                        String name = company.getString("name");
                        String logoUrl = company.getString("logo");

                        String interfaceUrlPrefix = company.getString("interfaceUrlPrefix");
                        String interfaceUrlSuffix = company.getString("interfaceUrlSuffix");

                        String companyId = company.getString("companyId");
                        //
                        ThirdParty p = new ThirdParty();
                        p.setName(name);
                        p.setIconType(ThirdParty.ICON_TYPE_NET);
                        p.setIconUrl(logoUrl);
                        p.setId(ThirdParty.ID_NET_COMPANY);
                        p.setInterfaceUrlPrefix(interfaceUrlPrefix);
                        p.setInterfaceUrlSuffix(interfaceUrlSuffix);
                        p.setAction(ThirdParty.ACTION_LOGIN);
                        p.setCompanyId(companyId);
                        p.setUserType(UserBean.USER_TYPE_PARTNER);

                        partys.add(p);
                    }
                }
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        TaskGetThirdpartysCallback listener = mListener.get();
                        if (null != listener) {
                            listener.onThirdpartyLoaded(partys);
                        }
                    }
                });

            } else {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        TaskGetThirdpartysCallback listener = mListener.get();
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
                    TaskGetThirdpartysCallback listener = mListener.get();
                    if (null != listener) {
                        listener.onResponseFail();
                    }
                }
            });
        }

    }

    private void getThirdPartyListForShare(final String userCompanyId) {
        final List<ThirdParty> partys = Lists.newArrayList();
        {
            ThirdParty p = new ThirdParty();
            p.setName("新浪微博");
            p.setIconType(ThirdParty.ICON_TYPE_RES);
            p.setIconResId(R.drawable.share_platform_sinaweibo);
            p.setId(ThirdParty.ID_SINA_WEIBO);
            partys.add(p);
        }
        {
            ThirdParty p = new ThirdParty();
            p.setName("腾讯微博");
            p.setIconType(ThirdParty.ICON_TYPE_RES);
            p.setIconResId(R.drawable.share_platform_qqweibo);
            p.setId(ThirdParty.ID_QQ_WEIBO);
            partys.add(p);
        }
        {
            ThirdParty p = new ThirdParty();
            p.setName("QQ");
            p.setIconType(ThirdParty.ICON_TYPE_RES);
            p.setIconResId(R.drawable.share_platform_qq);
            p.setId(ThirdParty.ID_QQ);
            partys.add(p);
        }
        //
        {
            ThirdParty p = new ThirdParty();
            p.setName("微信");
            p.setIconType(ThirdParty.ICON_TYPE_RES);
            p.setIconResId(R.drawable.share_platform_wechat_friends);
            p.setId(ThirdParty.ID_WECHART);
            partys.add(p);
        }
        if (TextUtils.isEmpty(userCompanyId)) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    TaskGetThirdpartysCallback listener = mListener.get();
                    if (null != listener) {
                        listener.onThirdpartyLoaded(partys);
                    }
                }
            });
            return;
        }
        // more from net.should use local company id to filter.
        Map<String, String> params = Maps.newHashMap();
        params.put("appID", APP_ID);
        params.put("appKey", APP_KEY);
        String result =
//				UniversalHttp.post(GET_THIRDPARTY_URL, params);
                null;
        if (TextUtils.isEmpty(result)) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    TaskGetThirdpartysCallback listener = mListener.get();
                    if (null != listener) {
                        listener.onThirdpartyLoaded(partys);
                    }
                }
            });
            return;
        }
        try {
            JSONObject response = new JSONObject(result);
            int resultcode = response.getInt("result");
            if (resultcode == 0) {
                JSONObject returnData = response.getJSONObject("returnData");
                JSONArray companys = returnData.getJSONArray("companys");
                if (companys != null && companys.length() > 0) {
                    for (int i = 0; i < companys.length(); i++) {
                        JSONObject company = companys.getJSONObject(i);
                        String companyId = company.getString("companyId");
                        if (!userCompanyId.equals(companyId)) {
                            continue;
                        }

                        JSONArray shareModules = company.getJSONArray("shareModule");
                        if (shareModules == null || shareModules.length() <= 0) {
                            continue;
                        }
                        String name = company.getString("name");
                        String logoUrl = company.getString("logo");
                        String interfaceUrlPrefix = company.getString("interfaceUrlPrefix");
                        String interfaceUrlSuffix = company.getString("interfaceUrlSuffix");

                        for (int j = 0; j < shareModules.length(); j++) {
                            JSONObject mod = shareModules.getJSONObject(j);
                            String modLogo = mod.getString("moduleLogo");
                            String modName = mod.getString("moduleName");
                            String modId = mod.getString("moduleId");
                            //
                            ThirdParty p = new ThirdParty();
                            p.setName(name + "(" + modName + ")");
                            p.setIconType(ThirdParty.ICON_TYPE_NET);
                            p.setIconUrl(modLogo);
                            p.setId(ThirdParty.ID_NET_COMPANY);
                            p.setInterfaceUrlPrefix(interfaceUrlPrefix);
                            p.setInterfaceUrlSuffix(interfaceUrlSuffix);
                            p.setAction(ThirdParty.ACTION_UPLOAD_COURSE);
                            p.setCompanyId(companyId);
                            p.setModuleId(modId);
                            p.setUserType(UserBean.USER_TYPE_PARTNER);

                            partys.add(p);
                        }
                    }
                }
                uiHandler.post(new Runnable() {

                    @Override
                    public void run() {

                        TaskGetThirdpartysCallback listener = mListener.get();
                        if (null != listener) {
                            listener.onThirdpartyLoaded(partys);
                        }
                    }
                });

            } else {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        TaskGetThirdpartysCallback listener = mListener.get();
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
                    TaskGetThirdpartysCallback listener = mListener.get();
                    if (null != listener) {
                        listener.onResponseFail();
                    }
                }
            });
        }
    }
}
