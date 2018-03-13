package com.speaktool.impl.platforms;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.speaktool.ui.activity.WebActivity;
import com.speaktool.api.CourseItem;
import com.speaktool.api.LoginCallback;
import com.speaktool.api.ThirdPartyRunState;
import com.speaktool.bean.LocalRecordBean;
import com.speaktool.bean.ThirdParty;
import com.speaktool.utils.RecordFileUtils;
import com.speaktool.utils.T;
import com.speaktool.utils.ZipUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

/**
 * 合作平台。
 *
 * @author Maple Shao
 */
public class PartnerPlat extends BasePaltform {

    private ThirdParty mThirdParty;

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

        toWebPage("", loginUrl);

    }

    /**
     * 去新闻页面
     */
    private void toWebPage(String title, String url) {
        Intent intent = new Intent(mContext, WebActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(WebActivity.EXTRA_TITLE, title);// 功能Item
        intent.putExtra(WebActivity.EXTRA_URL, url);// 功能Item
        mContext.startActivity(intent);// 开启目标Activity
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
                T.showShort(mContext, "分享失败");
                return;
            }
        }
        // set info.
    }

    @Override
    protected void shareUploadedRecord() {
    }

}
