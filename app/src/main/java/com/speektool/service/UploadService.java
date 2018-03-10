package com.speektool.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.aidl.UploadAIDL;
import com.google.common.collect.Maps;
import com.speektool.Const;
import com.speektool.bean.LocalRecordBean;
import com.speektool.bean.RecordUploadBean;
import com.speektool.bean.ThirdpartyRecordUploadBean;
import com.speektool.tasks.CancelableRunnable.Tag;
import com.speektool.tasks.UploadFileRunnable;
import com.speektool.tasks.UploadFileRunnable.UploadCallback;

/**
 * 上传下载服务
 * 
 * @author shaoshuai
 * 
 */
public class UploadService extends Service {
	public static final String tag = UploadService.class.getSimpleName();

	public static final String EXTRA_REQUEST_CODE = "request_code";
	public static final String EXTRA_ACTION = "action";
	public static final String EXTRA_REQUEST_DATA = "data";
	/** 上传到讲讲 */
	public static final String ACTION_UPLOAD_TO_SPEAKTOOL = "action.upload_to_speaktool";
	/** 上传到第三方 */
	public static final String ACTION_UPLOAD_TO_THIRDPARTY = "action.upload_to_thirdparty";
	public static final String ACTION_CANCEL_UPLOAD = "action.cancel_upload";// 取消上传
	public static final String EXTRA_CANCEL_TAG = "cancel_tag";
	public static final String EXTRA_RESULT_CODE = "result_code";// onstart,onfinish...
	public static final String ACTION_UPLOAD_STATE = "action.upload_state";
	public static final String EXTRA_RESULT_DATA = "result_data";

	/** 上传结果代码 */
	public static enum UploadResultCode implements Serializable {
		/** 开始 */
		START,
		/** 取消 */
		CANCEL,
		/** 成功 */
		SUCCESS,
		/** 失败 */
		FAIL,
		/** 进度 */
		PROGRESS,
		/** 已经存在 */
		ALREADY_EXIST,
		/** 令牌无效 */
		TOKEN_INVALID;
	}

	/** 上传请求方式 */
	public static enum UploadRequestCode implements Serializable {
		/** 分享新浪微博 */
		SHARE_SINA_WEIBO,
		/** 分享QQ */
		SHARE_QQ,
		/** 分享腾讯微博 */
		SHARE_TENCENT_WEIBO,
		/** 分享微信 */
		SHARE_WECHART,
		/** 分享伙伴 */
		SHARE_PARTNER,
		/** 只上传 */
		JUST_UPLOAD,
		/** 拷贝连接 */
		COPY_LINK;
	}

	private static final int NOTIFICATION_ID = 888;

	@Override
	public void onCreate() {
		startForeground();
		super.onCreate();
	}

	private void startForeground() {
		try {
			final Notification notification = new Notification();
			startForeground(NOTIFICATION_ID, notification);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
	}

	private void stopForeground() {
		try {
			stopForeground(true);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		stopForeground();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new UploadBinder();
	}

	public class UploadBinder extends UploadAIDL.Stub {
		@Override
		public boolean isUploading(String courseKey) throws RemoteException {
			return tags.get(courseKey) != null;
		}
	}

	/** 执行服务 */
	private ExecutorService mExecutor = Executors.newFixedThreadPool(5);
	private Map<String, Tag> tags = Maps.newHashMap();

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 崩溃重启时，这可能是空的.
		if (intent == null || intent.getStringExtra(EXTRA_ACTION) == null) {
			return super.onStartCommand(intent, flags, startId);
		}
		final UploadRequestCode requestCode = (UploadRequestCode) intent.getSerializableExtra(EXTRA_REQUEST_CODE);// sina,qq...
		Object data = intent.getSerializableExtra(EXTRA_REQUEST_DATA);
		String extraAction = intent.getStringExtra(EXTRA_ACTION);// spktoo,moodle.

		if (ACTION_UPLOAD_TO_SPEAKTOOL.equals(extraAction)) {
			// 检查上传。
			final RecordUploadBean recordUploadBean = (RecordUploadBean) data;
			if (tags.get(recordUploadBean.getThumbNailPath()) != null) {
				Log.e(tag, "已上传，拒绝");
				return super.onStartCommand(intent, flags, startId);
			}
			// 上传到讲讲
			uploadSpeakTool(requestCode, recordUploadBean);

		} else if (ACTION_UPLOAD_TO_THIRDPARTY.equals(extraAction)) {
			// 检查上传。
			final ThirdpartyRecordUploadBean recordUploadBean = (ThirdpartyRecordUploadBean) data;
			if (tags.get(recordUploadBean.getThumbNailPath()) != null) {
				Log.e(tag, "已上传，拒绝");
				return super.onStartCommand(intent, flags, startId);
			}
			// 上传到第三方
			uploadThirdParty(requestCode, recordUploadBean);

		} else if (ACTION_CANCEL_UPLOAD.equals(extraAction)) {// 取消上传
			String cancelTag = intent.getStringExtra(EXTRA_CANCEL_TAG);// 获取图片
			Tag tag = tags.get(cancelTag);
			if (tag != null) {
				tag.isCanceled = true;
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 上传到讲讲
	 * 
	 * @param requestCode
	 * @param recordUploadBean
	 */
	private void uploadSpeakTool(final UploadRequestCode requestCode, final RecordUploadBean recordUploadBean) {
		// 开始上传
		Tag cancelTag = new Tag();
		tags.put(recordUploadBean.getThumbNailPath(), cancelTag);
		// 信息说明
		Map<String, String> textParams = Maps.newHashMap();
		textParams.put("courseId", recordUploadBean.getCourseId());
		textParams.put("uid", recordUploadBean.getUid());
		textParams.put("courseName", recordUploadBean.getTitle());
		textParams.put("courseTag", recordUploadBean.getTab());
		textParams.put("categoryName", recordUploadBean.getType());
		textParams.put("courseIntro", recordUploadBean.getIntroduce());
		textParams.put("duration", recordUploadBean.getDuration() / 1000 + "");
		textParams.put("courseType", recordUploadBean.getCourseType() + "");
		textParams.put("width", recordUploadBean.getMakeWindowWidth() + "");
		textParams.put("height", recordUploadBean.getMakeWindowHeight() + "");
		// 文件类
		Map<String, File> fileParams = Maps.newHashMap();
		if (!TextUtils.isEmpty(recordUploadBean.getThumbNailPath())) {
			File photo = new File(recordUploadBean.getThumbNailPath());
			fileParams.put("photoFile", photo);
		}
		if (!TextUtils.isEmpty(recordUploadBean.getVideoFilePath())) {
			File f = new File(recordUploadBean.getVideoFilePath());
			fileParams.put("videoFile", f);
		}
		if (!TextUtils.isEmpty(recordUploadBean.getZipFilePath())) {
			File f = new File(recordUploadBean.getZipFilePath());
			fileParams.put("zipFile", f);
		}
		// 本地记录
		final LocalRecordBean lLocalRecordBean = new LocalRecordBean();
		lLocalRecordBean.setIntroduce(recordUploadBean.getIntroduce());
		lLocalRecordBean.setCourseId(recordUploadBean.getCourseId());
		lLocalRecordBean.setThumbnailImgPath(recordUploadBean.getThumbNailPath());
		lLocalRecordBean.setRecordTitle(recordUploadBean.getTitle());
		// 上传文件
		mExecutor.execute(new UploadFileRunnable(cancelTag, recordUploadBean.getUploadUrl(), textParams, fileParams,
				new UploadCallback() {
					/** 上传成功 */
					@Override
					public void onSuccess(String result) {
						tags.remove(recordUploadBean.getThumbNailPath());
						try {
							JSONObject response = new JSONObject(result);
							int resultcode = response.getInt("result");
							Log.e(tag, "上传zip文件的结果:" + resultcode);
							Log.e(tag, "上传zip文件的响应:" + response.toString());
							if (resultcode == 0) {
								JSONObject returnData = response.getJSONObject("returnData");
								String shareUrl = returnData.getString("url");
								String courseId = returnData.getString("courseId");
								// 必须在这里做，因为用户界面可能关闭。
								updateLocalCourseProperty(shareUrl, recordUploadBean.getRecordDir(), courseId);
								lLocalRecordBean.setShareUrl(shareUrl);
								Intent stateIntent = new Intent(ACTION_UPLOAD_STATE);
								stateIntent.putExtra(EXTRA_RESULT_CODE, UploadResultCode.SUCCESS);
								stateIntent.putExtra(EXTRA_RESULT_DATA, lLocalRecordBean);
								stateIntent.putExtra(EXTRA_REQUEST_CODE, requestCode);
								sendBroadcast(stateIntent);
								// 上传成功后删除压缩。
								File zip = new File(recordUploadBean.getZipFilePath());
								if (zip.exists()) {
									zip.delete();
								}
							} else {//
								sendFailCast();
							}
						} catch (JSONException e) {
							e.printStackTrace();
							sendFailCast();
						}
					}

					/** 上传进度 */
					@Override
					public void onProgressChanged(int progress) {
						lLocalRecordBean.setProgress(progress);
						Intent stateIntent = new Intent(ACTION_UPLOAD_STATE);
						stateIntent.putExtra(EXTRA_RESULT_CODE, UploadResultCode.PROGRESS);
						stateIntent.putExtra(EXTRA_RESULT_DATA, lLocalRecordBean);
						stateIntent.putExtra(EXTRA_REQUEST_CODE, requestCode);
						sendBroadcast(stateIntent);
					}

					/** 上传失败 */
					@Override
					public void onFail() {
						tags.remove(recordUploadBean.getThumbNailPath());
						sendFailCast();
					}

					/** 取消上传 */
					@Override
					public void onCancel() {
						tags.remove(recordUploadBean.getThumbNailPath());
						Intent stateIntent = new Intent(ACTION_UPLOAD_STATE);
						stateIntent.putExtra(EXTRA_RESULT_CODE, UploadResultCode.CANCEL);
						stateIntent.putExtra(EXTRA_RESULT_DATA, lLocalRecordBean);
						stateIntent.putExtra(EXTRA_REQUEST_CODE, requestCode);
						sendBroadcast(stateIntent);
					}

					/** 上传开始 */
					@Override
					public void onStart() {
						Intent stateIntent = new Intent(ACTION_UPLOAD_STATE);
						stateIntent.putExtra(EXTRA_RESULT_CODE, UploadResultCode.START);
						stateIntent.putExtra(EXTRA_RESULT_DATA, lLocalRecordBean);
						stateIntent.putExtra(EXTRA_REQUEST_CODE, requestCode);
						sendBroadcast(stateIntent);
					}

					private void sendFailCast() {
						Intent stateIntent = new Intent(ACTION_UPLOAD_STATE);
						stateIntent.putExtra(EXTRA_RESULT_CODE, UploadResultCode.FAIL);
						stateIntent.putExtra(EXTRA_RESULT_DATA, lLocalRecordBean);
						stateIntent.putExtra(EXTRA_REQUEST_CODE, requestCode);
						sendBroadcast(stateIntent);
					}
				}));
	}

	/**
	 * 上传到第三方
	 * 
	 * @param requestCode
	 * @param recordUploadBean
	 */
	private void uploadThirdParty(final UploadRequestCode requestCode, final ThirdpartyRecordUploadBean recordUploadBean) {
		// 开始上传
		Tag cancelTag = new Tag();
		tags.put(recordUploadBean.getThumbNailPath(), cancelTag);
		Map<String, String> textParams = Maps.newHashMap();
		textParams.put("courseUid", recordUploadBean.getCourseId());
		textParams.put("account", recordUploadBean.getThirdpartyUseAccount());
		textParams.put("token", recordUploadBean.getToken());
		textParams.put("appSign", recordUploadBean.getAppSign());
		textParams.put("moduleId", recordUploadBean.getModuleId());
		textParams.put("courseName", recordUploadBean.getTitle());
		textParams.put("courseTag", recordUploadBean.getTab());
		textParams.put("categoryName", recordUploadBean.getType());
		textParams.put("courseIntro", recordUploadBean.getIntroduce());
		textParams.put("duration", recordUploadBean.getDuration() / 1000 + "");
		textParams.put("courseType", recordUploadBean.getCourseType() + "");
		textParams.put("width", recordUploadBean.getMakeWindowWidth() + "");
		textParams.put("height", recordUploadBean.getMakeWindowHeight() + "");
		Map<String, File> fileParams = Maps.newHashMap();
		if (!TextUtils.isEmpty(recordUploadBean.getThumbNailPath())) {
			File photo = new File(recordUploadBean.getThumbNailPath());
			fileParams.put("photoFile", photo);
		}
		//
		if (!TextUtils.isEmpty(recordUploadBean.getVideoFilePath())) {
			File f = new File(recordUploadBean.getVideoFilePath());
			fileParams.put("videoFile", f);
		}
		//
		if (!TextUtils.isEmpty(recordUploadBean.getZipFilePath())) {
			File f = new File(recordUploadBean.getZipFilePath());
			fileParams.put("zipFile", f);
		}
		// 初始化本地记录
		final LocalRecordBean lLocalRecordBean = new LocalRecordBean();
		lLocalRecordBean.setIntroduce(recordUploadBean.getIntroduce());
		lLocalRecordBean.setCourseId(recordUploadBean.getCourseId());
		lLocalRecordBean.setThumbnailImgPath(recordUploadBean.getThumbNailPath());
		lLocalRecordBean.setRecordTitle(recordUploadBean.getTitle());
		//
		mExecutor.execute(new UploadFileRunnable(cancelTag, recordUploadBean.getUploadUrl(), textParams, fileParams,
				new UploadCallback() {
					@Override
					public void onSuccess(String result) {
						Log.e(tag, "上传zip文件的结果字符串：" + result);
						tags.remove(recordUploadBean.getThumbNailPath());
						try {
							JSONObject response = new JSONObject(result);
							int resultcode = response.getInt("result");
							Log.e(tag, "上传zip文件结果代码:" + resultcode);
							Log.e(tag, "上传zip文件的响应:" + response.toString());

							if (resultcode == 0) {
								// 注意：这不是上传到讲讲服务器。
								Intent stateIntent = new Intent(ACTION_UPLOAD_STATE);
								stateIntent.putExtra(EXTRA_RESULT_CODE, UploadResultCode.SUCCESS);
								stateIntent.putExtra(EXTRA_RESULT_DATA, lLocalRecordBean);
								stateIntent.putExtra(EXTRA_REQUEST_CODE, requestCode);
								sendBroadcast(stateIntent);
								// 上传成功后删除压缩。
								if (!TextUtils.isEmpty(recordUploadBean.getVideoFilePath())) {
									File video = new File(recordUploadBean.getVideoFilePath());
									if (video.exists()) {
										video.delete();
									}
								}
								if (!TextUtils.isEmpty(recordUploadBean.getZipFilePath())) {
									File zip = new File(recordUploadBean.getZipFilePath());
									if (zip.exists()) {
										zip.delete();
									}
								}

							} else if (resultcode == -2) {
								Intent stateIntent = new Intent(ACTION_UPLOAD_STATE);
								stateIntent.putExtra(EXTRA_RESULT_CODE, UploadResultCode.ALREADY_EXIST);
								stateIntent.putExtra(EXTRA_RESULT_DATA, lLocalRecordBean);
								stateIntent.putExtra(EXTRA_REQUEST_CODE, requestCode);
								sendBroadcast(stateIntent);
							} else if (resultcode == -1) {
								Intent stateIntent = new Intent(ACTION_UPLOAD_STATE);
								stateIntent.putExtra(EXTRA_RESULT_CODE, UploadResultCode.TOKEN_INVALID);
								stateIntent.putExtra(EXTRA_RESULT_DATA, lLocalRecordBean);
								stateIntent.putExtra(EXTRA_REQUEST_CODE, requestCode);
								sendBroadcast(stateIntent);

							} else {
								sendFailCast();
							}
						} catch (JSONException e) {
							e.printStackTrace();
							sendFailCast();
						}
					}

					@Override
					public void onProgressChanged(int progress) {
						lLocalRecordBean.setProgress(progress);
						Intent stateIntent = new Intent(ACTION_UPLOAD_STATE);
						stateIntent.putExtra(EXTRA_RESULT_CODE, UploadResultCode.PROGRESS);
						stateIntent.putExtra(EXTRA_RESULT_DATA, lLocalRecordBean);
						stateIntent.putExtra(EXTRA_REQUEST_CODE, requestCode);
						sendBroadcast(stateIntent);
					}

					@Override
					public void onFail() {
						tags.remove(recordUploadBean.getThumbNailPath());
						sendFailCast();
					}

					@Override
					public void onCancel() {
						tags.remove(recordUploadBean.getThumbNailPath());
						Intent stateIntent = new Intent(ACTION_UPLOAD_STATE);
						stateIntent.putExtra(EXTRA_RESULT_CODE, UploadResultCode.CANCEL);
						stateIntent.putExtra(EXTRA_RESULT_DATA, lLocalRecordBean);
						stateIntent.putExtra(EXTRA_REQUEST_CODE, requestCode);
						sendBroadcast(stateIntent);
					}

					@Override
					public void onStart() {
						Intent stateIntent = new Intent(ACTION_UPLOAD_STATE);
						stateIntent.putExtra(EXTRA_RESULT_CODE, UploadResultCode.START);
						stateIntent.putExtra(EXTRA_RESULT_DATA, lLocalRecordBean);
						stateIntent.putExtra(EXTRA_REQUEST_CODE, requestCode);
						sendBroadcast(stateIntent);

					}

					private void sendFailCast() {
						Intent stateIntent = new Intent(ACTION_UPLOAD_STATE);
						stateIntent.putExtra(EXTRA_RESULT_CODE, UploadResultCode.FAIL);
						stateIntent.putExtra(EXTRA_RESULT_DATA, lLocalRecordBean);
						stateIntent.putExtra(EXTRA_REQUEST_CODE, requestCode);
						sendBroadcast(stateIntent);
					}
				}));
	}

	private static void updateLocalCourseProperty(String shareUrl, String dir, String courseId) {
		try {
			File infofile = new File(dir, Const.INFO_FILE_NAME);
			if (infofile.exists()) {
				Properties p = new Properties();
				FileInputStream ins = new FileInputStream(infofile);
				p.load(ins);

				p.setProperty(LocalRecordBean.SHARE_URL, shareUrl);
				p.setProperty(LocalRecordBean.COURSE_ID, courseId);
				FileOutputStream os = new FileOutputStream(infofile);
				p.store(os, null);

				os.close();
				ins.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
