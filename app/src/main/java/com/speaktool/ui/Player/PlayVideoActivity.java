package com.speaktool.ui.Player;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ViewFlipper;

import com.speaktool.R;
import com.speaktool.SpeakApp;
import com.speaktool.api.Page;
import com.speaktool.api.Page.Page_BG;
import com.speaktool.impl.api.Play;
import com.speaktool.impl.api.PlayMode;
import com.speaktool.impl.bean.ClearPageData;
import com.speaktool.impl.bean.CopyPageData;
import com.speaktool.bean.ScreenInfoBean;
import com.speaktool.busevents.PlayTimeChangedEvent;
import com.speaktool.impl.DrawModeManager;
import com.speaktool.impl.modes.DrawModePath;
import com.speaktool.impl.player.JsonScriptPlayer;
import com.speaktool.impl.recorder.PageRecorder;
import com.speaktool.impl.recorder.RecorderContext;
import com.speaktool.impl.shapes.EditWidget;
import com.speaktool.impl.shapes.ImageWidget;
import com.speaktool.ui.Draw.RecordBean;
import com.speaktool.utils.ScreenFitUtil;
import com.speaktool.view.layouts.DrawPage;
import com.speaktool.view.layouts.VideoSeekBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
public class PlayVideoActivity extends FragmentActivity implements Play {
    public static final String EXTRA_RECORD_BEAN = "record_bean";

    @BindView(R.id.drawBoardContainer) ViewFlipper viewFlipper;// 绘画板容器
    @BindView(R.id.ivPlayPause) ImageView ivPlayPause;// 播放暂停
    @BindView(R.id.vsb_bar) VideoSeekBar vSeekBar;// 视频播放控制器

    private List<Page> pages = new ArrayList<Page>();// 界面集合
    private JsonScriptPlayer mJsonScriptPlayer;// JSON脚本播放器
    private int currentBoardIndex = 0;// 当前界面索引

    RecordBean recordBean;
    private int pageWidth;
    private int pageHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        recordBean = (RecordBean) getIntent().getSerializableExtra(EXTRA_RECORD_BEAN);

        mJsonScriptPlayer = new JsonScriptPlayer(recordBean, this);

        ivPlayPause.setVisibility(View.INVISIBLE);// 隐藏播放器
        ScreenInfoBean currentScreen = ScreenFitUtil.getCurrentDeviceInfo();
        pageWidth = currentScreen.width;
        pageHeight = currentScreen.height;
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

    @Override
    public void createPageImpl(Page_BG backgroundType, int position, int pageId) {
        DrawPage board = new DrawPage(this, backgroundType, this, pageId);
        pages.add(position, board);
    }

    @Override
    public void copyPageImpl(int srcPageId, int destPageId, String option) {
        Page srcPage = getPageFromId(srcPageId);
        Page destPage = getPageFromId(destPageId);
        if (CopyPageData.OPT_COPY_ALL.equals(option)) {
            srcPage.copyAllTo(destPage);
        } else {
            srcPage.copyViewsTo(destPage);
        }
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

    @Override
    public void resetAllViews() {
        viewFlipper.removeAllViews();
        pages.clear();
        currentBoardIndex = 0;
        DrawPage.resetShapeId(PlayMode.PLAY);

        DrawModeManager.getIns().setDrawMode(new DrawModePath());
    }

    @Override
    protected void onStop() {
        if (mJsonScriptPlayer.isPlaying()) {
            mJsonScriptPlayer.pause();
            ivPlayPause.setImageResource(android.R.drawable.ic_media_play);
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mJsonScriptPlayer.exitPlayer();
        EventBus.getDefault().unregister(this);
        DrawPage.resetShapeId(PlayMode.PLAY);
        PlayService.killServiceProcess(this);
        super.onDestroy();
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


    // 获取当前页面（画板）
    @Override
    public Page getCurrentBoard() {
        if (currentBoardIndex < 0 || currentBoardIndex >= pages.size()) {
            return null;
        } else {
            return pages.get(currentBoardIndex);
        }
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
    private RecorderContext mRecorderContext;

    @Override
    public PageRecorder getPageRecorder() {
        if (mPageRecorder == null)
            mPageRecorder = new PageRecorder(this);
        return mPageRecorder;
    }

    @Override
    public RecorderContext getRecorderContext() {
        if (mRecorderContext == null)
            mRecorderContext = new RecorderContext();
        return mRecorderContext;
    }

    @Override
    public void postTaskToUiThread(Runnable task) {
        SpeakApp.getUiHandler().post(task);
    }

    @Override
    public void setPageBackgroundImpl(int pageId, Page_BG backgroundType) {
        Page board = getPageFromId(pageId);
        if (board != null) {
            board.setBackgroundType(backgroundType);
        }
    }

    @Override
    public String getRecordDir() {
        return recordBean.dir;
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
    public void showEditClickPopup(EditWidget edit) {
    }

    @Override
    public void showImageClickPopup(ImageWidget imageWidget) {
    }

    /**
     * 改变页面
     */
    @Override
    public void preChangePage(final Runnable successRunnable) {
//        showLoading("正在加载", null);
    }

}
