package com.speektool.impl.platforms;

import android.content.Context;

import com.speektool.api.LoginCallback;
import com.speektool.api.ThirdPartyRunState;
import com.speektool.service.UploadService.UploadRequestCode;
import com.speektool.ui.dialogs.WechartShareChoiceDialog;

/**
 * 微信平台
 * 
 * @author shaoshuai
 * 
 */
public class WechartPlat extends BasePaltform {

	public WechartPlat(Context mContext, ThirdPartyRunState mThirdPartyRunState) {
		super(mContext, mThirdPartyRunState);
	}

	@Override
	public void login(LoginCallback mLoginCallback) {
		throw new UnsupportedOperationException("微信平台不支持登陆.");
	}

	@Override
	protected void shareUploadedRecord() {
		WechartShareChoiceDialog weDia = new WechartShareChoiceDialog(mContext,
				mCourseItem, mThirdPartyRunState);
		weDia.show();

	}

	@Override
	protected UploadRequestCode getUploadRequestCode() {
		return UploadRequestCode.SHARE_WECHART;
	}

}
