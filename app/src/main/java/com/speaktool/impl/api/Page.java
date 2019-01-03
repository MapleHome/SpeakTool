package com.speaktool.impl.api;

import android.view.View;

import com.speaktool.api.FocusedView;
import com.speaktool.impl.cmd.ICmd;
import com.speaktool.impl.shapes.Path_;
import com.speaktool.impl.shapes.PenShape_;
import com.speaktool.impl.shapes.Point_;
import com.speaktool.impl.shapes.Shape_;
import com.speaktool.impl.shapes.ViewShape_;

/**
 * 画纸接口
 *
 * @author shaoshuai
 */
public interface Page {

    enum Page_BG {
        White,  // 白色背景
        Line,   // 线条背景
        Grid,   // 网格背景
        Cor     // 坐标背景
    }

    /**
     * 默认背景
     */
    Page_BG DEFAULT_PAGE_BG_TYPE = Page_BG.White;

    /**
     * 获取背景类型
     */
    Page_BG getBackgroundType();

    /**
     * 设置背景类型
     */
    void setBackgroundType(Page_BG bgType);


    View view();

    /**
     * 获取纸张ID
     */
    int getPageID();

    void addImg(String resName);

    /**
     * 清除所有
     */
    void clearShapeAndViews();

    /**
     * 清除笔记
     */
    void clearPenShapes();

    void copyAllTo(final Page dest);

    void copyViewsTo(final Page dest);

    /**
     * 操作撤销
     */
    void undo();

    /**
     * 对撤销进行返回（重新添加）
     */
    void redo();

    /**
     * 添加【操作撤销】命令
     */
    void addUndoCmd(ICmd cmd);

    /**
     * 添加【操作返回】命令
     */
    void addRedoCmd(ICmd cmd);

    /**
     * 更新【操作撤销】和【操作返回】状态
     */
    void updateUndoRedoState();

    /**
     * 发送命令
     *
     * @param cmd                 命令
     * @param isJustSendToPlaying 只是发送到播放器
     */
    void sendCommand(ICmd cmd, boolean isJustSendToPlaying);

    Shape_ shape(int id);

    void deleteShape(int id);

    //
    void saveShape(PenShape_ shape);

    void saveShape(ViewShape_ shape);

    //
    void drawOnTemp(Path_ path);

    //
    void drawOnBuffer(Path_ path);

    void drawOnBuffer(Point_ point);

    //
    void draw(ViewShape_ viewShape);

    //
    void refresh();

    //
    void unDraw(PenShape_ shape);

    void unDraw(ViewShape_ view);


    FocusedView getFocusedView();

    void setFocusedView(FocusedView focus);

    int makeShapeId();

    /**
     * 用户是否还有可撤销or返回操作
     */
    boolean isUserHaveOperation();

//    int getPlayBoardWidth();

    /**
     * 重绘缓存Bitmap
     */
    void redrawBufferBitmap();

    /**
     * 回收缓存Bitmap
     */
    void recycleBufferBitmap();
    //

}
