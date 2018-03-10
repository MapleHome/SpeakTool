package com.speektool.dao;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.speektool.bean.UserBean;

/**
 * 用户 数据库
 * 
 * @author shaoshuai
 * 
 */
public class UserDatabase {
	private static DatabaseHelper getHelper(Context context) {
		return OpenHelperManager.getHelper(context, DatabaseHelper.class);
	}

	/**
	 * 保存用户本地会话
	 * 
	 * @param userBean
	 * @param context
	 */
	public static void saveUserLocalSession(UserBean userBean, Context context) {
		userBean.setLoginState(UserBean.STATE_IN);
		final DatabaseHelper helper = getHelper(context);
		try {
			Dao<UserBean, String> dao = helper.getUserDao();
			dao.createOrUpdate(userBean);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			OpenHelperManager.releaseHelper();
		}
	}

	/**
	 * 删除用户本地会话
	 * 
	 * @param userBean
	 * @param context
	 */
	public static void deleteUserLocalSession(UserBean userBean, Context context) {
		userBean.setLoginState(UserBean.STATE_OUT);
		final DatabaseHelper helper = getHelper(context);
		try {
			Dao<UserBean, String> dao = helper.getUserDao();
			// int affect = dao.delete(user);
			dao.createOrUpdate(userBean);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			OpenHelperManager.releaseHelper();
		}
	}

	/**
	 * 获取本地用户
	 * 
	 * @param context
	 * @return
	 */
	public static UserBean getUserLocalSession(Context context) {

		final DatabaseHelper helper = getHelper(context);
		List<UserBean> sessions = null;
		try {
			final Dao<UserBean, String> dao = helper.getUserDao();
			sessions = dao.queryForEq("loginState", UserBean.STATE_IN);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			OpenHelperManager.releaseHelper();
		}
		if (sessions != null && !sessions.isEmpty())
			return sessions.get(0);
		else
			return null;
	}

	/**
	 * 获取用户登陆状态
	 * 
	 * @param context
	 * @return
	 */
	public static int getUserLoginState(Context context) {
		UserBean session = getUserLocalSession(context);
		if (session == null)
			return UserBean.STATE_OUT;
		return session.getLoginState();
	}
}
