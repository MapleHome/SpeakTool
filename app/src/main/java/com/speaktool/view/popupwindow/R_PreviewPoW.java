package com.speaktool.view.popupwindow;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow.OnDismissListener;

import com.maple.msdialog.AlertDialog;
import com.speaktool.R;
import com.speaktool.api.Draw;
import com.speaktool.impl.player.PlayProcess;
import com.speaktool.ui.Player.PlayService;
import com.speaktool.utils.ScreenFitUtil;
import com.speaktool.utils.T;
import com.speaktool.view.dialogs.LoadingDialog;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 右侧功能栏——预览界面
 *
 * @author shaoshuai
 */
public class R_PreviewPoW extends BasePopupWindow implements OnClickListener, OnDismissListener {
    private Draw mDraw;
    private Dialog mLoadingDialog;

    @Override
    public View getContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.pow_previewclick, null);
        ButterKnife.bind(this, view);
        return view;
    }

    public R_PreviewPoW(Context context, View anchor, Draw draw) {
        super(context, anchor);
        this.setOnDismissListener(this);
        mDraw = draw;
        mMakeReleaseScriptResultReceiver = new MakeReleaseScriptResultReceiver();
        IntentFilter filter = new IntentFilter(PlayProcess.ACTION_PREVIEW_RESULT);
        mContext.registerReceiver(mMakeReleaseScriptResultReceiver, filter);

        mDraw.pauseRecord();
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.tvPreviewPage:
                previewPage();
                break;
            case R.id.tvPreviewAll:
                previewAll();
                break;
        }
    }

    // 预览全部
    @OnClick(R.id.tvPreviewAll)
    void previewAll() {
        if (!mDraw.getPageRecorder().isHaveRecordForAll()) {
            T.showShort(mContext, "还没有录像！");
            return;
        }
        String dirPath = mDraw.getPageRecorder().getRecordDir();
        showLoading();
        Intent it = new Intent(mContext, PlayService.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        it.putExtra(PlayProcess.EXTRA_ACTION, PlayProcess.ACTION_PREVIEW);
        it.putExtra(PlayProcess.EXTRA_RECORD_DIR, dirPath);
        it.putExtra(PlayProcess.EXTRA_SCREEN_INFO, ScreenFitUtil.getCurrentDeviceInfo());
        mContext.startService(it);
    }

    // 预览本页
    @OnClick(R.id.tvPreviewPage)
    void previewPage() {
        int pageid = mDraw.getCurrentBoard().getPageID();
        if (!mDraw.getPageRecorder().isHaveRecordForPage(pageid)) {
            T.showShort(mContext, "本页还没有录像！");
            return;
        }
        //
        String dirPath = mDraw.getPageRecorder().getRecordDir();
        showLoading();
        Intent it = new Intent(mContext, PlayService.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        it.putExtra(PlayProcess.EXTRA_ACTION, PlayProcess.ACTION_PREVIEW);
        it.putExtra(PlayProcess.EXTRA_RECORD_DIR, dirPath);
        it.putExtra(PlayProcess.EXTRA_PREVIEW_PAGE_ID, pageid);
        it.putExtra(PlayProcess.EXTRA_SCREEN_INFO, ScreenFitUtil.getCurrentDeviceInfo());
        mContext.startService(it);
    }

    @Override
    public void onDismiss() {
        if (mMakeReleaseScriptResultReceiver != null)
            mContext.unregisterReceiver(mMakeReleaseScriptResultReceiver);
    }

    private void showLoading() {
        mLoadingDialog = new LoadingDialog(mDraw.context(), "正在加载");
        mLoadingDialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    showCancelMakeReleaseRecordDialog();
                    return true;
                }
                return false;
            }
        });
        mLoadingDialog.show();
    }

    private void showCancelMakeReleaseRecordDialog() {
        new AlertDialog(mDraw.context())
                .setTitle("提示")
                .setMessage("您确定要放弃合成录像吗？")
                .setLeftButton("取消", null)
                .setRightButton("确定", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PlayService.killServiceProcess(mContext);
                        dismissLoading();
                    }
                }).show();
    }

    private void dismissLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    private MakeReleaseScriptResultReceiver mMakeReleaseScriptResultReceiver;

    private class MakeReleaseScriptResultReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            dismissLoading();
        }
    }
}
