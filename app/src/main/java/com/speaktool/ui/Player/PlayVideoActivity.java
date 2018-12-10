package com.speaktool.ui.Player;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.speaktool.Const;
import com.speaktool.R;
import com.speaktool.SpeakApp;
import com.speaktool.api.Draw;
import com.speaktool.api.Page;
import com.speaktool.api.Page.Page_BG;
import com.speaktool.bean.ActivePageData;
import com.speaktool.bean.ClearPageData;
import com.speaktool.bean.CopyPageData;
import com.speaktool.bean.CreatePageData;
import com.speaktool.bean.LocalRecordBean;
import com.speaktool.bean.MusicBean;
import com.speaktool.bean.PageBackgroundData;
import com.speaktool.bean.RecordUploadBean;
import com.speaktool.bean.ScreenInfoBean;
import com.speaktool.busevents.PlayTimeChangedEvent;
import com.speaktool.busevents.RefreshCourseListEvent;
import com.speaktool.impl.DrawModeManager;
import com.speaktool.impl.cmd.clear.CmdClearPage;
import com.speaktool.impl.cmd.copy.CmdCopyPage;
import com.speaktool.impl.cmd.create.CmdActivePage;
import com.speaktool.impl.cmd.create.CmdCreatePage;
import com.speaktool.impl.cmd.transform.CmdChangePageBackground;
import com.speaktool.impl.modes.DrawModePath;
import com.speaktool.impl.player.JsonScriptPlayer;
import com.speaktool.impl.player.PlayProcess;
import com.speaktool.impl.player.SoundPlayer;
import com.speaktool.impl.recorder.PageRecorder;
import com.speaktool.impl.recorder.RecorderContext;
import com.speaktool.impl.shapes.EditWidget;
import com.speaktool.impl.shapes.ImageWidget;
import com.speaktool.utils.ScreenFitUtil;
import com.speaktool.utils.T;
import com.speaktool.utils.record.RecordFileAnalytic;
import com.speaktool.view.dialogs.LoadingDialog;
import com.speaktool.view.layouts.DrawPage;
import com.speaktool.view.layouts.VideoSeekBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.FragmentActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 播放本地视频
 *
 * @author shaoshuai
 */
public class PlayVideoActivity extends FragmentActivity implements Draw {
    public static final String EXTRA_RECORD_BEAN = "record_bean";

    @BindView(R.id.drawBoardContainer) ViewFlipper viewFlipper;// 绘画板容器
    @BindView(R.id.viewFlipperOverlay) View viewFlipperOverlay;// 文本
    @BindView(R.id.ivPlayPause) ImageView ivPlayPause;// 播放暂停
    @BindView(R.id.layoutVideoController) VideoSeekBar vSeekBar;// 视频播放控制器

    // 常量
    private List<MusicBean> globalMusics = new ArrayList<>();// 添加音乐集合
    private List<Page> pages = new ArrayList<Page>();// 界面集合
    private JsonScriptPlayer mJsonScriptPlayer;// JSON脚本播放器
    private int currentBoardIndex = 0;// 当前界面索引
    private String mRecordDir;// 课程目录

    LocalRecordBean rec;
    private int pageWidth;
    private int pageHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(null);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);// inject finish.
        setContentView(R.layout.activity_play_video);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        rec = (LocalRecordBean) getIntent().getSerializableExtra(EXTRA_RECORD_BEAN);
        // 检查是否为空
//        Preconditions.checkNotNull(rec, "null LocalRecordBean handle to play.");
        mJsonScriptPlayer = new JsonScriptPlayer(rec, this);

        ivPlayPause.setVisibility(View.INVISIBLE);// 隐藏播放器
        ScreenInfoBean currentScreen = ScreenFitUtil.getCurrentDeviceInfo();
        pageWidth = currentScreen.width;
        pageHeight = currentScreen.height;
        // 设置播放器大小
        LayoutParams lp = (LayoutParams) vSeekBar.getLayoutParams();
        lp.width = pageWidth;
        vSeekBar.setLayoutParams(lp);
        //
        LayoutParams lp2 = (LayoutParams) viewFlipperOverlay.getLayoutParams();
        lp2.width = pageWidth;
        lp2.height = pageHeight;
        viewFlipperOverlay.setLayoutParams(lp2);
        //
        initListener();

        DrawModeManager.getIns().setDrawMode(new DrawModePath());
        mJsonScriptPlayer.play();

    }

    private void initListener() {
        // 播放暂停
        ivPlayPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ivPlayPause:// 播放暂停
                        Log.e("本地视频播放", "点击暂停");
                        if (mJsonScriptPlayer.isPlayComplete()) {
                            mJsonScriptPlayer.rePlay();
                            ivPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                            break;
                        }
                        if (mJsonScriptPlayer.isPlaying()) {
                            mJsonScriptPlayer.pause();
                            ivPlayPause.setImageResource(android.R.drawable.ic_media_play);
                        } else {
                            mJsonScriptPlayer.goOn();
                            ivPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                        }
                        break;
                }
            }
        });
        // 播放进度改变监听
        vSeekBar.setSeekListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mJsonScriptPlayer.seekTo(seekBar.getProgress());
                ivPlayPause.setVisibility(View.INVISIBLE);// 隐藏播放器
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
        mJsonScriptPlayer.exitPlayer();
        finish();
        killPlayProcess();// must do.
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
        CmdCreatePage cmd = new CmdCreatePage();
        cmd.setTime(getPageRecorder().recordTimeNow());
        cmd.setData(new CreatePageData(pageId, position, backgroundType));
        getCurrentBoard().sendCommand(cmd, true);
        return pages.size();
    }

    @Override
    public void createPageImpl(Page_BG backgroundType, int position, int pageId) {
        DrawPage board = new DrawPage(this, backgroundType, this, pageId);
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
        CmdCopyPage cmd = new CmdCopyPage();
        cmd.setTime(getPageRecorder().recordTimeNow());
        cmd.setData(new CopyPageData(srcPageId, destPageId, option));
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
        CmdClearPage cmd = new CmdClearPage();
        cmd.setTime(getPageRecorder().recordTimeNow());
        cmd.setData(new ClearPageData(pageId, option));
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
            ivPlayPause.setVisibility(View.INVISIBLE);// 隐藏播放器
        }
    };

    @Override
    public void showVideoController() {
        if (ivPlayPause.getVisibility() == View.VISIBLE) {
            ivPlayPause.setVisibility(View.INVISIBLE);
            vSeekBar.removeCallbacks(hideVideoControllerRunnable);
            return;
        }
        ivPlayPause.setVisibility(View.VISIBLE);
        vSeekBar.postDelayed(hideVideoControllerRunnable, 1000);
    }

    @Override
    public void onPlayComplete() {
        postTaskToUiThread(new Runnable() {
            @Override
            public void run() {
                // 播放完成，更新UI
                ivPlayPause.setImageResource(android.R.drawable.ic_media_play);
                int p = JsonScriptPlayer.MAX_PROGRESS;
                vSeekBar.setProgress(p);
                vSeekBar.setProgressText(p);
            }
        });
    }

    @Override
    public void onPlayStart() {
        ivPlayPause.setImageResource(android.R.drawable.ic_media_pause);
    }

    // =====================视频播放器--结束=======================================

    @Override
    public void resetAllViews() {
        viewFlipper.removeAllViews();
        pages.clear();
        currentBoardIndex = 0;
        pageID = 0;
        DrawPage.resetShapeId(this);

        DrawModeManager.getIns().setDrawMode(new DrawModePath());
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        pageID = 0;
        DrawPage.resetShapeId(this);

        killPlayProcess();
        if (isRecordsChanged) {
            EventBus.getDefault().post(new RefreshCourseListEvent());
        }
        SoundPlayer.unique().stop();// stop other sound.
        super.onDestroy();
    }

    private void killPlayProcess() {
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
        int position = pages.indexOf(bd);
        if (position < 0 || position >= pages.size())
            return;
        setActivePageImpl(id);

        CmdActivePage cmd = new CmdActivePage(
                getPageRecorder().recordTimeNow(),// time
                new ActivePageData(id)// data:page id
        );
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

    @Override
    public void setActivePageImpl(int pageId) {
        Page befpage = getCurrentBoard();
        if (befpage != null) {
            befpage.recycleBufferBitmap();
        }
        //
        Page bd = getPageFromId(pageId);
        bd.redrawBufferBitmap();
        int position = pages.indexOf(bd);
        //
        viewFlipper.removeAllViews();
        viewFlipper.addView(bd.view());
        viewFlipper.setDisplayedChild(0);

        currentBoardIndex = position;
        //
        getCurrentBoard().updateUndoRedoState();
        DrawModeManager.getIns().setDrawMode(new DrawModePath());

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
                boolean isSuccess = RecordFileAnalytic.setRecordInfos(getPageRecorder().getDir(), recordUploadBean);
//                final boolean isSuccess = getPageRecorder().setRecordInfos(recordUploadBean);
                if (!isSuccess) {
                    dismissLoading();
                    postTaskToUiThread(new Runnable() {
                        public void run() {
                            T.showShort(context(), getString(R.string.save_recordinfo_fail));
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
        if (mJsonScriptPlayer.isPlaying()) {
            mJsonScriptPlayer.pause();
            ivPlayPause.setImageResource(android.R.drawable.ic_media_play);
        }
        super.onStop();
    }

    // =====================top pop=======================================
    @Subscribe
    public void onEventMainThread(PlayTimeChangedEvent event) {
        // 更新播放进度条
        long now = event.getNow();//  当前进度
        long total = event.getCloseTime(); // 总时长
        int p = (int) (((float) now / (float) total) * JsonScriptPlayer.MAX_PROGRESS);

        vSeekBar.setProgress(p);
        vSeekBar.setProgressText(now);
        vSeekBar.setTotalDuration(total);
    }

    @Override
    public Page deletePageImpl(int pageId) {
        Page bd = getPageFromId(pageId);
        int position = pages.indexOf(bd);

        if (position < 0 || position >= pages.size())
            return null;
        pages.remove(position);
        if (position == pages.size()) {// removed.
            bd = pages.get(position - 1);
        } else {
            bd = pages.get(position);
        }
        currentBoardIndex = pages.indexOf(bd);
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
        cmd.setData(new PageBackgroundData(pageId, backgroundType));
        cmd.setTime(getPageRecorder().recordTimeNow());
        getCurrentBoard().sendCommand(cmd, true);

    }

    private static final int REQUEST_CODE_IMAGE_CAPTURE = 1;
    private static final String CAMERA_TEMP_IMAGE_PATH = Const.TEMP_DIR + "/camera_temp.jpg";
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
        mLoadingDialog = new LoadingDialog(this, msg);
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
        return PlayMode.PLAY;
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
