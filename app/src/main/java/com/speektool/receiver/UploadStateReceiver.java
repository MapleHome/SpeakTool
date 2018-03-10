package com.speektool.receiver;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

import com.speektool.activity.MainActivity;
import com.speektool.bean.LocalRecordBean;
import com.speektool.busevents.RefreshCourseListEvent;
import com.speektool.impl.platforms.ShareLocation;
import com.speektool.service.UploadService;
import com.speektool.service.UploadService.UploadRequestCode;
import com.speektool.service.UploadService.UploadResultCode;
import com.speektool.ui.dialogs.FillShareInfoDialog;
import com.speektool.ui.dialogs.OneButtonAlertDialog;
import com.speektool.ui.dialogs.QQShareChoiceDialog;
import com.speektool.ui.dialogs.WechartShareChoiceDialog;
import com.speektool.utils.T;

import de.greenrobot.event.EventBus;

/**
 * 上传状态广播接受者
 *
 * @author shaoshuai
 */
public class UploadStateReceiver extends BroadcastReceiver {
    private MainActivity mPage;
    private Context mContext;

    /**
     * 收到信息
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        mPage = (MainActivity) context;

        UploadRequestCode requestCode = (UploadRequestCode) intent.getSerializableExtra(UploadService.EXTRA_REQUEST_CODE);
        UploadResultCode resultCode = (UploadResultCode) intent.getSerializableExtra(UploadService.EXTRA_RESULT_CODE);
        LocalRecordBean lLocalRecordBean = (LocalRecordBean) intent.getSerializableExtra(UploadService.EXTRA_RESULT_DATA);

        switch (resultCode) {
            case START:// 开始
                mPage.updateUploadStateUi(lLocalRecordBean.getThumbnailImgPath(), true);
                break;
            case PROGRESS:// 进度
                mPage.updateUploadProgressUi(lLocalRecordBean.getThumbnailImgPath(), lLocalRecordBean.getProgress());
                break;
            case SUCCESS:// 成功
                mPage.updateUploadStateUi(lLocalRecordBean.getThumbnailImgPath(), false);
                // 通过EventBus订阅者发送消息
                EventBus.getDefault().post(new RefreshCourseListEvent());
                doSuccess(requestCode, lLocalRecordBean);
                break;
            case CANCEL:// 取消
                mPage.updateUploadStateUi(lLocalRecordBean.getThumbnailImgPath(), false);
                T.showShort(mContext, "上传被取消！");
                break;
            case FAIL:// 失败
                mPage.updateUploadStateUi(lLocalRecordBean.getThumbnailImgPath(), false);
                T.showShort(mContext, "上传失败");
                break;
            case ALREADY_EXIST:// 已经存在
                mPage.updateUploadStateUi(lLocalRecordBean.getThumbnailImgPath(), false);
                T.showShort(mContext, "课程已经存在！");
                break;
            case TOKEN_INVALID:// 令牌无效
                mPage.updateUploadStateUi(lLocalRecordBean.getThumbnailImgPath(), false);
                T.showShort(mContext, "该平台验证口令失效，请重新登录");
                break;
            default:
                break;
        }
    }

    private void doSuccess(UploadRequestCode requestCode, LocalRecordBean lLocalRecordBean) {
        Dialog dia;
        switch (requestCode) {
            case SHARE_QQ:
                dia = new QQShareChoiceDialog(mContext, lLocalRecordBean, null);
                dia.show();
                break;
            case SHARE_SINA_WEIBO:
                dia = new FillShareInfoDialog(mContext, ShareLocation.SINA_WEIBO, lLocalRecordBean, null);
                dia.show();
                break;
            case SHARE_TENCENT_WEIBO:
                dia = new FillShareInfoDialog(mContext, ShareLocation.TENCENT_WEIBO, lLocalRecordBean, null);
                dia.show();
                break;
            case SHARE_WECHART:
                dia = new WechartShareChoiceDialog(mContext, lLocalRecordBean, null);
                dia.show();
                break;
            case JUST_UPLOAD:
                T.showShort(mContext, "上传成功！");
                break;
            case SHARE_PARTNER:
                T.showShort(mContext, "上传成功！");
                break;
            case COPY_LINK:
                copyUploadedLink(lLocalRecordBean.getShareUrl());
                break;
            default:
                break;
        }
    }

    private void copyUploadedLink(String url) {
        ClipboardManager cmb = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(url);

        OneButtonAlertDialog copylinkDia = new OneButtonAlertDialog(mContext, "录像地址已复制到剪贴板！");
        copylinkDia.show();
    }
}