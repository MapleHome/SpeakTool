package com.speektool.impl.platforms;

import android.content.Context;
import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.speektool.api.CourseItem;
import com.speektool.api.LoginCallback;
import com.speektool.api.ThirdPartyRunState;
import com.speektool.api.ThirdpartyPlatform;
import com.speektool.bean.LocalRecordBean;
import com.speektool.ui.dialogs.LoadingDialog;

/**
 * 第三方平台基础类
 *
 * @author Maple Shao
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

        }
    }

    /**
     * 共享本地记录
     */
    private void shareLocalRecord() {
        String shareUrl = mCourseItem.getShareUrl();
        if (!TextUtils.isEmpty(shareUrl)) {
            shareUploadedRecord();
        } else {
            // upload then share.
        }
    }

    /**
     * 分享上传记录
     */
    protected abstract void shareUploadedRecord();


}
