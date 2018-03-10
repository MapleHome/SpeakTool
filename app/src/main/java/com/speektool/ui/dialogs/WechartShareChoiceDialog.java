package com.speektool.ui.dialogs;

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
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.google.common.base.Preconditions;
import com.speektool.R;
import com.speektool.SpeekToolApp;
import com.speektool.api.CourseItem;
import com.speektool.api.ThirdPartyRunState;
import com.speektool.utils.NetUtil;

public class WechartShareChoiceDialog extends Dialog implements View.OnClickListener,
		PlatformActionListener {
	private static final String tag = WechartShareChoiceDialog.class.getSimpleName();
	private Button btnShareToWechartMoments;
	private Button btnShareToWechart;
	private Button btnCancelExit;
	private CourseItem mCourseItem;
	private ThirdPartyRunState mShareState;
	private Context mActivityContext;

	public WechartShareChoiceDialog(Context context, CourseItem course, ThirdPartyRunState state) {
		this(context, R.style.dialogTheme, course, state);
	}

	public WechartShareChoiceDialog(Context context, int theme, CourseItem course, ThirdPartyRunState state) {
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
		setContentView(R.layout.dialog_wechart_sharechoice);
		btnShareToWechartMoments = (Button) findViewById(R.id.btnShareToWechartMoments);
		btnShareToWechartMoments.setOnClickListener(this);
		btnShareToWechart = (Button) findViewById(R.id.btnShareToWechart);
		btnShareToWechart.setOnClickListener(this);
		btnCancelExit = (Button) findViewById(R.id.btnCancelExit);
		btnCancelExit.setOnClickListener(this);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onBackPressed() {
		this.dismiss();

	}

	private final String parseXmlString(int resid) {
		return getContext().getString(resid);
	}

	@Override
	public void onClick(View v) {
		if (v == btnShareToWechartMoments) {
			this.dismiss();
			if (mShareState != null)
				mShareState.onStartRun();

			ShareParams mSWBparams = new ShareParams();
			mSWBparams.setShareType(Platform.SHARE_VIDEO);
			mSWBparams.setTitle(mCourseItem.getRecordTitle());
			mSWBparams.setText(mCourseItem.getIntroduce() + " ");
			mSWBparams.setUrl(mCourseItem.getShareUrl());
			String iconUrl = mCourseItem.getThumbnailImgPath();

			if (!TextUtils.isEmpty(iconUrl)) {

				if (NetUtil.isNetPath(iconUrl))
					mSWBparams.setImageUrl(iconUrl);
				else
					mSWBparams.setImagePath(iconUrl);
			}

			WechatMoments wechatMoments = (WechatMoments) ShareSDK.getPlatform(getContext().getApplicationContext(),
					WechatMoments.NAME);

			wechatMoments.setPlatformActionListener(this);
			wechatMoments.SSOSetting(false);
			wechatMoments.share(mSWBparams);

		} else if (v == btnShareToWechart) {
			this.dismiss();
			if (mShareState != null)
				mShareState.onStartRun();

			ShareParams mSWBparams = new ShareParams();
			mSWBparams.setShareType(Platform.SHARE_VIDEO);
			mSWBparams.setTitle(mCourseItem.getRecordTitle());
			mSWBparams.setText(mCourseItem.getIntroduce() + " ");
			mSWBparams.setUrl(mCourseItem.getShareUrl());
			String iconUrl = mCourseItem.getThumbnailImgPath();

			if (!TextUtils.isEmpty(iconUrl)) {

				if (NetUtil.isNetPath(iconUrl))
					mSWBparams.setImageUrl(iconUrl);
				else
					mSWBparams.setImagePath(iconUrl);
			}

			Wechat wechat = (Wechat) ShareSDK.getPlatform(getContext(), Wechat.NAME);

			wechat.setPlatformActionListener(this);
			wechat.SSOSetting(false);
			wechat.share(mSWBparams);

		} else if (v == btnCancelExit) {
			this.dismiss();

		}
	}

	@Override
	public void onCancel(Platform arg0, int arg1) {
		if (mShareState != null)
			mShareState.onFinishRun();

	}

	@Override
	public void onComplete(Platform plat, int action, HashMap<String, Object> retmap) {
		if (mShareState != null)
			mShareState.onFinishRun();
		SpeekToolApp.getUiHandler().post(new Runnable() {
			@Override
			public void run() {
				OneButtonAlertDialog dia = new OneButtonAlertDialog(mActivityContext, "分享成功！");
				dia.show();
			}
		});

	}

	@Override
	public void onError(Platform arg0, int arg1, Throwable e) {
		if (mShareState != null)
			mShareState.onFinishRun();

		SpeekToolApp.getUiHandler().post(new Runnable() {

			@Override
			public void run() {
				OneButtonAlertDialog dia = new OneButtonAlertDialog(mActivityContext, "分享失败！");
				dia.show();
			}
		});
		e.printStackTrace();
	}
}
