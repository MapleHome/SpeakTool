package com.speaktool.api;

import android.content.Context;

import com.speaktool.impl.recorder.PageRecorder;
import com.speaktool.impl.recorder.RecorderContext;
import com.speaktool.impl.shapes.EditWidget;
import com.speaktool.impl.shapes.ImageWidget;

/**
 * @author maple
 * @time 2018/12/14
 */
public interface BaseDraw {

    Context context();

    int makePageWidth();

    int makePageHeight();

    void postTaskToUiThread(Runnable task);

    /**
     * 获取画纸页面记录器
     */
    PageRecorder getPageRecorder();

    /**
     * 获取画纸页面记录器上下文
     */
    RecorderContext getRecorderContext();

    /**
     * 显示视频控制器
     */
    void showVideoController();

    String getRecordDir();

    /**
     * 获取当前画纸
     */
    Page getCurrentBoard();

    /**
     * 获取画板模式
     */
    PlayMode getPlayMode();

    /**
     * 设置指定id的画纸为当前活动画纸
     *
     * @param pageId - 画纸ID
     */
    void setActivePageImpl(int pageId);

    /**
     * 创建画板纸张
     *
     * @param bgType   - 纸张背景类型
     * @param position - 纸张在画册中的索引
     * @param pageId   - 纸张ID
     */
    void createPageImpl(Page.Page_BG bgType, int position, int pageId);

    /**
     * 清除页面图片
     *
     * @param pageId
     * @param option
     */
    void clearPageImpl(int pageId, String option);

    Page deletePageImpl(int pageId);

    /**
     * 复制页面图片
     *
     * @param srcPageId  源页面ID
     * @param destPageId 目标页面ID
     * @param option     操作类型
     */
    void copyPageImpl(int srcPageId, int destPageId, String option);


    void setPageBackgroundImpl(int pageId, Page.Page_BG bgType);


    //
    void preChangePage(final Runnable successRunnable);


    //----------------------------------------------------

    void showEditClickPopup(EditWidget edit);

    void showImageClickPopup(ImageWidget imageWidget);
}
