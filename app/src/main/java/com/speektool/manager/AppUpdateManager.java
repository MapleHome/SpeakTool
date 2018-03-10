package com.speektool.manager;

import java.io.File;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;

import com.ishare_lib.ui.dialog.AlertDialog;
import com.speektool.Const;
import com.speektool.R;
import com.speektool.tasks.TaskCheckAppUpdate;
import com.speektool.tasks.TaskCheckAppUpdate.CheckAppUpdateCallback;
import com.speektool.tasks.ThreadPoolWrapper;
import com.speektool.ui.dialogs.LoadingDialogHelper;
import com.speektool.utils.SPUtils;
import com.speektool.utils.T;

/**
 * 应用更新- 不要创建新实例作为局部变量,否则无法运行回调
 * 
 * @author lich
 * 
 */
public class AppUpdateManager {

	private Context mContext;
	private ThreadPoolWrapper singleExecutor;
	private boolean isNeedShowLoading;

	private LoadingDialogHelper mLoadingDialogHelper;
	public static final String PREF_APP_DOWNLOAD_ID = "app_download_id";
	private static final String UPDATE_APK_NAME = "speaktool_update.apk";

	public AppUpdateManager(Context context, ThreadPoolWrapper singleExecutor, boolean isNeedShowLoading) {
		super();
		this.mContext = context;
		this.singleExecutor = singleExecutor;
		this.isNeedShowLoading = isNeedShowLoading;
		mLoadingDialogHelper = new LoadingDialogHelper(context);
	}

	/**
	 * 检查更新APP
	 */
	public void checkAppUpdate() {
		if (isNeedShowLoading) {
			mLoadingDialogHelper.showLoading("正在检查更新...", null, true);
		}
		singleExecutor.execute(new TaskCheckAppUpdate(mCheckAppUpdateCallback));
	}

	// 检查更新回调
	private final CheckAppUpdateCallback mCheckAppUpdateCallback = new CheckAppUpdateCallback() {
		// 推荐更新
		@Override
		public void onReccomendUpdate(final String updateUrl, String versionNameServer, String updateNote) {
			if (isNeedShowLoading) {
				mLoadingDialogHelper.dismissLoading();
			}
			String msg = mContext.getString(R.string.app_update_note,// 重要性:%1$s\n当前版本:%2$s\n最新版本:%3$s\n更新内容:\n%4$s
					mContext.getString(R.string.app_update_reccommandMsg),// 本版本可以选择性更新。
					AppManager.getCurrentAppVersionName(mContext), versionNameServer, updateNote);
			// 显示推荐更新对话框.
			new AlertDialog(mContext).builder().setTitle("讲讲更新提示").setMsg(msg)
					.setPositiveButton("下载", new OnClickListener() {
						@Override
						public void onClick(View v) {
							downloadApp(updateUrl);// download.
						}
					}).setNegativeButton("取消", new OnClickListener() {
						@Override
						public void onClick(View v) {
						}
					}).show();
		}

		// 强制更新
		@Override
		public void onNeedForceUpdate(final String updateUrl, String versionNameServer, String updateNote) {
			if (isNeedShowLoading) {
				mLoadingDialogHelper.dismissLoading();
			}
			// 显示推荐更新对话框.
			String msg = mContext.getString(R.string.app_update_note,// 重要性:%1$s\n当前版本:%2$s\n最新版本:%3$s\n更新内容:\n%4$s
					mContext.getString(R.string.app_update_forceMsg),// 本版本api接口需要强制更新，否则将导致无法正常使用。
					AppManager.getCurrentAppVersionName(mContext), versionNameServer, updateNote);
			new AlertDialog(mContext).builder().setTitle("讲讲更新提示").setMsg(msg)
					.setPositiveButton("下载", new OnClickListener() {
						@Override
						public void onClick(View v) {
							downloadApp(updateUrl);// download.
						}
					}).setNegativeButton("取消", new OnClickListener() {
						@Override
						public void onClick(View v) {
						}
					}).show();

		}

		// 失败
		@Override
		public void onFail() {
			if (isNeedShowLoading) {
				mLoadingDialogHelper.dismissLoading();
				T.showShort(mContext, "检查应用程序更新失败！");
			}
		}

		// 不需要更新
		@Override
		public void onNoNeedUpdate() {
			if (isNeedShowLoading) {
				mLoadingDialogHelper.dismissLoading();
				T.showShort(mContext, "版本已经最新！");
			}
		}
	};

	private void downloadApp(final String apkUrl) {
		T.showShort(mContext, "成功添加更新下载任务！");
		File savefile = new File(Const.DOWNLOAD_DIR, UPDATE_APK_NAME);
		if (savefile.exists()) {
			savefile.delete();
		}

		DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));

		request.setDestinationUri(Uri.fromFile(savefile));
		request.setTitle(mContext.getString(R.string.app_name));
		// request.setDescription("MeiLiShuo desc");
		// request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		// request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
		// request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
		// request.setMimeType("application/cn.trinea.download.file");
		long downloadId = downloadManager.enqueue(request);
		SPUtils.putLong(PREF_APP_DOWNLOAD_ID, downloadId);

	}
}
