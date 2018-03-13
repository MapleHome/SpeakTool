package com.speaktool.dao;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.speaktool.R;
import com.speaktool.bean.SearchCategoryBean;
import com.speaktool.bean.UserBean;

/**
 * 数据库助手类用于管理数据库的创建和升级。同时这类通常提供的其他类使用DAO。
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final Class<?>[] DATA_CLASSES = { SearchCategoryBean.class, UserBean.class };

	public static final String DATABASE_NAME = "spktl.db";
	private static final int DATABASE_VERSION = 2;

	private Dao<SearchCategoryBean, String> mRecordTypeDao = null;
	private Dao<UserBean, String> mUserDao = null;
	private Context mContext;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mContext = context.getApplicationContext();
	}

	/**
	 * 第一次创建数据库时调用。通常调用create table语句来创建表并存储数据。
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			for (Class<?> dataClass : DATA_CLASSES) {
				TableUtils.createTable(connectionSource, dataClass);
			}
			// 初始化记录类型。
			String[] nativeTypes = mContext.getResources().getStringArray(
					R.array.native_recordTypes);
			if (nativeTypes == null || nativeTypes.length == 0)
				return;
			SearchCategoryBean type = null;
			for (String typename : nativeTypes) {
				type = new SearchCategoryBean();
				type.setCategoryName(typename);
				RecordCategoriesDatabase.addCategory(type, mContext);// 死循环。
			}
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "无法创建数据库", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * This is called when your application is upgraded and it has a higher
	 * version number. This allows you to adjust the various data to match the
	 * new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
			int oldVersion, int newVersion) {
		try {
			for (Class<?> dataClass : DATA_CLASSES) {
				TableUtils.dropTable(connectionSource, dataClass, true);
			}
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "无法结束数据库", e);
			throw new RuntimeException(e);
		}
	}

	public Dao<SearchCategoryBean, String> getRecordTypeDao()
			throws SQLException {
		if (mRecordTypeDao == null) {
			mRecordTypeDao = getDao(SearchCategoryBean.class);
		}
		return mRecordTypeDao;
	}

	public Dao<UserBean, String> getUserDao() throws SQLException {
		if (mUserDao == null) {
			mUserDao = getDao(UserBean.class);
		}
		return mUserDao;
	}

	/**
	 * 关闭数据库连接和清除所有缓存DAO。
	 */
	@Override
	public void close() {
		// OpenHelperManager.getHelper(context, DatabaseHelper.class);
		// OpenHelperManager.releaseHelper();
		mRecordTypeDao = null;
		mUserDao = null;
		super.close();
	}
}