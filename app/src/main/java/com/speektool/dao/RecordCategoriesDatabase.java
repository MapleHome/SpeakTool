package com.speektool.dao;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;

import com.google.common.collect.Lists;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.speektool.R;
import com.speektool.bean.SearchCategoryBean;
import com.speektool.busevents.RecordTypeChangedEvent;

import de.greenrobot.event.EventBus;

/**
 * 课程记录 类别 数据库（本地+服务器= 所有类型）
 * 
 * @author shaoshuai
 * 
 */
public class RecordCategoriesDatabase {

	private static DatabaseHelper getHelper(Context context) {
		return OpenHelperManager.getHelper(context, DatabaseHelper.class);
	}

	/**
	 * 获得本地类别
	 * 
	 * @param context
	 * @param isNeedAllType
	 * @return
	 */
	public static List<SearchCategoryBean> getLocalCategories(Context context,
			boolean isNeedAllType) {
		final DatabaseHelper helper = getHelper(context);
		List<SearchCategoryBean> uploads = null;
		try {
			final Dao<SearchCategoryBean, String> dao = helper
					.getRecordTypeDao();
			uploads = dao.queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			OpenHelperManager.releaseHelper();
		}
		if (isNeedAllType) {
			SearchCategoryBean first = new SearchCategoryBean();
			first.setCategoryId(SearchCategoryBean.CID_ALL);
			first.setCategoryName(context.getString(R.string.allRecordType));
			if (uploads == null)
				uploads = Lists.newArrayList();
			uploads.add(0, first);
		}
		return uploads;

	}

	/**
	 * 添加类别
	 * 
	 * @param type
	 * @param context
	 */
	public static void addCategory(SearchCategoryBean type, Context context) {

		final DatabaseHelper helper = getHelper(context);
		try {
			Dao<SearchCategoryBean, String> dao = helper.getRecordTypeDao();
			int affect = dao.create(type);
			if (affect > 0)
				postEvent();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			OpenHelperManager.releaseHelper();
		}

	}

	/**
	 * 更新类别
	 * 
	 * @param type
	 * @param context
	 */
	public static void updateCategory(SearchCategoryBean type, Context context) {

		final DatabaseHelper helper = getHelper(context);
		try {
			Dao<SearchCategoryBean, String> dao = helper.getRecordTypeDao();
			dao.createOrUpdate(type);
			postEvent();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			OpenHelperManager.releaseHelper();
		}

	}

	/**
	 * 删除类别
	 * 
	 * @param type
	 *            类别
	 * @param context
	 */
	public static void deleteCategory(SearchCategoryBean type, Context context) {

		final DatabaseHelper helper = getHelper(context);
		try {
			Dao<SearchCategoryBean, String> dao = helper.getRecordTypeDao();
			int affect = dao.delete(type);
			if (affect > 0)
				postEvent();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			OpenHelperManager.releaseHelper();
		}
	}

	public static boolean isCategoryExist(SearchCategoryBean type,
			Context context) {

		final DatabaseHelper helper = getHelper(context);
		List<SearchCategoryBean> uploads = null;
		try {
			final Dao<SearchCategoryBean, String> dao = helper
					.getRecordTypeDao();
			uploads = dao.queryForEq(SearchCategoryBean.FIELD_TYPE_NAME,
					type.getCategoryName());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			OpenHelperManager.releaseHelper();
		}
		if (uploads != null && !uploads.isEmpty())
			return true;
		else
			return false;
	}

	private static void postEvent() {
		EventBus.getDefault().post(new RecordTypeChangedEvent());

	}
}
