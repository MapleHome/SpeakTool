package com.speaktool.utils;


import android.text.TextUtils;

import com.google.gson.Gson;
import com.speaktool.bean.UserBean;

/**
 * User Utils
 *
 * @author maple
 * @time 2019/1/3
 */
public class UserSPUtils {
    private static String user_key = "user";

    public void setUser(UserBean userBean) {
        String userStr = new Gson().toJson(userBean);
        new SPUtils().put(user_key, userStr);
    }

    public UserBean getUser() {
        String userStr = new SPUtils().getString(user_key, "");
        if (TextUtils.isEmpty(userStr)) {
            return new UserBean();
        } else {
            return new Gson().fromJson(userStr, UserBean.class);
        }
    }

    public void setUserActivity(Boolean activity) {
        UserBean userBean = getUser();
        userBean.setActivity(activity);
        setUser(userBean);
    }

}