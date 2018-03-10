package com.speektool.impl.platforms;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.http.UniversalHttp;
import com.speektool.Const;
import com.speektool.SpeekToolApp;
import com.speektool.api.CourseItem;
import com.speektool.api.LoginCallback;
import com.speektool.api.ThirdPartyRunState;
import com.speektool.api.ThirdpartyPlatform;
import com.speektool.bean.LocalRecordBean;
import com.speektool.bean.RecordUploadBean;
import com.speektool.bean.UserBean;
import com.speektool.busevents.RefreshCourseListEvent;
import com.speektool.service.UploadService;
import com.speektool.service.UploadService.UploadRequestCode;
import com.speektool.tasks.TaskCheckThirdpartyUserExist;
import com.speektool.tasks.TaskCheckThirdpartyUserExist.CheckThirdpartyUserExistCallback;
import com.speektool.tasks.TaskUserLogin;
import com.speektool.tasks.TaskUserLogin.UserLoginCallback;
import com.speektool.tasks.TaskUserRegister;
import com.speektool.tasks.TaskUserRegister.UserRegisterCallback;
import com.speektool.ui.dialogs.LoadingDialog;
import com.speektool.ui.dialogs.OneButtonAlertDialog;
import com.speektool.utils.MD5Util;
import com.speektool.utils.RecordFileUtils;
import com.speektool.utils.T;

import de.greenrobot.event.EventBus;

/**
 * 第三方平台基础类
 * 
 * @author Maple Shao
 * 
 */
public abstract class BasePaltform implements ThirdpartyPlatform {
	protected static Context mContext;
	protected ThirdPartyRunState mThirdPartyRunState;
	protected LoginCallback mLoginCallback;
	protected CourseItem mCourseItem;
	protected final LoadingDialog mLoadingDialog;

	public BasePaltform(Context context, ThirdPartyRunState mThirdPartyRunState) {
		super();
		this.mContext = context;
		this.mThirdPartyRunState = mThirdPartyRunState;

		mLoadingDialog = new LoadingDialog(mContext);
	}

	@Override
	public void share(CourseItem mItemBean) {
		Preconditions.checkNotNull(mItemBean);
		mCourseItem = mItemBean;
		if (mCourseItem instanceof LocalRecordBean) {
			shareLocalRecord();
		} else {
			shareServerRecord();
		}
	}

	/** 共享服务器记录 */
	private void shareServerRecord() {
		shareUploadedRecord();
	}

	/** 共享本地记录 */
	private void shareLocalRecord() {
		String shareUrl = mCourseItem.getShareUrl();
		if (!TextUtils.isEmpty(shareUrl)) {
			shareUploadedRecord();
		} else {
			// upload then share.
			new Thread(new Runnable() {
				@Override
				public void run() {
					zipAndUploadFile();
				}
			}).start();
		}
	}

	/**
	 * 分享上传记录
	 */
	protected abstract void shareUploadedRecord();

	private void zipAndUploadFile() {
		LocalRecordBean localItem = (LocalRecordBean) mCourseItem;
		RecordUploadBean recordUploadBean = RecordFileUtils
				.getSpklUploadBeanFromDir(localItem.getRecordDir(), mContext);
		if (recordUploadBean == null) {
			T.showShort(mContext, "上传失败！");
			return;
		}
		//
		mThirdPartyRunState.onFinishRun();
		Intent requestUploadIntent = new Intent(mContext, UploadService.class);
		requestUploadIntent.putExtra(UploadService.EXTRA_ACTION, UploadService.ACTION_UPLOAD_TO_SPEAKTOOL);
		requestUploadIntent.putExtra(UploadService.EXTRA_REQUEST_DATA, recordUploadBean);
		requestUploadIntent.putExtra(UploadService.EXTRA_REQUEST_CODE, getUploadRequestCode());
		mContext.startService(requestUploadIntent);
	}

	/** 获取上传请求代码 */
	protected abstract UploadRequestCode getUploadRequestCode();

	/** 检查平台用户是否存在 */
	protected void checkPlatformUserExist(final UserBean thirdPartyUserBean) {
		mLoadingDialog.show();
		new Thread(new TaskCheckThirdpartyUserExist(mCheckThirdpartyUserExistCallback, thirdPartyUserBean)).start();
	}

	/** 用户是否存在回调 */
	private CheckThirdpartyUserExistCallback mCheckThirdpartyUserExistCallback = new CheckThirdpartyUserExistCallback() {
		// 不存在
		@Override
		public void onNotExist(UserBean thirdPartyUserBean) {
			mLoadingDialog.dismiss();
			whenThirdpartyUserNotRegistered(thirdPartyUserBean);
		}

		// 存在
		@Override
		public void onExist(UserBean thirdPartyUserBean) {
			mLoadingDialog.dismiss();
			loginToSpeaktool(thirdPartyUserBean);// 登陆讲讲
		}

		// 连接失败
		@Override
		public void onConnectFail() {
			mLoadingDialog.dismiss();
			T.showShort(mContext, "服务器链接失败！请检查网络");
		}

		// 检查失败
		@Override
		public void onCheckFail() {
			mLoadingDialog.dismiss();
			T.showShort(mContext, "服务器响应错误！");
		}
	};

	/** 第三方平台用户未注册 */
	protected void whenThirdpartyUserNotRegistered(UserBean thirdPartyUserBean) {
		String account = MD5Util.MD5WithServer(thirdPartyUserBean.getWidgetUserId());
		String pwd = MD5Util.MD5WithServer(account + "onceas");

		thirdPartyUserBean.setAccount(account);// 帐号
		thirdPartyUserBean.setPassword(pwd);// 密码 

		if (!TextUtils.isEmpty(thirdPartyUserBean.getPortraitPath())) {// 头像路径不为空
			downloadThirdpartyPortraitAndRegister(thirdPartyUserBean);
		} else {
			mLoadingDialog.show();
			new Thread(new TaskUserRegister(mUserRegisterCallback, thirdPartyUserBean)).start();
		}
	}

	/** 下载头像并注册 */
	protected void downloadThirdpartyPortraitAndRegister(final UserBean thirdPartyUserBean) {
		mLoadingDialog.show();
		new Thread(new Runnable() {   
			@Override
			public void run() {
				// 下载头像到本地
				File tempPortrait = new File(Const.RECORD_DIR, "tempPortrait.jpg");
				File downloadedFile = UniversalHttp.downloadFile(thirdPartyUserBean.getPortraitPath(), tempPortrait);
				// 更新头像路径
				if (downloadedFile != null) {
					thirdPartyUserBean.setPortraitPath(downloadedFile.getAbsolutePath());
				} else {
					thirdPartyUserBean.setPortraitPath(null);
				}
				// 注册
				SpeekToolApp.getUiHandler().post(new Runnable() {
					@Override
					public void run() {
						new Thread(new TaskUserRegister(mUserRegisterCallback, thirdPartyUserBean)).start();
					}
				});
			}
		}).start();
	}

	/**
	 * 用户注册接口回调
	 */
	protected UserRegisterCallback mUserRegisterCallback = new UserRegisterCallback() {
		// 用户已存在
		@Override
		public void onUserAlreadyExist() {
			mLoadingDialog.dismiss();
			OneButtonAlertDialog dia = new OneButtonAlertDialog(mContext, "用户已经存在！");
			dia.show();
		}

		// 注册成功
		@Override
		public void onRegisterSuccess() {
			mLoadingDialog.dismiss();
			EventBus.getDefault().post(new RefreshCourseListEvent());
			if (mLoginCallback != null) {
				mLoginCallback.onLoginFinish(LoginCallback.SUCCESS);
			}
		}

		// 注册失败
		@Override
		public void onRegisterFail() {
			mLoadingDialog.dismiss();
			T.showShort(mContext, "服务器响应错误！");
		}

		// 连接失败
		@Override
		public void onConnectFail() {
			mLoadingDialog.dismiss();
			T.showShort(mContext, "服务器链接失败！请检查网络");
		}
	};

	/** 用户登陆接口回调 */
	private UserLoginCallback mUserLoginCallback = new UserLoginCallback() {
		// 登陆成功
		@Override
		public void onLoginSuccess() {
			mLoadingDialog.dismiss();
			EventBus.getDefault().post(new RefreshCourseListEvent());
			if (mLoginCallback != null) {
				mLoginCallback.onLoginFinish(LoginCallback.SUCCESS);
			}
		}

		// 登陆失败
		@Override
		public void onLoginFail() {
			mLoadingDialog.dismiss();
			OneButtonAlertDialog dia = new OneButtonAlertDialog(mContext, "登录失败！");
			dia.show();
		}

		// 连接失败
		@Override
		public void onConnectFail() {
			mLoadingDialog.dismiss();
			T.showShort(mContext, "服务器链接失败！请检查网络");
		}
	};

	/** 登陆讲讲 */
	protected void loginToSpeaktool(final UserBean thirdPartyReturnUserBean) {
		mLoadingDialog.show();

		String account = MD5Util.MD5WithServer(thirdPartyReturnUserBean.getWidgetUserId());
		String pwd = MD5Util.MD5WithServer(account + "onceas");
		thirdPartyReturnUserBean.setAccount(account);
		thirdPartyReturnUserBean.setPassword(pwd);

		new Thread(new TaskUserLogin(mUserLoginCallback, thirdPartyReturnUserBean)).start();
	}
}
