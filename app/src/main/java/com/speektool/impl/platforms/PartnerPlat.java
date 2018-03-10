package com.speektool.impl.platforms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.common.base.Preconditions;
import com.http.UniversalHttp;
import com.speektool.Const;
import com.speektool.SpeekToolApp;
import com.speektool.activity.WebActivity;
import com.speektool.api.CourseItem;
import com.speektool.api.LoginCallback;
import com.speektool.api.ThirdPartyRunState;
import com.speektool.bean.LocalRecordBean;
import com.speektool.bean.RecordUploadBean;
import com.speektool.bean.ServerRecordBean;
import com.speektool.bean.ThirdParty;
import com.speektool.bean.ThirdpartyRecordUploadBean;
import com.speektool.bean.UserBean;
import com.speektool.dao.UserDatabase;
import com.speektool.manager.AppManager;
import com.speektool.service.UploadService;
import com.speektool.service.UploadService.UploadRequestCode;
import com.speektool.tasks.TaskGetThirdpartyUserInfo;
import com.speektool.tasks.TaskGetThirdpartyUserInfo.GetThirdpartyUserInfoCallback;
import com.speektool.utils.RecordFileUtils;
import com.speektool.utils.T;
import com.speektool.utils.ZipUtils;

/**
 * 合作平台。
 * 
 * @author Maple Shao
 * 
 * 
 */
public class PartnerPlat extends BasePaltform {

	private ThirdParty mThirdParty;
	private LoginCallbackReceiver mLoginCallbackReceiver;

	public PartnerPlat(Context mContext, ThirdPartyRunState mThirdPartyRunState, ThirdParty pThirdParty) {
		super(mContext, mThirdPartyRunState);
		mThirdParty = pThirdParty;
	}

	@Override
	public void login(LoginCallback mLoginCallback) {
		this.mLoginCallback = mLoginCallback;
		//
		String loginUrl;
		if (TextUtils.isEmpty(mThirdParty.getInterfaceUrlSuffix())) {
			loginUrl = String.format("%s/%s", mThirdParty.getInterfaceUrlPrefix(), ThirdParty.ACTION_LOGIN);

		} else {
			loginUrl = String.format("%s/%s%s", mThirdParty.getInterfaceUrlPrefix(), ThirdParty.ACTION_LOGIN,
					mThirdParty.getInterfaceUrlSuffix());
		}
		if (TextUtils.isEmpty(loginUrl)) {
			T.showShort(mContext, "登录失败！");
			return;
		}
		if (mThirdPartyRunState != null) {
			mThirdPartyRunState.onStartRun();
		}
		// 设置回调监听
		mLoginCallbackReceiver = new LoginCallbackReceiver();
		IntentFilter filter = new IntentFilter(LoginCallbackReceiver.ACTION_LOGIN_CALLBACK);
		filter.addAction(LoginCallbackReceiver.ACTION_LOGIN_CANCEL);
		mContext.registerReceiver(mLoginCallbackReceiver, filter);

		toWebPage("", loginUrl);

	}

	/** 去新闻页面 */
	private void toWebPage(String title, String url) {
		Intent intent = new Intent(mContext, WebActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(WebActivity.EXTRA_TITLE, title);// 功能Item
		intent.putExtra(WebActivity.EXTRA_URL, url);// 功能Item
		mContext.startActivity(intent);// 开启目标Activity
	}

	public class LoginCallbackReceiver extends BroadcastReceiver {
		public static final String EXTRA_ACCOUNT = "account";
		public static final String EXTRA_TOKEN = "token";
		public static final String ACTION_LOGIN_CALLBACK = "action.login_callback";
		public static final String ACTION_LOGIN_CANCEL = "action.login_cancel";

		@Override
		public void onReceive(Context context, Intent intent) {
			mContext.unregisterReceiver(mLoginCallbackReceiver);
			if (mThirdPartyRunState != null) {
				mThirdPartyRunState.onFinishRun();
			}
			if (ACTION_LOGIN_CALLBACK.equals(intent.getAction())) {
				String account = intent.getStringExtra(EXTRA_ACCOUNT);
				String token = intent.getStringExtra(EXTRA_TOKEN);
				if (TextUtils.isEmpty(token)) {
					Toast.makeText(mContext, "validate fail,token is null.", 0).show();
					return;
				}
				partnerLoginComplete(account, token);

			} else {
				Log.e("PartnerPlat", "validate be canceled.");
			}
		}
	}

	/** 登陆完成 */
	private void partnerLoginComplete(String account, String token) {
		final UserBean userBean = new UserBean();
		userBean.setType(mThirdParty.getUserType());
		userBean.setWidgetUserId(account);
		userBean.setToken(token);
		userBean.setCompanyId(mThirdParty.getCompanyId());

		SpeekToolApp.getUiHandler().post(new Runnable() {
			@Override
			public void run() {
				checkPlatformUserExist(userBean);
			}
		});

	}

	// 获取第三方用户信息回调
	private GetThirdpartyUserInfoCallback mGetThirdpartyUserInfoCallback = new GetThirdpartyUserInfoCallback() {
		@Override
		public void onSuccess(UserBean thirdPartyUserBean) {
			mLoadingDialog.dismiss();
			whenThirdpartyUserNotRegistered(thirdPartyUserBean);
			// // 获取用户信息完成.开始注册.
			// String account =
			// MD5Util.MD5WithServer(thirdPartyUserBean.getWidgetUserId());
			// String pwd = MD5Util.MD5WithServer(account + "onceas");
			// thirdPartyUserBean.setAccount(account);// 帐号
			// thirdPartyUserBean.setPassword(pwd);// 密码
			//
			// if (!TextUtils.isEmpty(thirdPartyUserBean.getPortraitPath())) {
			// // 下载头像并注册
			// downloadThirdpartyPortraitAndRegister(thirdPartyUserBean);
			// } else {
			// mLoadingDialog.show();
			// new Thread(new TaskUserRegister(mUserRegisterCallback,
			// thirdPartyUserBean)).start();
			// }
		}

		@Override
		public void onResponseFail() {
			mLoadingDialog.dismiss();
			T.showShort(mContext, "从第三方平台获取用户信息失败！");
		}

		@Override
		public void onConnectFail() {
			mLoadingDialog.dismiss();
			T.showShort(mContext, "服务器链接失败！请检查网络");
		}
	};

	@Override
	protected void whenThirdpartyUserNotRegistered(final UserBean thirdPartyUserBean) {
		// get user info then register.use getThirdPartyUserInfo
		String url = null;
		if (TextUtils.isEmpty(mThirdParty.getInterfaceUrlSuffix())) {
			url = String.format("%s/%s", mThirdParty.getInterfaceUrlPrefix(), ThirdParty.ACTION_GET_USERINFO);
		} else {
			url = String.format("%s/%s%s", mThirdParty.getInterfaceUrlPrefix(), ThirdParty.ACTION_GET_USERINFO,
					mThirdParty.getInterfaceUrlSuffix());
		}
		mLoadingDialog.show();
		new Thread(new TaskGetThirdpartyUserInfo(mGetThirdpartyUserInfoCallback, thirdPartyUserBean, url)).start();

	}

	@Override
	public void share(CourseItem mItemBean) {
		// must override .
		Preconditions.checkNotNull(mItemBean);
		mCourseItem = mItemBean;
		if (mCourseItem instanceof LocalRecordBean) {
			// localrecord must zip in android.
			new Thread(new Runnable() {
				@Override
				public void run() {
					shareLocalRecordScript();
				}
			}).start();

		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					downloadAndUpload();
				}
			}).start();
		}
	}

	protected static File[] getUploadFiles(File dir) {
		File[] uploadFiles = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (RecordFileUtils.isReleaseFile(filename))
					return true;
				else
					return false;
			}
		});
		return uploadFiles;
	}

	private void shareLocalRecordScript() {
		LocalRecordBean lLocalRecordBean = (LocalRecordBean) mCourseItem;
		File dir = new File(lLocalRecordBean.getRecordDir());
		final File zip = new File(dir, "record.zip");
		if (!zip.exists()) {
			try {
				File[] recordfiles = getUploadFiles(dir);
				zip.createNewFile();
				// must after list files,otherwise the zip will be zipped.
				ZipUtils.zipFiles(Arrays.asList(recordfiles), zip);
			} catch (Exception e) {
				e.printStackTrace();
				T.showShort(mContext, shareFial);
				return;
			}
		}
		// set info.
		setScriptRecordInfoAndUpload(zip, dir);
	}

	String shareFial = "分享失败";

	private void setScriptRecordInfoAndUpload(File zip, File dir) {
		File infofile = new File(dir, Const.INFO_FILE_NAME);
		if (!infofile.exists()) {
			T.showShort(mContext, shareFial);
			return;
		}
		Properties p = new Properties();
		FileInputStream ins;
		try {
			ins = new FileInputStream(infofile);
			p.load(ins);
		} catch (Exception e) {
			e.printStackTrace();
			T.showShort(mContext, shareFial);
			return;
		}
		String title = p.getProperty(LocalRecordBean.TITLE);
		String thumbnailName = p.getProperty(LocalRecordBean.THUMBNAIL_NAME);
		String tab = p.getProperty(LocalRecordBean.TAB);
		String categoryName = p.getProperty(LocalRecordBean.CATEGORY_NAME);
		String introduce = p.getProperty(LocalRecordBean.INTRODUCE);
		String shareUrl = p.getProperty(LocalRecordBean.SHARE_URL);
		String courseId = p.getProperty(LocalRecordBean.COURSE_ID);

		String w = p.getProperty(LocalRecordBean.MAKE_WINDOW_WIDTH);

		String h = p.getProperty(LocalRecordBean.MAKE_WINDOW_HEIGHT);
		int makeWindowWidth = 0;
		int makeWindowHeight = 0;
		try {
			if (!TextUtils.isEmpty(w)) {
				makeWindowWidth = Integer.valueOf(w);
			}
			if (!TextUtils.isEmpty(h)) {
				makeWindowHeight = Integer.valueOf(h);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			ins.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String imagepath = String.format("%s%s%s", dir.getAbsolutePath(), File.separator, thumbnailName);
		//
		final ThirdpartyRecordUploadBean recordUploadBean = new ThirdpartyRecordUploadBean();
		recordUploadBean.setZipFilePath(zip.getAbsolutePath());
		recordUploadBean.setCourseType(RecordUploadBean.COURSE_TYPE_SCRIPT);

		UserBean session = UserDatabase.getUserLocalSession(mContext);
		String widgetUserId = session.getWidgetUserId();
		String token = session.getToken();
		recordUploadBean.setThirdpartyUseAccount(widgetUserId);
		recordUploadBean.setToken(token);
		recordUploadBean.setAppSign(AppManager.getAppSignEncodedByMd5(mContext));
		recordUploadBean.setModuleId(mThirdParty.getModuleId());
		//
		recordUploadBean.setTitle(title);

		recordUploadBean.setThumbNailPath(imagepath);
		recordUploadBean.setTab(tab);
		recordUploadBean.setType(categoryName);
		recordUploadBean.setIntroduce(introduce);
		long duration = RecordFileUtils.getRecordDuration(dir.getAbsolutePath());
		recordUploadBean.setDuration(duration);
		recordUploadBean.setMakeWindowWidth(makeWindowWidth);
		recordUploadBean.setMakeWindowHeight(makeWindowHeight);
		recordUploadBean.setCourseId(courseId);
		upload(recordUploadBean);
	}

	protected void deleteDir(File dir) {
		if (dir == null || !dir.exists())
			return;
		File[] files = dir.listFiles();
		if (files != null) {
			for (File f : files)
				f.delete();
		}
		dir.delete();
	}

	private void downloadAndUploadZipScript(final String zipurlfile) {
		// download record zip starting.
		final File tempzip = new File(Const.TEMP_DIR, "temp.zip");

		if (tempzip.exists()) {
			tempzip.delete();
		}
		try {
			tempzip.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			T.showShort(mContext, shareFial);
			return;
		}
		//
		final String zipurlfileReal = Const.SPEEKTOOL_SERVER__URL + zipurlfile;
		File zip = UniversalHttp.downloadFile(zipurlfileReal, "", tempzip);
		if (zip != null) {// download success.
			final File temp = new File(Const.TEMP_DIR + "temp/");
			if (temp.exists()) {
				deleteDir(temp);
			}
			temp.mkdirs();
			// unzip temp.zip to temp dir.
			try {
				ZipUtils.upZipFile(tempzip, temp.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
				T.showShort(mContext, shareFial);
				return;
			}
			// set info.
			setScriptRecordInfoAndUpload(zip, temp);
		} else {
			T.showShort(mContext, shareFial);
		}
	}

	private void downloadAndUpload() {
		ServerRecordBean serverItem = (ServerRecordBean) mCourseItem;
		final String zipurlfile = serverItem.getZipURL();
		final String videoFileUrl = serverItem.getVideoURL();
		if (!TextUtils.isEmpty(zipurlfile)) {
			downloadAndUploadZipScript(zipurlfile);
		} else if (!TextUtils.isEmpty(videoFileUrl)) {// mp4.
			downloadAndUploadVideo(videoFileUrl);
		} else {
			T.showShort(mContext, "分享失败！");
		}
	}

	private void downloadAndUploadVideo(final String videoFileUrl) {
		// download mp4 starting.
		final File tempMp4 = new File(Const.TEMP_DIR, "temp.mp4");
		if (tempMp4.exists()) {
			tempMp4.delete();
		}
		try {
			tempMp4.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			T.showShort(mContext, shareFial);
			return;
		}
		//
		final String videoFileUrlReal = Const.SPEEKTOOL_SERVER__URL + videoFileUrl;
		File saveFile = UniversalHttp.downloadFile(videoFileUrlReal, "", tempMp4);
		if (saveFile != null) {// download success.
			ServerRecordBean serverBean = (ServerRecordBean) mCourseItem;
			String thumbUrl = serverBean.getThumbnailImgPath();
			String imagepath = null;
			if (!TextUtils.isEmpty(thumbUrl)) {
				// download thumb jpg.
				final File tempThumb = new File(Const.TEMP_DIR, "temp.jpg");
				if (tempThumb.exists()) {
					tempThumb.delete();
				}
				try {
					tempThumb.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				final String thumbFileUrlReal = Const.SPEEKTOOL_SERVER__URL + thumbUrl;
				File thumb = UniversalHttp.downloadFile(thumbFileUrlReal, "", tempThumb);
				if (thumb != null) {
					imagepath = thumb.getAbsolutePath();
				}
			}

			final ThirdpartyRecordUploadBean recordUploadBean = new ThirdpartyRecordUploadBean();
			recordUploadBean.setZipFilePath(null);
			recordUploadBean.setVideoFilePath(saveFile.getAbsolutePath());
			recordUploadBean.setCourseType(RecordUploadBean.COURSE_TYPE_VIDEO);

			UserBean session = UserDatabase.getUserLocalSession(mContext);
			String widgetUserId = session.getWidgetUserId();
			String token = session.getToken();
			recordUploadBean.setThirdpartyUseAccount(widgetUserId);
			recordUploadBean.setToken(token);
			recordUploadBean.setAppSign(AppManager.getAppSignEncodedByMd5(mContext));
			recordUploadBean.setModuleId(mThirdParty.getModuleId());

			String title = serverBean.getRecordTitle();

			String tab = serverBean.getCourseTag();
			String categoryName = serverBean.getCategoryName();
			String introduce = serverBean.getIntroduce();
			String courseId = serverBean.getCourseId();
			int makeWindowWidth = -1;//
			int makeWindowHeight = -1;
			long duration = serverBean.getDuration();

			recordUploadBean.setTitle(title);
			recordUploadBean.setThumbNailPath(imagepath);
			recordUploadBean.setTab(tab);
			recordUploadBean.setType(categoryName);
			recordUploadBean.setIntroduce(introduce);

			recordUploadBean.setDuration(duration);
			recordUploadBean.setMakeWindowWidth(makeWindowWidth);
			recordUploadBean.setMakeWindowHeight(makeWindowHeight);
			recordUploadBean.setCourseId(courseId);
			upload(recordUploadBean);

		} else {
			T.showShort(mContext, shareFial);
		}

	}

	private void upload(ThirdpartyRecordUploadBean uploadBean) {
		String uploadUrl = null;

		if (TextUtils.isEmpty(mThirdParty.getInterfaceUrlSuffix())) {
			uploadUrl = String.format("%s/%s", mThirdParty.getInterfaceUrlPrefix(), ThirdParty.ACTION_UPLOAD_COURSE);
		} else {
			uploadUrl = String.format("%s/%s%s", mThirdParty.getInterfaceUrlPrefix(), ThirdParty.ACTION_UPLOAD_COURSE,
					mThirdParty.getInterfaceUrlSuffix());
		}
		uploadBean.setUploadUrl(uploadUrl);

		mThirdPartyRunState.onFinishRun();
		Intent requestUploadIntent = new Intent(mContext, UploadService.class);
		requestUploadIntent.putExtra(UploadService.EXTRA_ACTION, UploadService.ACTION_UPLOAD_TO_THIRDPARTY);
		requestUploadIntent.putExtra(UploadService.EXTRA_REQUEST_DATA, uploadBean);
		requestUploadIntent.putExtra(UploadService.EXTRA_REQUEST_CODE, getUploadRequestCode());
		mContext.startService(requestUploadIntent);

	}

	@Override
	protected void shareUploadedRecord() {
	}

	@Override
	protected UploadRequestCode getUploadRequestCode() {
		return UploadRequestCode.SHARE_PARTNER;
	}
}
