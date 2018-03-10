package com.speektool.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.ishare_lib.ui.dialog.AlertDialog;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.speektool.Const;
import com.speektool.R;
import com.speektool.SpeekToolApp;
import com.speektool.api.AsyncDataLoader;
import com.speektool.api.Draw;
import com.speektool.api.Page;
import com.speektool.api.Page.Page_BG;
import com.speektool.api.PhotoImporter.PickPhotoCallback;
import com.speektool.base.BasePopupWindow.WeiZhi;
import com.speektool.bean.ActivePageData;
import com.speektool.bean.ClearPageData;
import com.speektool.bean.CopyPageData;
import com.speektool.bean.CreatePageData;
import com.speektool.bean.LocalRecordBean;
import com.speektool.bean.MusicBean;
import com.speektool.bean.PageBackgroundData;
import com.speektool.bean.PicDataHolder;
import com.speektool.bean.RecordUploadBean;
import com.speektool.bean.ScreenInfoBean;
import com.speektool.busevents.CloseEditPopupWindowEvent;
import com.speektool.busevents.DisableEraserEvent;
import com.speektool.busevents.DisableRedoEvent;
import com.speektool.busevents.DisableUndoEvent;
import com.speektool.busevents.DrawModeChangedEvent;
import com.speektool.busevents.EnableEraserEvent;
import com.speektool.busevents.EnableRedoEvent;
import com.speektool.busevents.EnableUndoEvent;
import com.speektool.busevents.PlayTimeChangedEvent;
import com.speektool.busevents.RecordPausingEvent;
import com.speektool.busevents.RecordRunningEvent;
import com.speektool.busevents.RecordTimeChangedEvent;
import com.speektool.busevents.RefreshCourseListEvent;
import com.speektool.factory.AsyncDataLoaderFactory;
import com.speektool.impl.cmd.clear.CmdClearPage;
import com.speektool.impl.cmd.copy.CmdCopyPage;
import com.speektool.impl.cmd.create.CmdActivePage;
import com.speektool.impl.cmd.create.CmdCreatePage;
import com.speektool.impl.cmd.delete.CmdDeletePage;
import com.speektool.impl.cmd.transform.CmdChangePageBackground;
import com.speektool.impl.handpen.DigitalPenController;
import com.speektool.impl.handpen.HandpenStateEvent;
import com.speektool.impl.handpen.IBISPenController;
import com.speektool.impl.modes.DrawModeChoice;
import com.speektool.impl.modes.DrawModeCode;
import com.speektool.impl.modes.DrawModeEraser;
import com.speektool.impl.modes.DrawModePath;
import com.speektool.impl.player.JsonScriptPlayer;
import com.speektool.impl.player.PlayProcess;
import com.speektool.impl.player.SoundPlayer;
import com.speektool.impl.recorder.PageRecorder;
import com.speektool.impl.recorder.RecordError;
import com.speektool.impl.recorder.RecorderContext;
import com.speektool.impl.recorder.SoundRecorder;
import com.speektool.impl.shapes.EditWidget;
import com.speektool.impl.shapes.ImageWidget;
import com.speektool.manager.DrawModeManager;
import com.speektool.paint.DrawPaint;
import com.speektool.service.PlayService;
import com.speektool.ui.dialogs.OneButtonAlertDialog;
import com.speektool.ui.dialogs.ProgressDialogOffer;
import com.speektool.ui.dialogs.SaveRecordAlertDialog;
import com.speektool.ui.layouts.DrawPage;
import com.speektool.ui.layouts.VideoPlayControllerView;
import com.speektool.ui.popupwindow.EditClickPopupWindow;
import com.speektool.ui.popupwindow.ImageClickPopupWindow;
import com.speektool.ui.popupwindow.L_ClearPoW;
import com.speektool.ui.popupwindow.L_EraserWayWitchPoW;
import com.speektool.ui.popupwindow.L_HandPenPoW;
import com.speektool.ui.popupwindow.L_M_AddBatchPhotosPoW;
import com.speektool.ui.popupwindow.L_M_AddNetImgPoW;
import com.speektool.ui.popupwindow.L_M_AddSinglePhotosPoW;
import com.speektool.ui.popupwindow.L_MorePoW;
import com.speektool.ui.popupwindow.L_PencilColorPoW;
import com.speektool.ui.popupwindow.R_AddNewPagePoW;
import com.speektool.ui.popupwindow.R_PreviwPoW;
import com.speektool.ui.popupwindow.R_RerecordPoW;
import com.speektool.utils.BitmapScaleUtil;
import com.speektool.utils.DisplayUtil;
import com.speektool.utils.FormatUtils;
import com.speektool.utils.RecordFileUtils;
import com.speektool.utils.ScreenFitUtil;
import com.speektool.utils.T;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;

/**
 * 画板【是一个画册性质，有多张画纸组成】
 *
 * @author shaoshuai
 */
public class DrawActivity extends Activity implements OnClickListener, OnTouchListener, Draw, PickPhotoCallback,
        OnSeekBarChangeListener {
    // 左侧功能条
    @ViewInject(R.id.ll_left_bar)
    private View layoutLeftBar;// 左侧功能条
    @ViewInject(R.id.ivHandPen)
    private ImageView ivHandPen;// 手写笔
    @ViewInject(R.id.ivChoose)
    private ImageView ivChoose;// 手
    @ViewInject(R.id.ivPath)
    private ImageView ivPath;// 颜色画笔
    @ViewInject(R.id.ivEraser)
    private ImageView ivEraser;// 橡皮
    @ViewInject(R.id.ivMore)
    private ImageView ivMore;// 添加
    @ViewInject(R.id.ivDeletePage)
    private ImageView ivDeletePage;// 删除界面
    @ViewInject(R.id.ivUndo)
    private ImageView ivUndo;// 撤销
    @ViewInject(R.id.ivRedo)
    private ImageView ivRedo;// 返回
    // 内容区
    @ViewInject(R.id.drawBoardContainer)
    private ViewFlipper viewFlipper;// 绘画板容器
    @ViewInject(R.id.viewFlipperOverlay)
    private View viewFlipperOverlay;// 文本
    @ViewInject(R.id.layoutVideoController)
    private VideoPlayControllerView layoutVideoController;// 视频播放控制器
    // 底部功能条
    @ViewInject(R.id.ll_right_bar)
    private View layoutBottom;// 底部功能条
    @ViewInject(R.id.ivRecord)
    private ImageView ivRecord;// 录制
    @ViewInject(R.id.tvTime)
    private TextView tvTime;// 时间
    @ViewInject(R.id.ivReRecord)
    private ImageView ivReRecord;// 重录
    @ViewInject(R.id.ivPrePage)
    private ImageView ivPrePage;// 上一页
    @ViewInject(R.id.tvPageInfo)
    private TextView tvPageInfo;// 1/5 页面信息
    @ViewInject(R.id.ivNextPage)
    private ImageView ivNextPage;// 下一页
    @ViewInject(R.id.ivNewPage)
    private ImageView ivNewPage;// 添加新界面
    @ViewInject(R.id.ivPreview)
    private ImageView ivPreview;// 预览
    @ViewInject(R.id.tvFinish)
    private TextView tvFinish;// 完成

    // 常量
    public static final String EXTRA_PLAY_MODE = "play_mode";// 画板模式关键字
    public static final String EXTRA_RECORD_BEAN = "record_bean";// 课程记录关键字
    /**
     * 视频控制器延迟
     */
    private static final long VIDEO_CONTROLLER_DISMISS_DELAY = 5000;

    private Context mContext;
    private PlayMode mPlayMode;// 当前画板模式
    private int pageWidth;
    private int pageHeight;
    /**
     * 【画册】- 画纸集合
     */
    private List<Page> pages = new ArrayList<Page>();
    /**
     * 当前画纸在画册中的索引
     */
    private int currentBoardIndex = 0;
    /**
     * 课程目录
     */
    private String mRecordDir;
    /**
     * JSON脚本播放器
     */
    private JsonScriptPlayer mJsonScriptPlayer;

    private IBISPenController mIBISPenController;// 点阵笔控制器
    private DigitalPenController mDigitalPenController;// 数码笔控制器

    private AsyncDataLoader<String, PicDataHolder> mNetPicturesIconAsyncLoader = AsyncDataLoaderFactory
            .newNetPicturesIconAsyncLoader();
    private AsyncDataLoader<String, PicDataHolder> mLocalPicturesIconAsyncLoader = AsyncDataLoaderFactory
            .newLocalPicturesIconAsyncLoader();

    private Dialog mLoadingDialog;

    public void showLoading(String msg, OnKeyListener onKeyListener) {
        mLoadingDialog = ProgressDialogOffer.offerDialogAsActivity(this, msg);
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
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(null);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);// inject finish.
        setContentView(R.layout.activity_draw_yulan);
        mContext = DrawActivity.this;
        ViewUtils.inject(this);
        EventBus.getDefault().register(this);// 注册EventBus订阅者
        Preconditions.checkNotNull(tvFinish);
        //
        mPlayMode = (PlayMode) getIntent().getSerializableExtra(EXTRA_PLAY_MODE);
        if (mPlayMode == PlayMode.PLAY) {// 播放
            layoutLeftBar.setVisibility(View.GONE);
            layoutBottom.setVisibility(View.GONE);
            layoutVideoController.setVisibility(View.INVISIBLE);// 隐藏播放器
            //
            LocalRecordBean rec = (LocalRecordBean) getIntent().getSerializableExtra(EXTRA_RECORD_BEAN);
            // 检查是否为空
            Preconditions.checkNotNull(rec, "null LocalRecordBean handle to play.");
            mJsonScriptPlayer = new JsonScriptPlayer(rec, this);

            ScreenInfoBean currentScreen = ScreenFitUtil.getCurrentDeviceInfo();
            pageWidth = currentScreen.w;
            pageHeight = currentScreen.h;
            // 设置播放器大小
            LayoutParams lp = (LayoutParams) layoutVideoController.getLayoutParams();
            lp.width = pageWidth;
            layoutVideoController.setLayoutParams(lp);
            //
            LayoutParams lp2 = (LayoutParams) viewFlipperOverlay.getLayoutParams();
            lp2.width = pageWidth;
            lp2.height = pageHeight;
            viewFlipperOverlay.setLayoutParams(lp2);
            //
            initListener();
            initPage();
            DrawModeManager.getIns().setDrawMode(new DrawModePath());
            mJsonScriptPlayer.play();
            //
        } else {// 绘制
            if (android.os.Build.VERSION.SDK_INT >= 18) {
                mIBISPenController = new IBISPenController(this);
                mDigitalPenController = new DigitalPenController(this);
                ivHandPen.setEnabled(true);// 设置可用
            } else {
                ivHandPen.setEnabled(false);// 设置不可用
                T.showShort(mContext, "当前系统不支持蓝牙！手写笔不可用。");
            }
            mMakeReleaseScriptResultReceiver = new MakeReleaseScriptResultReceiver();
            IntentFilter filter = new IntentFilter(PlayProcess.ACTION_MAKE_RESULT);
            this.registerReceiver(mMakeReleaseScriptResultReceiver, filter);
            // 初始化画板纸张的宽高
            Point screenSize = DisplayUtil.getScreenSize(getApplicationContext());
            pageWidth = LayoutParams.MATCH_PARENT;
            pageHeight = screenSize.y;

            initListener();
            initPage();
            DrawModeManager.getIns().setDrawMode(new DrawModePath());
        }
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
        // 播放器监听
        layoutVideoController.setPlayPauseClickListener(this);// 播放暂停
        layoutVideoController.setSeekListener(this); // 播放进度改变监听
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
                getDigitalPenController().destroy();// 断开设备
                getIBISPenController().destroy();
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
                R_PreviwPoW previewPow = new R_PreviwPoW(mContext, v, this);
                previewPow.showPopupWindow(WeiZhi.Left);
                break;
            case R.id.tvFinish:// 完成
                onExitDraw();
                break;
            // -----------------------------------------------------
            case R.id.ivPlayPause:// 播放暂停
                if (mJsonScriptPlayer.isPlayComplete()) {
                    mJsonScriptPlayer.rePlay();
                    layoutVideoController.setPlayPauseIcon(android.R.drawable.ic_media_pause);
                    break;
                }
                if (mJsonScriptPlayer.isPlaying()) {
                    mJsonScriptPlayer.pause();
                    layoutVideoController.setPlayPauseIcon(android.R.drawable.ic_media_play);
                } else {
                    mJsonScriptPlayer.goOn();
                    layoutVideoController.setPlayPauseIcon(android.R.drawable.ic_media_pause);
                }
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

    /**
     * 获取点阵笔控制器
     */
    public IBISPenController getIBISPenController() {
        return mIBISPenController;
    }

    /**
     * 获取数字笔控制器
     */
    public DigitalPenController getDigitalPenController() {
        return mDigitalPenController;
    }

    private OnActivityResultListener mOnActivityResultListener;

    public void setOnActivityResultListener(OnActivityResultListener lsn) {
        mOnActivityResultListener = lsn;
    }

    public interface OnActivityResultListener {
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

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
        if (mPlayMode == PlayMode.MAKE) {
            if (getPageRecorder().isHaveRecordForAll()) {
                this.pauseRecord();
                SaveRecordAlertDialog savedia = new SaveRecordAlertDialog(this, this);
                savedia.show();
            } else {// no record.
                if (isUserHaveOperationInSomeBoard()) {
                    new AlertDialog(this).builder().setTitle("提示").setMsg("是否退出？退出后之前所有操作都会被清空！")
                            .setNegativeButton("取消", new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            }).setPositiveButton("退出", new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            exitDrawWithoutSave();
                        }
                    }).show();
                } else {
                    getPageRecorder().deleteRecordDir();
                    this.finish();
                }
            }

        } else {// play/preview mode.
            mJsonScriptPlayer.exitPlayer();
            finish();
            killPlayProcess();// must do.
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
            new AlertDialog(mContext).builder()
                    .setTitle("提示")
                    .setMsg("请问是否删除本页？")
                    .setPositiveButton("确认", new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            preChangePage(new Runnable() {
                                public void run() {
                                    deleteCurrentBoardClick();
                                }
                            });
                        }
                    })
                    .setNegativeButton("取消", new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
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
        DrawPage board = new DrawPage(getApplicationContext(), backgroundType, this, pageId);
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

    private Runnable hideVideoControllerRunnable = new Runnable() {
        @Override
        public void run() {
            layoutVideoController.setVisibility(View.INVISIBLE);// 隐藏播放器
        }
    };

    @Override
    public void showVideoController() {
        if (layoutVideoController.getVisibility() == View.VISIBLE) {
            layoutVideoController.setVisibility(View.INVISIBLE);
            layoutVideoController.removeCallbacks(hideVideoControllerRunnable);
            return;
        }
        layoutVideoController.setVisibility(View.VISIBLE);
        layoutVideoController.postDelayed(hideVideoControllerRunnable, VIDEO_CONTROLLER_DISMISS_DELAY);

    }

    @Override
    public void onPlayComplete() {
        postTaskToUiThread(new Runnable() {
            @Override
            public void run() {
                layoutVideoController.setPlayPauseIcon(android.R.drawable.ic_media_play);

                int p = JsonScriptPlayer.MAX_PROGRESS;
                String totalstr = FormatUtils.getFormatTimeSimple(p);
                layoutVideoController.setProgress(p);
                layoutVideoController.setProgressText(totalstr);
            }
        });
    }

    @Override
    public void onPlayStart() {
        postTaskToUiThread(new Runnable() {
            @Override
            public void run() {
                layoutVideoController.setPlayPauseIcon(android.R.drawable.ic_media_pause);
            }
        });
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
        if (mIBISPenController != null) {
            mIBISPenController.destroy();
            mIBISPenController = null;
        }
        if (mDigitalPenController != null) {
            mDigitalPenController.destroy();
            mDigitalPenController = null;
        }

        if (mMakeReleaseScriptResultReceiver != null) {
            this.unregisterReceiver(mMakeReleaseScriptResultReceiver);
        }
        // 反注册EventBus订阅者
        EventBus.getDefault().unregister(this);
        resetPageId();
        DrawPage.resetShapeId(this);
        mNetPicturesIconAsyncLoader.destroy();
        mLocalPicturesIconAsyncLoader.destroy();
        //
        if (getPlayMode() == PlayMode.MAKE)
            SoundRecorder.closeWorldTimer();
        killPlayProcess();

        if (isRecordsChanged) {
            EventBus.getDefault().post(new RefreshCourseListEvent());
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

    private MakeReleaseScriptResultReceiver mMakeReleaseScriptResultReceiver;
    private boolean isRecordsChanged = false;

    /**
     * 发行脚本结果广播接收者
     */
    private class MakeReleaseScriptResultReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            dismissLoading();
            int result = intent.getIntExtra(PlayProcess.EXTRA_MAKE_RESULT, PlayProcess.MAKE_SUECESS);
            if (result == PlayProcess.MAKE_FAIL) {
                OneButtonAlertDialog dia = new OneButtonAlertDialog(context(), "录音合成失败！请检查存储卡空间");
                dia.show();
                return;
            }
            RecordFileUtils.deleteNonReleaseFiles(new File(getRecordDir()));
            /** make success. */
            isRecordsChanged = true;
            // EventBus.getDefault().post(new RefreshCourseListEvent());
            if (mCurrentRecordUploadBean.isPublicPublish()) {
                // 无需判断是否登陆，直接上传，如果没有登陆则使用匿名UID
                uploadFile();
            } else {
                finish();
            }
        }
    }

    private RecordUploadBean mCurrentRecordUploadBean;

    @Override
    public void saveRecord(final RecordUploadBean recordUploadBean) {
        mCurrentRecordUploadBean = recordUploadBean;
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPageRecorder().saveCurrentPageRecord();
                final boolean isSuccess = getPageRecorder().setRecordInfos(recordUploadBean);
                if (!isSuccess) {
                    dismissLoading();
                    postTaskToUiThread(new Runnable() {
                        public void run() {
                            OneButtonAlertDialog dia = new OneButtonAlertDialog(context(), "保存录像信息文件失败，请检查存储卡是否有剩余空间！");
                            dia.show();
                        }
                    });
                    return;
                }
                toStartPlayService();
            }
        }).start();

    }

    /**
     * 显示取消合成课程记录Dialog
     */
    private void showCancelMakeReleaseRecordDialog() {
        new AlertDialog(this).builder().setTitle("提示").setMsg("您确定要放弃合成录像吗？")
                .setPositiveButton("确认", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        killPlayProcess();
                        dismissLoading();
                    }
                }).setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).show();

    }

    /**
     * 上传文件
     */
    private void uploadFile() {
        RecordUploadBean recordUploadBean = RecordFileUtils.getSpklUploadBeanFromDir(getPageRecorder().getRecordDir(),
                getApplicationContext());
        if (recordUploadBean == null) {
            T.showShort(mContext, "上传内容为空！");
            return;
        }
        finish();
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
        if (getPlayMode() == PlayMode.MAKE) {
            pauseRecord();
        } else {// play.
            if (mJsonScriptPlayer.isPlaying()) {
                mJsonScriptPlayer.pause();
                layoutVideoController.setPlayPauseIcon(android.R.drawable.ic_media_play);
            }
        }
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
                            new AlertDialog(context()).builder().setTitle("提示").setMsg(getErrorMsg(ret))
                                    .setNegativeButton("退出", new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            onExitDraw();
                                        }
                                    }).setPositiveButton("重试", new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    preChangePage(successRunnable);
                                }
                            }).show();
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
                final RecordError ret = getPageRecorder().recordPage(getCurrentBoard().getPageID());
                // dismissLoading();
                postTaskToUiThread(new Runnable() {
                    public void run() {
                        if (ret != RecordError.SUCCESS) {
                            new AlertDialog(context()).builder().setTitle("提示").setMsg(getErrorMsg(ret))
                                    .setNegativeButton("退出", new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            onExitDraw();
                                        }
                                    }).setPositiveButton("重试", new OnClickListener() {
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

    public void onEventMainThread(EnableEraserEvent event) {
        ivEraser.setEnabled(true);
        ivEraser.setColorFilter(null);
    }

    public void onEventMainThread(DisableEraserEvent event) {
        ivEraser.setEnabled(false);
        ivEraser.setColorFilter(Color.GRAY);
    }

    //
    public void onEventMainThread(EnableRedoEvent event) {
        ivRedo.setEnabled(true);
        ivRedo.setColorFilter(null);
    }

    public void onEventMainThread(DisableRedoEvent event) {
        ivRedo.setEnabled(false);
        ivRedo.setColorFilter(Color.GRAY);
    }

    //

    public void onEventMainThread(EnableUndoEvent event) {
        ivUndo.setEnabled(true);
        ivUndo.setColorFilter(null);
    }

    public void onEventMainThread(DisableUndoEvent event) {
        ivUndo.setEnabled(false);
        ivUndo.setColorFilter(Color.GRAY);
    }

    //
    public void onEventMainThread(DrawModeChangedEvent event) {
        DrawModeCode preMode = event.getPreMode();
        DrawModeCode nowMode = event.getNowMode();
        normalPreUi(preMode);
        selectNowUi(nowMode);
    }

    public void onEventMainThread(RecordTimeChangedEvent event) {
        if (mPlayMode != PlayMode.MAKE)
            return;
        tvTime.setText(FormatUtils.getFormatTimeSimple(event.getNow()));
    }

    public void onEventMainThread(PlayTimeChangedEvent event) {
        if (mPlayMode == PlayMode.MAKE)
            return;
        long now = event.getNow();
        long total = event.getCloseTime();
        float per = (float) now / (float) total;
        int p = (int) (per * (float) JsonScriptPlayer.MAX_PROGRESS);

        // GLogger.e(tag, "p:" + p);
        String nowstr = FormatUtils.getFormatTimeSimple(now);
        String totalstr = FormatUtils.getFormatTimeSimple(total);
        if (nowstr.equals(totalstr)) {
            p = JsonScriptPlayer.MAX_PROGRESS;
        }
        layoutVideoController.setProgress(p);
        layoutVideoController.setProgressText(nowstr);
        layoutVideoController.setTotalDuration(totalstr);

    }

    public void onEventMainThread(RecordRunningEvent event) {
        // 更换为：记录状态
        int barRecordingColor = getResources().getColor(R.color.bar_recording_background);
        layoutLeftBar.setBackgroundColor(barRecordingColor);
        layoutBottom.setBackgroundColor(barRecordingColor);
        ivRecord.setImageResource(R.drawable.draw_recording_selected);
    }

    public void onEventMainThread(RecordPausingEvent event) {
        // 更换成：记录暂停状态
        layoutLeftBar.setBackgroundColor(getResources().getColor(R.color.draw_left_bar_bg));
        layoutBottom.setBackgroundColor(getResources().getColor(R.color.draw_right_bar_bg));
        ivRecord.setImageResource(R.drawable.draw_recording_normal);
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
        SpeekToolApp.getUiHandler().post(task);
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
    private static final String CAMERA_TEMP_IMAGE_PATH = Const.SD_PATH + "/camera_temp.jpg";
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
        L_M_AddNetImgPoW popupWindow = new L_M_AddNetImgPoW(mContext, anchor, this, mNetPicturesIconAsyncLoader);
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
        new Thread(new Runnable() {

            @Override
            public void run() {
                final String ret = copyImgToRecordDir(imgPath);
                if (ret != null) {
                    SpeekToolApp.getUiHandler().post(new Runnable() {

                        @Override
                        public void run() {
                            getCurrentBoard().addImg(ret);
                        }
                    });
                } else {
                    SpeekToolApp.getUiHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            T.showShort(mContext, "图片添加失败！");
                        }
                    });
                }
            }
        }).start();
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
                final List<String> images = Lists.newArrayList();
                for (int i = 0; i < size; i++) {
                    String imgPath = multiPickImgPaths.get(i);
                    final String ret = copyImgToRecordDir(imgPath);
                    if (ret != null) {
                        images.add(ret);
                    } else {
                        SpeekToolApp.getUiHandler().post(new Runnable() {
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
        SpeekToolApp.getUiHandler().post(new Runnable() {
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
        return mPlayMode;
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
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mJsonScriptPlayer.seekTo(seekBar.getProgress());
    }

    @Override
    public void removeAllHandlerTasks() {
        SpeekToolApp.getUiHandler().removeCallbacksAndMessages(null);
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
        EditClickPopupWindow popupWindow = new EditClickPopupWindow(mContext, edit);
        popupWindow.showPopupWindow(WeiZhi.Bottom);
    }

    // 显示图片编辑功能栏
    @Override
    public void showImageClickPopup(final ImageWidget imageWidget) {
        ImageClickPopupWindow popupWindow = new ImageClickPopupWindow(mContext, imageWidget);
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

    // ---------------------------------------------------------------------------------------


    /**
     * 开启播放服务
     */
    private void toStartPlayService() {
        Intent it = new Intent(context(), PlayService.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        it.putExtra(PlayProcess.EXTRA_ACTION, PlayProcess.ACTION_MAKE_RELEASE_SCRIPT);
        it.putExtra(PlayProcess.EXTRA_RECORD_DIR, getRecordDir());
        it.putExtra(PlayProcess.EXTRA_SCREEN_INFO, ScreenFitUtil.getCurrentDeviceInfo());
        context().startService(it);
    }

    /**
     * 停止播放服务
     */
    private final void killPlayProcess() {
        PlayService.killServiceProcess(this);
    }
}
