package com.speaktool.ui.Draw;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.maple.msdialog.AlertDialog;
import com.speaktool.Const;
import com.speaktool.R;
import com.speaktool.SpeakApp;
import com.speaktool.api.Draw;
import com.speaktool.api.Page;
import com.speaktool.api.Page.Page_BG;
import com.speaktool.api.PhotoImporter.PickPhotoCallback;
import com.speaktool.bean.ActivePageData;
import com.speaktool.bean.ClearPageData;
import com.speaktool.bean.CopyPageData;
import com.speaktool.bean.CreatePageData;
import com.speaktool.bean.MusicBean;
import com.speaktool.bean.PageBackgroundData;
import com.speaktool.bean.RecordUploadBean;
import com.speaktool.bean.ScreenInfoBean;
import com.speaktool.busevents.CloseEditPopupWindowEvent;
import com.speaktool.busevents.DrawModeChangedEvent;
import com.speaktool.busevents.EraserEvent;
import com.speaktool.busevents.RecordRunEvent;
import com.speaktool.busevents.RecordTimeChangedEvent;
import com.speaktool.busevents.RedoEvent;
import com.speaktool.busevents.RefreshCourseListEvent;
import com.speaktool.busevents.UndoEvent;
import com.speaktool.impl.DrawModeManager;
import com.speaktool.impl.cmd.clear.CmdClearPage;
import com.speaktool.impl.cmd.copy.CmdCopyPage;
import com.speaktool.impl.cmd.create.CmdActivePage;
import com.speaktool.impl.cmd.create.CmdCreatePage;
import com.speaktool.impl.cmd.delete.CmdDeletePage;
import com.speaktool.impl.cmd.transform.CmdChangePageBackground;
import com.speaktool.impl.handpen.HandpenStateEvent;
import com.speaktool.impl.modes.DrawModeChoice;
import com.speaktool.impl.modes.DrawModeCode;
import com.speaktool.impl.modes.DrawModeEraser;
import com.speaktool.impl.modes.DrawModePath;
import com.speaktool.impl.paint.DrawPaint;
import com.speaktool.impl.player.SoundPlayer;
import com.speaktool.impl.recorder.PageRecorder;
import com.speaktool.impl.recorder.RecordError;
import com.speaktool.impl.recorder.RecorderContext;
import com.speaktool.impl.shapes.EditWidget;
import com.speaktool.impl.shapes.ImageWidget;
import com.speaktool.utils.BitmapScaleUtil;
import com.speaktool.utils.DisplayUtil;
import com.speaktool.utils.FormatUtils;
import com.speaktool.utils.RecordFileUtils;
import com.speaktool.utils.ScreenFitUtil;
import com.speaktool.utils.T;
import com.speaktool.utils.record.RecordFileAnalytic;
import com.speaktool.view.dialogs.LoadingDialog;
import com.speaktool.view.dialogs.SaveRecordAlertDialog;
import com.speaktool.view.layouts.DrawPage;
import com.speaktool.view.popupwindow.BasePopupWindow.WeiZhi;
import com.speaktool.view.popupwindow.EditClickPoW;
import com.speaktool.view.popupwindow.ImageClickPoW;
import com.speaktool.view.popupwindow.L_ClearPoW;
import com.speaktool.view.popupwindow.L_EraserWayWitchPoW;
import com.speaktool.view.popupwindow.L_HandPenPoW;
import com.speaktool.view.popupwindow.L_M_AddBatchPhotosPoW;
import com.speaktool.view.popupwindow.L_M_AddNetImgPoW;
import com.speaktool.view.popupwindow.L_M_AddSinglePhotosPoW;
import com.speaktool.view.popupwindow.L_MorePoW;
import com.speaktool.view.popupwindow.L_PencilColorPoW;
import com.speaktool.view.popupwindow.R_AddNewPagePoW;
import com.speaktool.view.popupwindow.R_PreviewPoW;
import com.speaktool.view.popupwindow.R_RerecordPoW;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 画板【是一个画册性质，有多张画纸组成】
 *
 * @author shaoshuai
 */
public class DrawActivity extends Activity implements OnClickListener, OnTouchListener, Draw, PickPhotoCallback {
    // 左侧功能条
    @BindView(R.id.ll_left_bar) View layoutLeftBar;// 左侧功能条
    @BindView(R.id.ivHandPen) ImageView ivHandPen;// 手写笔
    @BindView(R.id.ivChoose) ImageView ivChoose;// 手
    @BindView(R.id.ivPath) ImageView ivPath;// 颜色画笔
    @BindView(R.id.ivEraser) ImageView ivEraser;// 橡皮
    @BindView(R.id.ivMore) ImageView ivMore;// 添加
    @BindView(R.id.ivDeletePage) ImageView ivDeletePage;// 删除界面
    @BindView(R.id.ivUndo) ImageView ivUndo;// 撤销
    @BindView(R.id.ivRedo) ImageView ivRedo;// 返回
    // 内容区
    @BindView(R.id.drawBoardContainer) ViewFlipper viewFlipper;// 绘画板容器
    @BindView(R.id.viewFlipperOverlay) View viewFlipperOverlay;// 文本
    // 底部功能条
    @BindView(R.id.ll_right_bar) View layoutBottom;// 底部功能条
    @BindView(R.id.ivRecord) ImageView ivRecord;// 录制
    @BindView(R.id.tvTime) TextView tvTime;// 时间
    @BindView(R.id.ivReRecord) ImageView ivReRecord;// 重录
    @BindView(R.id.ivPrePage) ImageView ivPrePage;// 上一页
    @BindView(R.id.tvPageInfo) TextView tvPageInfo;// 1/5 页面信息
    @BindView(R.id.ivNextPage) ImageView ivNextPage;// 下一页
    @BindView(R.id.ivNewPage) ImageView ivNewPage;// 添加新界面
    @BindView(R.id.ivPreview) ImageView ivPreview;// 预览
    @BindView(R.id.tvFinish) TextView tvFinish;// 完成

    private Context mContext;
    private int pageWidth;
    private int pageHeight;
    private List<Page> pages = new ArrayList<>();// 【画册】- 画纸集合
    private int currentBoardIndex = 0; // 当前画纸在画册中的索引
    private String mRecordDir;// 课程目录


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_board);
        mContext = this;
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        // 绘制
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            // mIBISPenController = new IBISPenController(this);
            // mDigitalPenController = new DigitalPenController(this);
            ivHandPen.setEnabled(true);// 设置可用
        } else {
            ivHandPen.setEnabled(false);// 设置不可用
            T.showShort(mContext, "当前系统不支持蓝牙！手写笔不可用。");
        }
        // 初始化画板纸张的宽高
        Point screenSize = DisplayUtil.getScreenSize(getApplicationContext());
        pageWidth = LayoutParams.MATCH_PARENT;
        pageHeight = screenSize.y;

        initListener();
        initPage();
        DrawModeManager.getIns().setDrawMode(new DrawModePath());

    }

    private void initListener() {
        // 点击监听
        tvFinish.setOnClickListener(this);// 完成
        ivHandPen.setOnClickListener(this);// 手写笔
        ivChoose.setOnClickListener(this);// 手
        ivPath.setOnClickListener(this);// 画笔
        ivEraser.setOnClickListener(this);// 橡皮
        ivMore.setOnClickListener(this);// 添加
        ivDeletePage.setOnClickListener(this);// 删除界面
        ivUndo.setOnClickListener(this);// 撤销
        ivRedo.setOnClickListener(this);// 返回

        ivRecord.setOnClickListener(this);// 录制
        ivReRecord.setOnClickListener(this);// 重录
        ivPrePage.setOnClickListener(this);// 上一页
        ivNextPage.setOnClickListener(this);// 下一页
        ivNewPage.setOnClickListener(this);// 添加新界面
        ivPreview.setOnClickListener(this);// 预览
        // setOnTouchListener.
        ivChoose.setOnTouchListener(this);// 手
        ivPath.setOnTouchListener(this);// 颜色画笔
        ivEraser.setOnTouchListener(this);// 橡皮
        ivUndo.setOnTouchListener(this);// 撤销
        ivRedo.setOnTouchListener(this);// 返回
        ivMore.setOnTouchListener(this);// 添加
        ivMore.setColorFilter(Color.WHITE);
    }

    private void initPage() {
        DrawModeManager.getIns().setDrawMode(new DrawModePath());
        if (getPlayMode() == PlayMode.MAKE) {
            int id = makePageId();
            createPageSendcmd(Page.DEFAULT_PAGE_BG_TYPE, 0, id);
            setActivePageSendcmd(id);
            postChangePage();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivHandPen:// 手写笔
                Log.e("监听", "点击了手写笔");
//                getDigitalPenController().destroy();// 断开设备
//                getIBISPenController().destroy();
                // 显示智能笔扫描窗口
                L_HandPenPoW handPenPow = new L_HandPenPoW(mContext, v, this);
                handPenPow.showPopupWindow(WeiZhi.Right);
                break;
            case R.id.ivChoose:// 手
                DrawModeManager.getIns().setDrawMode(new DrawModeChoice());
                break;
            case R.id.ivPath: // 画笔
                DrawModeCode code = DrawModeManager.getIns().getModeCode();
                if (code == DrawModeCode.PATH) {
                    // 显示颜色窗口
                    L_PencilColorPoW popupWindow = new L_PencilColorPoW(mContext, v);
                    popupWindow.showPopupWindow(WeiZhi.Right);
                } else {
                    DrawModeManager.getIns().setDrawMode(new DrawModePath());
                }
                break;
            case R.id.ivEraser:// 橡皮
                if (DrawModeManager.getIns().getModeCode() == DrawModeCode.ERASER) {
                    // 显示橡皮擦窗口
                    L_EraserWayWitchPoW eraserPow = new L_EraserWayWitchPoW(mContext, v);
                    eraserPow.showPopupWindow(WeiZhi.Right);
                } else {
                    DrawModeManager.getIns().setDrawMode(new DrawModeEraser());
                }
                break;
            case R.id.ivMore:// 添加
                L_MorePoW addPow = new L_MorePoW(mContext, v, this);
                addPow.showPopupWindow(WeiZhi.Right);
                break;
            case R.id.ivDeletePage:// 删除界面
                L_ClearPoW delPow = new L_ClearPoW(mContext, v, this, this);
                delPow.showPopupWindow(WeiZhi.Right);
                break;
            case R.id.ivUndo:// 撤销
                getCurrentBoard().undo();
                break;
            case R.id.ivRedo:// 对撤销进行返回（重新添加）
                getCurrentBoard().redo();
                break;
            // -----------------------------------------------------
            case R.id.ivRecord:// 录制
                record();
                break;
            case R.id.ivReRecord:// 重录
                // 显示重录窗口
                R_RerecordPoW reRecordPow = new R_RerecordPoW(mContext, v, this);
                reRecordPow.showPopupWindow(WeiZhi.Left);
                break;
            case R.id.ivPrePage:// 上一页
                preChangePage(new Runnable() {
                    @Override
                    public void run() {
                        preBoardClick();
                    }
                });
                break;
            case R.id.ivNextPage:// 下一页
                preChangePage(new Runnable() {
                    @Override
                    public void run() {
                        nextBoardClick();
                    }
                });
                break;
            case R.id.ivNewPage:// 添加新界面
                // 显示添加新页面窗口
                R_AddNewPagePoW newPagePow = new R_AddNewPagePoW(mContext, v, this);
                newPagePow.showPopupWindow(WeiZhi.Left);
                break;
            case R.id.ivPreview:// 预览
                // 预览
                R_PreviewPoW previewPow = new R_PreviewPoW(mContext, v, this);
                previewPow.showPopupWindow(WeiZhi.Left);
                break;
            case R.id.tvFinish:// 完成
                onExitDraw();
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                down(v);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                up(v);
                break;
        }
        return false;
    }

    private void down(View v) {
        switch (v.getId()) {
            case R.id.ivChoose:
            case R.id.ivEraser:
            case R.id.ivUndo:
            case R.id.ivRedo:
                ((ImageView) v).setColorFilter(Color.GRAY);
                break;
            case R.id.ivPath:
                ((ImageView) v).setColorFilter(Color.CYAN);
                break;
            case R.id.ivMore:
                ((ImageView) v).setColorFilter(null);
                break;
        }

    }

    private void up(View v) {
        switch (v.getId()) {
            case R.id.ivChoose:
            case R.id.ivEraser:
            case R.id.ivPath:
            case R.id.ivUndo:
            case R.id.ivRedo:
                ((ImageView) v).setColorFilter(null);
                break;
            case R.id.ivMore:
                ((ImageView) v).setColorFilter(Color.WHITE);
                break;
        }
    }

    // =====================功能点击事件--开始============================================================================

//    /**
//     * 获取点阵笔控制器
//     */
//    public IBISPenController getIBISPenController() {
//        return mIBISPenController;
//    }
//
//    /**
//     * 获取数字笔控制器
//     */
//    public DigitalPenController getDigitalPenController() {
//        return mDigitalPenController;
//    }

    private OnActivityResultListener mOnActivityResultListener;

    public void setOnActivityResultListener(OnActivityResultListener lsn) {
        mOnActivityResultListener = lsn;
    }

    public interface OnActivityResultListener {
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

    @Subscribe
    public void onEventMainThread(HandpenStateEvent e) {
        if (e.state == HandpenStateEvent.STATE_CONNECTED) {
            ivHandPen.setImageResource(R.drawable.handpen_enable);
            T.showShort(mContext, "手写笔连接成功！ 开始书写笔迹.");
        } else {
            ivHandPen.setImageResource(R.drawable.handpen_disable);
            T.showShort(mContext, "手写笔连接断开！");
        }
    }

    // =====================功能点击事件--开始============================================================================
    // 实现接口 - 绘制完成
    @Override
    public void onExitDraw() {
        if (getPageRecorder().isHaveRecordForAll()) {
            this.pauseRecord();
            new SaveRecordAlertDialog(this, this).show();
        } else {// no record.
            if (isUserHaveOperationInSomeBoard()) {
                new AlertDialog(this)
                        .setTitle("提示")
                        .setMessage("是否退出？退出后之前所有操作都会被清空！")
                        .setLeftButton("取消", null)
                        .setRightButton("退出", new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                exitDrawWithoutSave();
                            }
                        })
                        .show();
            } else {
                getPageRecorder().deleteRecordDir();
                this.finish();
            }
        }
    }

    /**
     * 录制
     */
    private void record() {
        if (!getRecorderContext().isBooted()) {
            bootRecord();
        } else if (getRecorderContext().isRunning()) {
            pauseRecord();
        } else {
            continueRecord();
        }
    }

    // 实现接口 - 上一页
    @Override
    public void preBoardClick() {
        if (currentBoardIndex == 0) {
            return;
        }
        Page bd = pages.get(currentBoardIndex - 1);
        setActivePageSendcmd(bd.getPageID());
        DrawModeManager.getIns().setDrawMode(new DrawModePath());
    }

    // 实现接口 - 下一页
    @Override
    public void nextBoardClick() {
        if (currentBoardIndex == pages.size() - 1) {
            int id = makePageId();// 创建页面
            createPageSendcmd(Page.DEFAULT_PAGE_BG_TYPE, currentBoardIndex + 1, id);
        }
        // 往后翻页
        Page bd = pages.get(currentBoardIndex + 1);
        setActivePageSendcmd(bd.getPageID());
    }

    /**
     * 删除页面
     */
    public void deletePager() {
        if (pages.size() <= 1) {
            T.showShort(mContext, "已经是最后一页了");
        } else {
            new AlertDialog(mContext)
                    .setTitle("提示")
                    .setMessage("请问是否删除本页？")
                    .setRightButton("确认", new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            preChangePage(new Runnable() {
                                public void run() {
                                    deleteCurrentBoardClick();
                                }
                            });
                        }
                    })
                    .setLeftButton("取消", null)
                    .show();
        }
    }

    private void deleteCurrentBoardClick() {
        Page bd = getCurrentBoard();
        /** should modify current board index. */
        Page pageShouldShow = deletePageImpl(bd.getPageID());
        if (pageShouldShow == null) {
            return;
        }
        // send cmd.
        CmdDeletePage cmd = new CmdDeletePage();
        cmd.setTime(getPageRecorder().recordTimeNow());
        ActivePageData data = new ActivePageData();
        data.setPageID(bd.getPageID());
        cmd.setData(data);
        getCurrentBoard().sendCommand(cmd, true);
        /** set active page. */
        setActivePageSendcmd(pageShouldShow.getPageID());
    }

    // 实现接口 -- 删除画纸界面
    @Override
    public Page deletePageImpl(int pageId) {
        Page bd = getPageFromId(pageId);
        int position = getPagePostion(bd);

        if (position < 0 || position >= pages.size())
            return null;
        pages.remove(position);
        if (position == pages.size()) {// removed.
            bd = pages.get(position - 1);
        } else {
            bd = pages.get(position);
        }
        currentBoardIndex = getPagePostion(bd);
        return bd;
        // setActivePageImpl(bd.getPageID());
    }

    // =====================功能点击事件--结束=======================================
    // =====================操作命令--开始=======================================

    /**
     * 创建画纸
     */
    private int createPageSendcmd(Page_BG backgroundType, int position, int pageId) {
        createPageImpl(backgroundType, position, pageId);
        // TODO 正在看
        CmdCreatePage cmd = new CmdCreatePage();
        cmd.setTime(getPageRecorder().recordTimeNow());
        cmd.setData(new CreatePageData(pageId, position, backgroundType));// 设置纸张属性
        getCurrentBoard().sendCommand(cmd, true);
        return pages.size();
    }

    // 实现接口 - 创建画板纸张
    @Override
    public void createPageImpl(Page_BG backgroundType, int position, int pageId) {
        DrawPage board = new DrawPage(this, backgroundType, this, pageId);
        pages.add(position, board);// 在指定位置添加纸张
    }

    // 实现接口 -
    @Override
    public void copyPageClick(String option) {
        int srcPageId = getCurrentBoard().getPageID();
        int id = makePageId();
        int position = currentBoardIndex + 1;
        createPageSendcmd(Page.DEFAULT_PAGE_BG_TYPE, position, id);
        //
        setActivePageSendcmd(id);
        copyPageSendcmd(srcPageId, id, option);
    }

    /**
     * 复制页面
     *
     * @param srcPageId  源页面ID
     * @param destPageId 目标页面ID
     * @param option     操作数据类型
     */
    private void copyPageSendcmd(int srcPageId, int destPageId, String option) {
        copyPageImpl(srcPageId, destPageId, option);
        // send cmd.
        CopyPageData data = new CopyPageData();
        data.setSrcPageId(srcPageId);
        data.setDestPageId(destPageId);
        data.setOption(option);

        CmdCopyPage cmd = new CmdCopyPage();
        cmd.setTime(getPageRecorder().recordTimeNow());
        cmd.setData(data);
        getCurrentBoard().sendCommand(cmd, true);

    }

    @Override
    public void copyPageImpl(int srcPageId, int destPageId, String option) {
        Page srcPage = getPageFromId(srcPageId);
        Page destPage = getPageFromId(destPageId);
        if (CopyPageData.OPT_COPY_ALL.equals(option))
            srcPage.copyAllTo(destPage);
        else
            srcPage.copyViewsTo(destPage);
    }

    // 实现接口——清除页面内容
    @Override
    public void clearPageClick(int pageId, String option) {
        clearPageImpl(pageId, option);
        // send cmd.
        ClearPageData data = new ClearPageData();
        data.setPageId(pageId);// 设置页面ID
        data.setOption(option);// 设置清除类型

        CmdClearPage cmd = new CmdClearPage();
        cmd.setTime(getPageRecorder().recordTimeNow());
        cmd.setData(data);
        getCurrentBoard().sendCommand(cmd, true);
    }

    @Override
    public void clearPageImpl(int pageId, String option) {
        Page page = getPageFromId(pageId);
        if (ClearPageData.OPT_CLEAR_ALL.equals(option)) {
            page.clearShapeAndViews();// 清除所有
        } else {
            page.clearPenShapes();// 清除笔记
        }
    }

    // =====================操作命令--结束=======================================
    // =====================视频播放器--开始=======================================

//    private Runnable hideVideoControllerRunnable = new Runnable() {
//        @Override
//        public void run() {
//            layoutVideoController.setVisibility(View.INVISIBLE);// 隐藏播放器
//        }
//    };

    @Override
    public void showVideoController() {
//        if (layoutVideoController.getVisibility() == View.VISIBLE) {
//            layoutVideoController.setVisibility(View.INVISIBLE);
//            layoutVideoController.removeCallbacks(hideVideoControllerRunnable);
//            return;
//        }
//        layoutVideoController.setVisibility(View.VISIBLE);
//        layoutVideoController.postDelayed(hideVideoControllerRunnable, 5000);

    }

    @Override
    public void onPlayComplete() {
//        postTaskToUiThread(new Runnable() {
//            @Override
//            public void run() {
//                layoutVideoController.setPlayPauseIcon(android.R.drawable.ic_media_play);
//
//                int p = JsonScriptPlayer.MAX_PROGRESS;
//                String totalstr = FormatUtils.getFormatTimeSimple(p);
//                layoutVideoController.setProgress(p);
//                layoutVideoController.setProgressText(totalstr);
//            }
//        });
    }

    @Override
    public void onPlayStart() {
//        postTaskToUiThread(new Runnable() {
//            @Override
//            public void run() {
//                layoutVideoController.setPlayPauseIcon(android.R.drawable.ic_media_pause);
//            }
//        });
    }

    // =====================视频播放器--结束=======================================

    @Override
    public void resetAllViews() {
        viewFlipper.removeAllViews();
        pages.clear();
        currentBoardIndex = 0;
        resetPageId();
        DrawPage.resetShapeId(this);
        initPage();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // when use camera.
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
//        if (mIBISPenController != null) {
//            mIBISPenController.destroy();
//            mIBISPenController = null;
//        }
//        if (mDigitalPenController != null) {
//            mDigitalPenController.destroy();
//            mDigitalPenController = null;
//        }
        // 反注册EventBus订阅者
        EventBus.getDefault().unregister(this);
        resetPageId();
        DrawPage.resetShapeId(this);
        //
        if (getPlayMode() == PlayMode.MAKE){
//            SoundRecorder.closeWorldTimer();
            getPageRecorder().closeWorldTimer();
        }
        SoundPlayer.unique().stop();// stop other sound.
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (getPlayMode() == PlayMode.MAKE && getCurrentBoard().getFocusedView() != null) {
            // 通过EventBus订阅者发送消息
            EventBus.getDefault().post(new CloseEditPopupWindowEvent());
            return;
        }
        onExitDraw();
    }

    private int pageID;

    // 实现接口 - 设置当前互动的画纸
    @Override
    public void setActivePageSendcmd(int id) {
        Page bd = getPageFromId(id);// 获取画纸
        int position = getPagePostion(bd);// 获取索引
        if (position < 0 || position >= pages.size())
            return;
        setActivePageImpl(id);
        // 发送命令
        CmdActivePage cmd = new CmdActivePage();
        cmd.setTime(getPageRecorder().recordTimeNow());
        cmd.setData(new ActivePageData(id));
        getCurrentBoard().sendCommand(cmd, true);
    }

    // 实现接口 - 设置当前互动画纸
    @Override
    public void setActivePageImpl(int pageId) {
        Page befpage = getCurrentBoard();// 当前画纸
        if (befpage != null) {
            befpage.recycleBufferBitmap();// 当前画纸-回收缓存
        }
        Page bd = getPageFromId(pageId); // 互动画纸
        bd.redrawBufferBitmap();// 互动画纸 - 重绘缓存
        int position = getPagePostion(bd);// 互动画纸 索引
        viewFlipper.removeAllViews();
        viewFlipper.addView(bd.view());
        viewFlipper.setDisplayedChild(0);// 显示指定索引的子视图

        String pageInfo = String.format(Locale.ENGLISH, "%d/%d", position + 1, pages.size());
        tvPageInfo.setText(pageInfo);// 更新界面信息
        currentBoardIndex = position;
        //
        getCurrentBoard().updateUndoRedoState();// 更新撤销or返回状态
        //
        DrawModeManager.getIns().setDrawMode(new DrawModePath());
        // 如果当前页面是第一页，则【上一页】按钮不可用
        if (position == 0) {
            ivPrePage.setEnabled(false);
            ivPrePage.setColorFilter(Color.GRAY);
        } else {
            ivPrePage.setEnabled(true);
            ivPrePage.setColorFilter(null);
        }
        Log.e("画板界面", "设置当前互动画纸完成");
    }

    public void newEmptyBoardClick() {
        int id = makePageId();
        createPageSendcmd(Page.DEFAULT_PAGE_BG_TYPE, currentBoardIndex + 1, id);
        setActivePageSendcmd(id);
    }

    // 实现接口 - 获取当前画板纸张
    @Override
    public Page getCurrentBoard() {
        if (currentBoardIndex < 0 || currentBoardIndex >= pages.size()) {
            return null;
        } else {
            return pages.get(currentBoardIndex);
        }
    }

    @Override
    public void saveRecord(final RecordUploadBean recordUploadBean) {
        showLoading("保存中，请稍侯...", new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    showCancelMakeReleaseRecordDialog();
                    return true;
                }
                return false;
            }
        });
        getPageRecorder().saveCurrentPageRecord();//save release.txt
        boolean isSuccess = RecordFileAnalytic.setRecordInfos(getPageRecorder().getDir(), recordUploadBean);
//        boolean isSuccess = getPageRecorder().setRecordInfos(recordUploadBean);// save info.txt
        try {
            // 汇总release.txt文件
            ScreenInfoBean info = ScreenFitUtil.getCurrentDeviceInfo();
            String dirPath = getRecordDir();
            RecordFileUtils.makeReleaseScript(new File(dirPath), mContext, info);
            // RecordFileUtils.deleteNonReleaseFiles(new File(getRecordDir()));
            // 生成上传压缩包
            RecordFileUtils.getSpklUploadBeanFromDir(getPageRecorder().getRecordDir());
        } catch (Exception e) {
            e.printStackTrace();
        }

        dismissLoading();
        if (isSuccess) {
            new AlertDialog(mContext)
                    .setTitle("提示")
                    .setMessage("保存成功！")
                    .setRightButton("确定", new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EventBus.getDefault().post(new RefreshCourseListEvent());
                            DrawActivity.this.finish();
                        }
                    }).show();
        } else {
            String msg = "保存录像信息文件失败，请检查存储卡是否有剩余空间！";
            new AlertDialog(mContext).setTitle("提示").setMessage(msg).show();
        }

    }

    /**
     * 显示取消合成课程记录Dialog
     */
    private void showCancelMakeReleaseRecordDialog() {
        new AlertDialog(this)
                .setTitle("提示")
                .setMessage("您确定要放弃合成录像吗？")
                .setRightButton("确认", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissLoading();
                    }
                })
                .setLeftButton("取消", null)
                .show();

    }

    private Dialog mLoadingDialog;

    public void showLoading(String msg, OnKeyListener onKeyListener) {
        mLoadingDialog = new LoadingDialog(this, msg);
        mLoadingDialog.setOnKeyListener(onKeyListener);
        mLoadingDialog.show();
    }

    public void dismissLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    @Override
    public void deleteRecord() {
        getPageRecorder().deleteRecordDir();
    }

    private String getErrorMsg(RecordError error) {
        switch (error) {
            case SDCARD_NOT_EXIST:
                return "存储卡不可用！无法创建录音文件";
            case SDCARD_NO_ENOUGH_SPACE:
                return "存储卡空间不足！无法创建录音文件";
            case SDCARD_CANNOT_WRITE:
                return "存储卡不可写！无法创建录音文件";
            default:
                return "未知错误.";
        }
    }

    @Override
    protected void onStop() {
        pauseRecord();
        super.onStop();
    }

    // 改变页面之前
    @Override
    public void preChangePage(final Runnable successRunnable) {
        showLoading("正在加载", null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final RecordError ret = getPageRecorder().saveCurrentPageRecord();
                dismissLoading();
                postTaskToUiThread(new Runnable() {
                    public void run() {
                        if (ret != RecordError.SUCCESS) {
                            new AlertDialog(context())
                                    .setTitle("提示")
                                    .setMessage(getErrorMsg(ret))
                                    .setLeftButton("退出", new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            onExitDraw();
                                        }
                                    })
                                    .setRightButton("重试", new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            preChangePage(successRunnable);
                                        }
                                    })
                                    .show();
                        } else {
                            successRunnable.run();
                            postChangePage();
                        }
                    }
                });
            }
        }).start();
    }

    // 改变页面之后
    private void postChangePage() {
        // showLoading(getString(R.string.loading), null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 创建临时CMD文件和录音文件
                final RecordError ret = getPageRecorder().recordPage(getCurrentBoard().getPageID());
                // dismissLoading();
                postTaskToUiThread(new Runnable() {
                    public void run() {
                        if (ret != RecordError.SUCCESS) {
                            new AlertDialog(context())
                                    .setTitle("提示")
                                    .setMessage(getErrorMsg(ret))
                                    .setLeftButton("退出", new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            onExitDraw();
                                        }
                                    })
                                    .setRightButton("重试", new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            postChangePage();
                                        }
                                    }).show();

                        } else {
                            // do nothing.
                        }
                    }
                });
            }
        }).start();
    }

    // =====================top pop=======================================
    @Subscribe
    public void onEventMainThread(EraserEvent event) {
        if (event.enable) {
            ivEraser.setEnabled(true);
            ivEraser.setColorFilter(null);
        } else {
            ivEraser.setEnabled(false);
            ivEraser.setColorFilter(Color.GRAY);
        }
    }

    @Subscribe
    public void onEventMainThread(RedoEvent event) {
        if (event.enable) {
            ivRedo.setEnabled(true);
            ivRedo.setColorFilter(null);
        } else {
            ivRedo.setEnabled(false);
            ivRedo.setColorFilter(Color.GRAY);
        }
    }

    @Subscribe
    public void onEventMainThread(UndoEvent event) {
        if (event.enable) {
            ivUndo.setEnabled(true);
            ivUndo.setColorFilter(null);
        } else {
            ivUndo.setEnabled(false);
            ivUndo.setColorFilter(Color.GRAY);
        }
    }

    //
    @Subscribe
    public void onEventMainThread(DrawModeChangedEvent event) {
        DrawModeCode preMode = event.getPreMode();
        DrawModeCode nowMode = event.getNowMode();
        normalPreUi(preMode);
        selectNowUi(nowMode);
    }


    @Subscribe
    public void onEventMainThread(RecordTimeChangedEvent event) {
        // 更新时间
        tvTime.setText(FormatUtils.getFormatTimeSimple(event.getNow()));
    }

    @Subscribe
    public void onEventMainThread(RecordRunEvent event) {
        if (event.isRun) {
            // 更换为：记录状态
            int barRecordingColor = getResources().getColor(R.color.bar_recording_background);
            layoutLeftBar.setBackgroundColor(barRecordingColor);
            layoutBottom.setBackgroundColor(barRecordingColor);
            ivRecord.setImageResource(R.drawable.draw_recording_selected);
        } else {
            // 更换成：记录暂停状态
            layoutLeftBar.setBackgroundColor(getResources().getColor(R.color.draw_left_bar_bg));
            layoutBottom.setBackgroundColor(getResources().getColor(R.color.draw_right_bar_bg));
            ivRecord.setImageResource(R.drawable.draw_recording_normal);
        }
    }

    // =====================插入音频=======================================

    private void selectNowUi(DrawModeCode nowMode) {
        if (nowMode == null)
            return;
        switch (nowMode) {
            case CHOICE:
                ivChoose.setImageResource(R.drawable.draw_hand_seleted);
                break;
            case PATH:
                ivPath.setImageResource(DrawPaint.getGlobalPaintInfo().getIconResIdSelected());
                break;
            case ERASER:
                ivEraser.setImageResource(R.drawable.draw_eraser_seleted);
                break;
            default:
                break;
        }
    }

    private void normalPreUi(DrawModeCode preMode) {
        if (preMode == null)
            return;
        switch (preMode) {
            case CHOICE:
                ivChoose.setImageResource(R.drawable.draw_hand_normal);
                break;
            case PATH:
                ivPath.setImageResource(DrawPaint.getGlobalPaintInfo().getIconResId());
                break;
            case ERASER:
                ivEraser.setImageResource(R.drawable.draw_eraser_normal);
                break;
            default:
                break;
        }
    }

    private PageRecorder mPageRecorder;

    // 实现接口 - 获取画纸页面记录器
    @Override
    public PageRecorder getPageRecorder() {
        if (mPageRecorder == null)
            mPageRecorder = new PageRecorder(this);
        return mPageRecorder;
    }

    private RecorderContext mRecorderContext;

    // 实现接口 - 获取画纸页面记录器上下文
    @Override
    public RecorderContext getRecorderContext() {
        if (mRecorderContext == null)
            mRecorderContext = new RecorderContext();
        return mRecorderContext;
    }

    @Override
    public int makePageId() {
        return ++pageID;
    }

    private void resetPageId() {
        pageID = 0;
    }

    @Override
    public void postTaskToUiThread(Runnable task) {
        SpeakApp.getUiHandler().post(task);
    }

    @Override
    public void setPageBackgroundImpl(int pageId, Page_BG backgroundType) {
        Page board = getPageFromId(pageId);
        if (board == null)
            return;
        board.setBackgroundType(backgroundType);
    }

    @Override
    public void setPageBackgroundClick(int pageId, Page_BG backgroundType) {
        setPageBackgroundImpl(pageId, backgroundType);
        // send cmd.
        CmdChangePageBackground cmd = new CmdChangePageBackground();
        PageBackgroundData data = new PageBackgroundData();
        data.setBackgroundType(backgroundType);
        data.setPageID(pageId);

        cmd.setData(data);
        cmd.setTime(getPageRecorder().recordTimeNow());

        getCurrentBoard().sendCommand(cmd, true);

    }

    private static final int REQUEST_CODE_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CODE_PICK_IMAGE = 2;
    private static final String CAMERA_TEMP_IMAGE_PATH = Const.TEMP_DIR + "/camera_temp.jpg";
    private PickPhotoCallback mPickPhotoCallback;

    @Override
    public void getImageFromCamera(View anchor, PickPhotoCallback callback) {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            mPickPhotoCallback = callback;
            Intent intentCamera = new Intent("android.media.action.IMAGE_CAPTURE");
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(CAMERA_TEMP_IMAGE_PATH)));
            intentCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(intentCamera, REQUEST_CODE_IMAGE_CAPTURE);
        } else {
            T.showShort(mContext, "sdcard not exist!");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_PICK_IMAGE) {
                // now not used.
                // doPickImageOk(data);
            } else if (requestCode == REQUEST_CODE_IMAGE_CAPTURE) {
                doCameraOk(data);
            }
        } else {// not ok.
            T.showShort(mContext, "照相机失败!");
        }
        if (mOnActivityResultListener != null) {
            mOnActivityResultListener.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void doCameraOk(Intent data) {
        File pic = new File(CAMERA_TEMP_IMAGE_PATH);
        if (!pic.exists()) {
            T.showShort(mContext, "照相机失败!");
            mPickPhotoCallback = null;
            return;
        }
        if (mPickPhotoCallback != null) {
            mPickPhotoCallback.onPhotoPicked(CAMERA_TEMP_IMAGE_PATH);
            mPickPhotoCallback = null;
        }
    }

    // 显示挑选照片窗口
    @Override
    public void getImageFromAlbum(View anchor, PickPhotoCallback callback) {
        L_M_AddSinglePhotosPoW popupWindow = new L_M_AddSinglePhotosPoW(mContext, anchor, callback);
        popupWindow.showPopupWindow(WeiZhi.Bottom);
        dim();
        popupWindow.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                undim();
            }
        });

    }

    // 显示获取网路图片窗口
    @Override
    public void getImageFromNet(View anchor, PickPhotoCallback callback) {
        L_M_AddNetImgPoW popupWindow = new L_M_AddNetImgPoW(mContext, anchor, this);
        popupWindow.showPopupWindow(WeiZhi.Bottom);
    }

    // 显示多选照片窗口
    @Override
    public void importImageBatch(View anchor, PickPhotoCallback callback) {
        L_M_AddBatchPhotosPoW popupWindow = new L_M_AddBatchPhotosPoW(mContext, anchor, callback);
        popupWindow.showPopupWindow(WeiZhi.Bottom);
        dim();
        popupWindow.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                undim();
            }
        });

    }

    // 实现接口 - 背景暗淡
    @Override
    public void dim() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.9f;
        this.getWindow().setAttributes(lp);
    }

    // 实现接口 - 背景正常
    @Override
    public void undim() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1f;
        this.getWindow().setAttributes(lp);
    }

    // 实现接口 - 启动记录
    @Override
    public void bootRecord() {
        if (getRecorderContext().isBooted())
            return;
        preChangePage(new Runnable() {
            @Override
            public void run() {
                getRecorderContext().boot();// 启动
            }
        });
    }

    // 实现接口 - 暂停记录
    @Override
    public void pauseRecord() {
        if (!getRecorderContext().isRunning())
            return;
        preChangePage(new Runnable() {
            @Override
            public void run() {
                getRecorderContext().pause();
                SoundPlayer.unique().pause();
            }
        });
    }

    // 实现接口 - 继续记录
    @Override
    public void continueRecord() {
        if (getRecorderContext().isRunning())
            return;
        preChangePage(new Runnable() {
            @Override
            public void run() {
                getRecorderContext().continuing();
            }
        });
    }

    @Override
    public void exitDrawWithoutSave() {
        showLoading("正在加载", null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPageRecorder().deleteRecordDir();
                dismissLoading();
                finish();
            }
        }).start();
    }

    @Override
    public void onPhotoPicked(final String imgPath) {
        SpeakApp.getUiHandler().post(new Runnable() {

            @Override
            public void run() {
                String ret = copyImgToRecordDir(imgPath);
                if (ret != null) {
                    getCurrentBoard().addImg(ret);
                } else {
                    T.showShort(mContext, "图片添加失败！");
                }
            }
        });
    }

    /**
     * 将图片复制到记录路径下
     *
     * @param imgPath
     * @return
     */
    private String copyImgToRecordDir(String imgPath) {
        if (BitmapScaleUtil.isGif(imgPath)) {
            final String resname = RecordFileUtils.copyGifToRecordDir(imgPath, getRecordDir());
            return resname;
        } else {
            final String resname = RecordFileUtils.copyBitmapToRecordDir(imgPath, getRecordDir());
            return resname;
        }
    }

    private int batchImportFirstPageId;

    @Override
    public void onPhotoPicked(final List<String> multiPickImgPaths) {
        showLoading("正在加载", null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int size = multiPickImgPaths.size();
                List<String> images = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    String imgPath = multiPickImgPaths.get(i);
                    String ret = copyImgToRecordDir(imgPath);
                    if (ret != null) {
                        images.add(ret);
                    } else {
                        SpeakApp.getUiHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                T.showShort(mContext, "图片添加失败！");
                            }
                        });
                    }
                }// for end.
                /**
                 * do after copy finish.
                 */
                batchImportFirstPageId = getCurrentBoard().getPageID();
                runnAddImgTask(images);
            }
        }).start();
    }

    /**
     * 使用递归序列添加图像.
     *
     * @param images
     */
    private void runnAddImgTask(final List<String> images) {
        if (images.isEmpty()) {
            dismissLoading();
            setActivePageSendcmd(batchImportFirstPageId);
            return;
        }
        SpeakApp.getUiHandler().post(new Runnable() {
            @Override
            public void run() {
                getCurrentBoard().addImg(images.remove(0));
                if (images.isEmpty()) {
                    dismissLoading();
                    setActivePageSendcmd(batchImportFirstPageId);
                    return;
                }
                nextBoardClick();
                runnAddImgTask(images);
            }
        });
    }

    @Override
    public void setRecordDir(String dir) {
        mRecordDir = dir;
    }

    @Override
    public String getRecordDir() {
        if (TextUtils.isEmpty(mRecordDir))
            return getPageRecorder().getRecordDir();
        return mRecordDir;
    }

    @Override
    public PlayMode getPlayMode() {
        return PlayMode.MAKE;
    }

    @Override
    public Context context() {
        return this;
    }

    @Override
    public int makePageWidth() {
        return pageWidth;
    }

    @Override
    public int makePageHeight() {
        return pageHeight;
    }

    @Override
    public void removeAllHandlerTasks() {
        SpeakApp.getUiHandler().removeCallbacksAndMessages(null);
    }

    @Override
    public void showViewFlipperOverlay() {
        viewFlipperOverlay.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideViewFlipperOverlay() {
        viewFlipperOverlay.setVisibility(View.GONE);
    }

    // 显示文本编辑功能栏
    @Override
    public void showEditClickPopup(final EditWidget edit) {
        EditClickPoW popupWindow = new EditClickPoW(mContext, edit);
        popupWindow.showPopupWindow(WeiZhi.Bottom);
    }

    // 显示图片编辑功能栏
    @Override
    public void showImageClickPopup(final ImageWidget imageWidget) {
        ImageClickPoW popupWindow = new ImageClickPoW(mContext, imageWidget);
        popupWindow.showPopupWindow(WeiZhi.Bottom);
    }

    // 实现接口 - 根据索引获取界面
    @Override
    public Page getPageAtPosition(int position) {
        return pages.get(position);
    }

    @Override
    public void addGlobalMusic(MusicBean music) {
    }

    // ---------------------------------------------------------------------------------------

    /**
     * 在一些画纸上，用户是否还有可撤销or返回操作
     */
    private boolean isUserHaveOperationInSomeBoard() {
        for (Page bd : pages) {
            if (bd.isUserHaveOperation())
                return true;
        }
        return false;
    }

    /**
     * 获取画册中指定id的画纸
     */
    private Page getPageFromId(int id) {
        for (Page bd : pages) {
            if (bd.getPageID() == id)
                return bd;
        }
        return null;
    }

    /**
     * 获取画纸在画册中的索引
     */
    private int getPagePostion(Page bd) {
        return pages.indexOf(bd);// 搜索指定的对象，并返回整个 List 中第一个匹配项的从零开始的索引。
    }

}
