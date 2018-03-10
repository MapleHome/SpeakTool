package com.speektool.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.speektool.Const;
import com.speektool.R;
import com.speektool.SpeekToolApp;
import com.speektool.api.Draw;
import com.speektool.api.Page;
import com.speektool.api.Page.Page_BG;
import com.speektool.bean.ActivePageData;
import com.speektool.bean.ClearPageData;
import com.speektool.bean.CopyPageData;
import com.speektool.bean.CreatePageData;
import com.speektool.bean.LocalRecordBean;
import com.speektool.bean.MusicBean;
import com.speektool.bean.PageBackgroundData;
import com.speektool.bean.RecordUploadBean;
import com.speektool.bean.ScreenInfoBean;
import com.speektool.busevents.PlayTimeChangedEvent;
import com.speektool.busevents.RefreshCourseListEvent;
import com.speektool.impl.cmd.clear.CmdClearPage;
import com.speektool.impl.cmd.copy.CmdCopyPage;
import com.speektool.impl.cmd.create.CmdActivePage;
import com.speektool.impl.cmd.create.CmdCreatePage;
import com.speektool.impl.cmd.transform.CmdChangePageBackground;
import com.speektool.impl.modes.DrawModePath;
import com.speektool.impl.player.JsonScriptPlayer;
import com.speektool.impl.player.PlayProcess;
import com.speektool.impl.player.SoundPlayer;
import com.speektool.impl.recorder.PageRecorder;
import com.speektool.impl.recorder.RecorderContext;
import com.speektool.impl.shapes.EditWidget;
import com.speektool.impl.shapes.ImageWidget;
import com.speektool.manager.DrawModeManager;
import com.speektool.service.PlayService;
import com.speektool.ui.dialogs.OneButtonAlertDialog;
import com.speektool.ui.dialogs.ProgressDialogOffer;
import com.speektool.ui.layouts.DrawPage;
import com.speektool.ui.layouts.VideoPlayControllerView;
import com.speektool.utils.FormatUtils;
import com.speektool.utils.ScreenFitUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * 播放本地视频
 *
 * @author shaoshuai
 */
@ContentView(R.layout.activity_play_video)
public class PlayVideoActivity extends RoboActivity implements Draw {
    // 内容区
    @InjectView(R.id.drawBoardContainer)
    private ViewFlipper viewFlipper;// 绘画板容器
    @InjectView(R.id.viewFlipperOverlay)
    private View viewFlipperOverlay;// 文本
    @InjectView(R.id.layoutVideoController)
    private VideoPlayControllerView layoutVideoController;// 视频播放控制器

    // 常量
    public static final String EXTRA_RECORD_BEAN = "record_bean";
    public static final String EXTRA_PLAY_MODE = "play_mode";
    /**
     * 视频控制器延迟
     */
    private static final long Video_CONTROLLER_DISMISS_DELAY = 5000;
    private Context mContext;
    /**
     * 界面集合
     */
    private List<Page> pages = new ArrayList<Page>();
    /**
     * 添加音乐集合
     */
    private final List<MusicBean> globalMusics = Lists.newArrayList();
    /**
     * 当前界面索引
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
    private PlayMode mPlayMode;

    private int pageWidth;
    private int pageHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(null);
        mContext = this;
        // 注册EventBus订阅者
        EventBus.getDefault().register(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);// inject finish.

        mPlayMode = (PlayMode) getIntent().getSerializableExtra(EXTRA_PLAY_MODE);

        if (mPlayMode == PlayMode.PLAY) {

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

            DrawModeManager.getIns().setDrawMode(new DrawModePath());
            mJsonScriptPlayer.play();
            //
        }
    }

    private void initListener() {
        // 播放暂停
        layoutVideoController.setPlayPauseClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ivPlayPause:// 播放暂停
                        Log.e("本地视频播放", "点击暂停");
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
        });
        // 播放进度改变监听
        layoutVideoController.setSeekListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mJsonScriptPlayer.seekTo(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
        });
    }

    // =====================功能点击事件--开始=======================================

    /**
     * 绘制完成
     */
    @Override
    public void onExitDraw() {
        if (mPlayMode == PlayMode.MAKE) {
        } else {// play/preview mode.
            mJsonScriptPlayer.exitPlayer();
            finish();
            killPlayProcess();// must do.
        }
    }

    // 上一页
    @Override
    public void preBoardClick() {
        if (currentBoardIndex == 0) {
            return;
        }
        Page bd = pages.get(currentBoardIndex - 1);
        setActivePageSendcmd(bd.getPageID());
        DrawModeManager.getIns().setDrawMode(new DrawModePath());
    }

    // 下一页
    @Override
    public void nextBoardClick() {
        if (currentBoardIndex == pages.size() - 1) {
            int id = makePageId();
            createPageSendcmd(Page.DEFAULT_PAGE_BG_TYPE, currentBoardIndex + 1, id);
        }
        Page bd = pages.get(currentBoardIndex + 1);
        setActivePageSendcmd(bd.getPageID());
    }

    // =====================功能点击事件--结束=======================================
    // =====================操作命令--开始=======================================
    // TODO NIHAO
    private int createPageSendcmd(Page_BG backgroundType, int position, int pageId) {
        createPageImpl(backgroundType, position, pageId);
        //
        CmdCreatePage cmd = new CmdCreatePage();
        cmd.setTime(getPageRecorder().recordTimeNow());
        CreatePageData data = new CreatePageData();
        data.setBackgroundType(backgroundType);
        data.setPageID(pageId);
        data.setPosition(position);
        cmd.setData(data);
        getCurrentBoard().sendCommand(cmd, true);
        return pages.size();
    }

    @Override
    public void createPageImpl(Page_BG backgroundType, int position, int pageId) {
        DrawPage board = new DrawPage(getApplicationContext(), backgroundType, this, pageId);
        pages.add(position, board);

    }

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

    // 实现父类——清除页面内容
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
    // TODO 视频播放器
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
        layoutVideoController.postDelayed(hideVideoControllerRunnable, Video_CONTROLLER_DISMISS_DELAY);

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

        DrawModeManager.getIns().setDrawMode(new DrawModePath());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {// when use
        // camera.
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        // 反注册EventBus订阅者
        EventBus.getDefault().unregister(this);
        resetPageId();
        DrawPage.resetShapeId(this);
        //

        killPlayProcess();
        if (isRecordsChanged) {
            EventBus.getDefault().post(new RefreshCourseListEvent());
        }
        SoundPlayer.unique().stop();// stop other sound.
        super.onDestroy();
    }

    private final void killPlayProcess() {
        PlayService.killServiceProcess(this);
    }

    @Override
    public void onBackPressed() {
        onExitDraw();
    }

    private int pageID;

    @Override
    public void setActivePageSendcmd(int id) {
        Page bd = getPageFromId(id);
        int position = getPagePostion(bd);

        if (position < 0 || position >= pages.size())
            return;
        setActivePageImpl(id);

        ActivePageData data = new ActivePageData();
        data.setPageID(id);

        CmdActivePage cmd = new CmdActivePage();
        cmd.setTime(getPageRecorder().recordTimeNow());
        cmd.setData(data);
        getCurrentBoard().sendCommand(cmd, true);
    }

    /**
     * 根据ID获取界面
     *
     * @param id 界面ID
     * @return 相应界面
     */
    private Page getPageFromId(int id) {
        for (Page bd : pages) {
            if (bd.getPageID() == id)
                return bd;
        }
        return null;
    }

    /**
     * 获取指定界面在集合中的索引
     *
     * @param bd 指定界面
     * @return 界面在集合中对应的索引值
     */
    private int getPagePostion(Page bd) {
        return pages.indexOf(bd);// 搜索指定的对象，并返回整个 List 中第一个匹配项的从零开始的索引。
    }

    @Override
    public void setActivePageImpl(int pageId) {
        Page befpage = getCurrentBoard();
        if (befpage != null) {
            befpage.recycleBufferBitmap();
        }
        //
        Page bd = getPageFromId(pageId);
        bd.redrawBufferBitmap();
        int position = getPagePostion(bd);
        //
        viewFlipper.removeAllViews();
        viewFlipper.addView(bd.view());

        viewFlipper.setDisplayedChild(0);

        currentBoardIndex = position;
        //
        getCurrentBoard().updateUndoRedoState();
        //
        DrawModeManager.getIns().setDrawMode(new DrawModePath());

        Log.e("lich", "setActivePageImpl 完成");
    }

    public void newEmptyBoardClick() {
        int id = makePageId();
        createPageSendcmd(Page.DEFAULT_PAGE_BG_TYPE, currentBoardIndex + 1, id);
        setActivePageSendcmd(id);
    }

    // 获取当前页面（画板）
    @Override
    public Page getCurrentBoard() {
        if (currentBoardIndex < 0 || currentBoardIndex >= pages.size()) {
            return null;
        } else {
            return pages.get(currentBoardIndex);
        }
    }

    private boolean isRecordsChanged = false;

    @Override
    public void saveRecord(final RecordUploadBean recordUploadBean) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                getPageRecorder().saveCurrentPageRecord();
                final boolean isSuccess = getPageRecorder().setRecordInfos(recordUploadBean);
                if (!isSuccess) {
                    dismissLoading();
                    postTaskToUiThread(new Runnable() {
                        public void run() {
                            OneButtonAlertDialog dia = new OneButtonAlertDialog(context(),
                                    getString(R.string.save_recordinfo_fail));
                            dia.show();
                        }
                    });
                    return;
                }
                toStartPlayService();

            }

        }).start();

    }

    @Override
    public void deleteRecord() {
        getPageRecorder().deleteRecordDir();
    }

    /**
     * 改变页面
     */
    @Override
    public void preChangePage(final Runnable successRunnable) {
        showLoading("正在加载", null);

    }

    @Override
    protected void onStop() {
        if (getPlayMode() == PlayMode.MAKE) {

        } else {// play.
            if (mJsonScriptPlayer.isPlaying()) {
                mJsonScriptPlayer.pause();
                layoutVideoController.setPlayPauseIcon(android.R.drawable.ic_media_play);
            }
        }
        super.onStop();
    }

    // =====================top pop=======================================

    public void onEventMainThread(PlayTimeChangedEvent event) {

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

    private PageRecorder mPageRecorder;

    @Override
    public PageRecorder getPageRecorder() {
        if (mPageRecorder == null)
            mPageRecorder = new PageRecorder(this);
        return mPageRecorder;
    }

    private RecorderContext mRecorderContext;

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
    private static final String CAMERA_TEMP_IMAGE_PATH = Const.SD_PATH + "/camera_temp.jpg";
    private Dialog mLoadingDialog;

    @Override
    public void getImageFromCamera(View anchor, PickPhotoCallback callback) {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {

            Intent intentCamera = new Intent("android.media.action.IMAGE_CAPTURE");
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(CAMERA_TEMP_IMAGE_PATH)));
            intentCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(intentCamera, REQUEST_CODE_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "sdcard not exist!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void getImageFromAlbum(View anchor, PickPhotoCallback callback) {

    }

    @Override
    public void getImageFromNet(View anchor, PickPhotoCallback callback) {

    }

    @Override
    public void importImageBatch(View anchor, PickPhotoCallback callback) {

    }

    @Override
    public void dim() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.9f;
        this.getWindow().setAttributes(lp);
    }

    @Override
    public void undim() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1f;
        this.getWindow().setAttributes(lp);
    }

    @Override
    public void bootRecord() {
        if (getRecorderContext().isBooted())
            return;
        preChangePage(new Runnable() {
            @Override
            public void run() {
                getRecorderContext().boot();
            }
        });
    }

    /**
     * 暂停记录
     */
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

    /**
     * 继续记录
     */
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
        showLoading(getString(R.string.loading), null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPageRecorder().deleteRecordDir();
                dismissLoading();
                finish();
            }
        }).start();
    }

    private void showLoading(String msg, OnKeyListener onKeyListener) {
        mLoadingDialog = ProgressDialogOffer.offerDialogAsActivity(this, msg);
        mLoadingDialog.setOnKeyListener(onKeyListener);
        mLoadingDialog.show();
    }

    private void dismissLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
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

    @Override
    public void showEditClickPopup(final EditWidget edit) {
    }

    @Override
    public void showImageClickPopup(final ImageWidget imageWidget) {
    }

    @Override
    public Page getPageAtPosition(int position) {
        return pages.get(position);
    }

    @Override
    public void addGlobalMusic(MusicBean music) {
        globalMusics.add(music);
    }

    // ---------------------------------------------------------------------------------------

    /**
     * 开启播放服务
     */
    private void toStartPlayService() {
        Intent it = new Intent(context(), PlayService.class);
        it.putExtra(PlayProcess.EXTRA_ACTION, PlayProcess.ACTION_MAKE_RELEASE_SCRIPT);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        it.putExtra(PlayProcess.EXTRA_RECORD_DIR, getRecordDir());
        it.putExtra(PlayProcess.EXTRA_SCREEN_INFO, ScreenFitUtil.getCurrentDeviceInfo());
        context().startService(it);
    }
}
