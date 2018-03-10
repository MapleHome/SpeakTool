package com.speektool.ui.dialogs;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo.ShareParams;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;

import com.google.common.base.Preconditions;
import com.speektool.Const;
import com.speektool.R;
import com.speektool.api.CourseItem;
import com.speektool.api.ThirdPartyRunState;
import com.speektool.utils.NetUtil;
import com.speektool.utils.T;

/**
 * QQ分享选择对话框
 * 
 * @author Maple Shao
 * 
 */
public class QQShareChoiceDialog extends Dialog implements View.OnClickListener, PlatformActionListener {
	private Button btnShareToQZone;// 分享到QQ空间
	private Button btnShareToQQ;// 分享到QQ好友
	private Button btnCancelExit;// 取消

	private CourseItem mCourseItem;
	private ThirdPartyRunState mShareState;
	private Context mActivityContext;

	public QQShareChoiceDialog(Context context, CourseItem course, ThirdPartyRunState state) {
		this(context, R.style.dialogTheme, course, state);
	}

	public QQShareChoiceDialog(Context context, int theme, CourseItem course, ThirdPartyRunState state) {
		super(context, theme);
		Preconditions.checkArgument(context instanceof Activity, "context must be Activity in Dialog.");
		mActivityContext = context;
		mShareState = state;
		mCourseItem = course;

		init();
	}

	private void init() {
		this.setCanceledOnTouchOutside(false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.dialog_qq_sharechoice);

		btnShareToQZone = (Button) findViewById(R.id.btnShareToQZone);
		btnShareToQQ = (Button) findViewById(R.id.btnShareToQQ);
		btnCancelExit = (Button) findViewById(R.id.btnCancelExit);

		btnShareToQZone.setOnClickListener(this);
		btnShareToQQ.setOnClickListener(this);
		btnCancelExit.setOnClickListener(this);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onBackPressed() {
		this.dismiss();

	}

	@Override
	public void onClick(View v) {
		if (v == btnShareToQZone) {// 分享到QQ空间
			this.dismiss();
			if (mShareState != null) {
				mShareState.onStartRun();
			}
			//
			new Thread(new Runnable() {
				@Override
				public void run() {
					ShareParams mSWBparams = new ShareParams();
					mSWBparams.setShareType(Platform.SHARE_IMAGE);
					String introduce = mCourseItem.getIntroduce();
					String title = mCourseItem.getRecordTitle();

					mSWBparams.setText(TextUtils.isEmpty(introduce) ? title : introduce);
					mSWBparams.setTitle(title);
					mSWBparams.setTitleUrl(mCourseItem.getShareUrl());
					mSWBparams.setSite(mCourseItem.getShareUrl());
					mSWBparams.setSiteUrl(mCourseItem.getShareUrl());

					String iconUrl = mCourseItem.getThumbnailImgPath();

					if (!TextUtils.isEmpty(iconUrl)) {

						if (NetUtil.isNetPath(iconUrl)) {
							File saveFile = new File(Const.SD_PATH + "/tempthumb.jpg");
							if (saveFile.exists())
								saveFile.delete();
							try {
								saveFile.createNewFile();
							} catch (IOException e) {
								e.printStackTrace();
							}
//							UniversalHttp.downloadFile(iconUrl, "", saveFile);
							mSWBparams.setImagePath(saveFile.getAbsolutePath());
						} else {
							mSWBparams.setImagePath(iconUrl);
						}
					}
					QZone lQZone = (QZone) ShareSDK.getPlatform(getContext(), QZone.NAME);

					lQZone.setPlatformActionListener(QQShareChoiceDialog.this);
					lQZone.SSOSetting(true);
					lQZone.share(mSWBparams);

				}
			}).start();

		} else if (v == btnShareToQQ) {// 分享到QQ
			this.dismiss();
			if (mShareState != null) {
				mShareState.onStartRun();
			}

			ShareParams mSWBparams = new ShareParams();
			mSWBparams.setShareType(Platform.SHARE_IMAGE);
			String introduce = mCourseItem.getIntroduce();
			String title = mCourseItem.getRecordTitle();

			mSWBparams.setText(TextUtils.isEmpty(introduce) ? title : introduce);
			mSWBparams.setTitle(title);
			mSWBparams.setTitleUrl(mCourseItem.getShareUrl());
			mSWBparams.setSite(mCourseItem.getShareUrl());
			mSWBparams.setSiteUrl(mCourseItem.getShareUrl());

			String iconUrl = mCourseItem.getThumbnailImgPath();

			if (!TextUtils.isEmpty(iconUrl)) {
				if (NetUtil.isNetPath(iconUrl))
					mSWBparams.setImageUrl(iconUrl);
				else
					mSWBparams.setImagePath(iconUrl);
			}

			QQ lQZone = (QQ) ShareSDK.getPlatform(getContext(), QQ.NAME);

			lQZone.setPlatformActionListener(this);
			lQZone.SSOSetting(true);
			lQZone.share(mSWBparams);

		} else if (v == btnCancelExit) {// 取消
			this.dismiss();
		}
	}

	@Override
	public void onCancel(Platform arg0, int arg1) {
		if (mShareState != null) {
			mShareState.onFinishRun();
		}
	}

	@Override
	public void onComplete(Platform plat, int action, HashMap<String, Object> retmap) {
		if (mShareState != null) {
			mShareState.onFinishRun();
		}
		T.showShort(mActivityContext, "分享成功！");
	}

	@Override
	public void onError(Platform arg0, int arg1, Throwable e) {
		if (mShareState != null) {
			mShareState.onFinishRun();
		}
		T.showShort(mActivityContext, "分享失败！");
		e.printStackTrace();
	}
}
