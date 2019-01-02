package com.speaktool.impl.api;

import com.speaktool.impl.api.Page.Page_BG;
import com.speaktool.api.PhotoImporter;
import com.speaktool.ui.draw.RecordBean;

/**
 * 绘画模式
 *
 * @author maple
 * @time 2018/12/14
 */
public interface Draw extends BaseDraw, PhotoImporter {
    /**
     * 上一页
     */
    void preBoardClick();

    /**
     * 下一页
     */
    void nextBoardClick();

    /**
     * 根据索引获取界面
     */
    Page getPageAtPosition(int position);

    /**
     * 设置指定id的画纸为当前互动画纸
     *
     * @param pageId - 画纸ID
     */
    void setActivePageSendcmd(int pageId);

    /**
     * 创建画纸ID
     */
    int makePageId();

    void newEmptyBoardClick();

    /**
     * 复制当前页面
     *
     * @param option 操作类型
     */
    void copyPageClick(String option);

    /**
     * 清除页面内容
     *
     * @param pageId 页面ID
     * @param option 清除类型
     */
    void clearPageClick(int pageId, String option);


    void setPageBackgroundClick(int pageId, Page_BG bgType);

    /**
     * 启动记录器
     */
    void bootRecord();

    /**
     * 暂停记录器
     */
    void pauseRecord();

    /**
     * 继续记录器
     */
    void continueRecord();

    /**
     * 保存课程记录
     */
    void saveRecord(RecordBean saveInfo);

    /**
     * 退出画板
     */
    void onExitDraw();

    void exitDrawWithoutSave();

}
