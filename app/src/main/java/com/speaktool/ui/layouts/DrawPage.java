package com.speaktool.ui.layouts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;

import com.google.common.collect.Maps;
import com.speaktool.R;
import com.speaktool.api.Draw;
import com.speaktool.api.Draw.PlayMode;
import com.speaktool.api.FocusedView;
import com.speaktool.api.Page;
import com.speaktool.bean.ImageCommonData;
import com.speaktool.bean.ScreenInfoBean;
import com.speaktool.busevents.EraserEvent;
import com.speaktool.busevents.RedoEvent;
import com.speaktool.busevents.UndoEvent;
import com.speaktool.impl.DrawModeManager;
import com.speaktool.impl.cmd.ICmd;
import com.speaktool.impl.cmd.create.CmdCreateImage;
import com.speaktool.impl.cmd.create.CmdCreatePen;
import com.speaktool.impl.cmd.delete.CmdDeletePen;
import com.speaktool.impl.modes.DrawModeChoice;
import com.speaktool.impl.paint.DrawPaint;
import com.speaktool.impl.paint.EraserPaint;
import com.speaktool.impl.shapes.Path_;
import com.speaktool.impl.shapes.PenShape_;
import com.speaktool.impl.shapes.Point_;
import com.speaktool.impl.shapes.Shape_;
import com.speaktool.impl.shapes.ViewShape_;
import com.speaktool.ui.custom.gif.GifDrawable;
import com.speaktool.utils.BitmapScaleUtil;
import com.speaktool.utils.DisplayUtil;
import com.speaktool.utils.ScreenFitUtil;
import com.speaktool.utils.T;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;


/**
 * 【画纸】- 画板纸张界面
 *
 * @author shaoshuai
 */
@SuppressWarnings({"deprecation", "rawtypes"})
public class DrawPage extends AbsoluteLayout implements Page {
    private Page_BG backgroundType;// 背景类型
    private int id = -1;// 画板纸张ID

    private int screenWidth;
    private int screenHeight;

    private final Paint bufferPaint = new Paint(Paint.DITHER_FLAG);
    public Bitmap bufferBitmap;
    private Canvas bufferCanvas;
    private int mPlayBoardWidth;
    private final List<PenShape_> penShapes = new ArrayList<PenShape_>();

    private final static int MAX_UNDO_SIZE = 100;// 最大可撤销次数
    private Stack<ICmd> cmdsUndo = new Stack<ICmd>();// 操作撤销集合
    private Stack<ICmd> cmdsRedo = new Stack<ICmd>();// 操作返回集合

    private FocusedView mFocusedView;
    private Draw draw;
    //
    private static int shapeId = 0;
    private static int playShapeId = 0;
    private Path_ mPath_;

    /**
     * 初始化画板纸张
     */
    public DrawPage(Context context, Page_BG backgroundType, Draw draw, int pageId) {
        super(context);
        this.setBackgroundType(backgroundType);
        this.draw = draw;
        this.id = pageId;
        init();
    }

    private void init() {
        Point screenSize = DisplayUtil.getScreenSize(getContext());
        screenWidth = screenSize.x;// 1920
        screenHeight = screenSize.y;// 1080

        bufferBitmap = createBufferBitmap();
        bufferCanvas = new Canvas(bufferBitmap);
        //
        FrameLayout.LayoutParams lpt = new FrameLayout.LayoutParams(0, 0);
        lpt.width = draw.makePageWidth();// -1 LayoutParams.MATCH_PARENT
        lpt.height = draw.makePageHeight();// 750 高度减去顶部和底部 功能条
        lpt.gravity = Gravity.CENTER;
        this.setLayoutParams(lpt);
    }

    // 实现接口 - 重绘缓存图像
    @Override
    public void redrawBufferBitmap() {
        bufferBitmap = createBufferBitmap();
        bufferCanvas = new Canvas(bufferBitmap);
        /** redraw pen notes. */
        int size = penShapes.size();
        for (int i = 0; i < size; i++) {
            PenShape_ pen = penShapes.get(i);
            pen.drawToPage(this);
        }
        refresh();
    }

    /**
     * 创建缓存图像
     */
    private Bitmap createBufferBitmap() {
        if (screenWidth * screenHeight > 512 * 512) {
            Bitmap orgBitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
            // 可能会造成失败，内存错误。
            Bitmap scaledBmp = Bitmap.createScaledBitmap(orgBitmap, screenWidth, screenHeight, true);
            orgBitmap.recycle();// 回收
            return scaledBmp;
        } else {
            Bitmap orgBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
            // may create fail,memory err.
            return orgBitmap;
        }
    }

    // 实现接口 - 回收缓存Bitmap
    @Override
    public void recycleBufferBitmap() {
        if (bufferBitmap != null) {
            bufferBitmap.recycle();
            bufferBitmap = null;
            bufferCanvas = null;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        setBackgroundType(backgroundType);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        this.setBackgroundDrawable(null);
        super.onDetachedFromWindow();
    }

    // 实现接口 - 添加【操作撤销】命令
    @Override
    public void addUndoCmd(ICmd cmd) {
        if (cmdsUndo.size() == MAX_UNDO_SIZE) {
            cmdsUndo.remove(0);
        }
        cmdsUndo.push(cmd);
    }

    // 实现接口 - 添加【操作返回】命令
    @Override
    public void addRedoCmd(ICmd cmd) {
        cmdsRedo.push(cmd);
    }

    // 实现接口 - 用户是否还可撤销或者返回
    @Override
    public boolean isUserHaveOperation() {
        return !cmdsUndo.isEmpty() || !cmdsRedo.isEmpty();
    }

    @Override
    public int getPlayBoardWidth() {
        return mPlayBoardWidth;
    }

    @Override
    public void copyAllTo(final Page dest) {
        /** copy views. */
        int counts = this.getChildCount();
        for (int i = 0; i < counts; i++) {
            Shape_ sp = (Shape_) this.getChildAt(i);
            if (sp instanceof ViewShape_) {
                ViewShape_ copy = ((ViewShape_) sp).copySelf();
                copy.drawToPage(dest);
                dest.saveShape(copy);
            }
        }
        /** copy pen notes. */
        int size = penShapes.size();
        for (int i = 0; i < size; i++) {
            PenShape_ pen = penShapes.get(i);
            pen.drawToPage(dest);
            dest.saveShape(pen);
        }
        //
        this.copyRedoCmdsTo(dest, true);
        this.copyUndoCmdsTo(dest, true);

        dest.refresh();
        dest.updateUndoRedoState();
        //
    }

    @Override
    public void copyViewsTo(final Page dest) {
        int counts = this.getChildCount();
        for (int i = 0; i < counts; i++) {
            Shape_ sp = (Shape_) this.getChildAt(i);
            if (sp instanceof ViewShape_) {
                ViewShape_ copy = ((ViewShape_) sp).copySelf();
                copy.drawToPage(dest);
                dest.saveShape(copy);
            }
        }
        this.copyRedoCmdsTo(dest, false);
        this.copyUndoCmdsTo(dest, false);

        dest.refresh();
        dest.updateUndoRedoState();
    }

    public void setFocusedView(FocusedView focus) {
        mFocusedView = focus;
    }

    public FocusedView getFocusedView() {
        return mFocusedView;
    }

    private void postEvent(Object event) {
        if (isMakeMode())
            EventBus.getDefault().post(event);// 通过EventBus订阅者发送消息
    }

    @Override
    public void saveShape(PenShape_ shape) {
        allShapeViews.put(shape.getShapeID(), shape);

        penShapes.add(shape);
        postEvent(new EraserEvent(true));
    }

    @Override
    public void saveShape(ViewShape_ shape) {
        allShapeViews.put(shape.getShapeID(), shape);
    }

    private void removePenShape(PenShape_ shape) {
        penShapes.remove(shape);
        if (penShapes.isEmpty()) {
            postEvent(new EraserEvent(false));
        }
    }

    private void copyUndoCmdsTo(Page dest, boolean isIncludeShape) {
        ICmd cmd = null;
        for (int i = 0; i < this.cmdsUndo.size(); i++) {
            cmd = cmdsUndo.get(i);
            if (isIncludeShape)
                dest.addUndoCmd(cmd.copy());
            else if (!(cmd instanceof CmdCreatePen))
                dest.addUndoCmd(cmd.copy());
        }
    }

    private void copyRedoCmdsTo(Page dest, boolean isIncludeShape) {
        for (ICmd cd : this.cmdsRedo) {
            if (isIncludeShape)
                dest.addRedoCmd(cd.copy());
            else if (!(cd instanceof CmdCreatePen))
                dest.addRedoCmd(cd.copy());
        }
    }

    // 实现接口 - 操作撤销
    @Override
    public void undo() {
        if (cmdsUndo.isEmpty()) {
            postEvent(new UndoEvent(false));
            return;
        }
        ICmd cmd = cmdsUndo.pop();
        ICmd inverseCmd = cmd.inverse();

        inverseCmd.run(draw, this);
        draw.postTaskToUiThread(new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        });

        inverseCmd.setTime(draw.getPageRecorder().recordTimeNow());

        draw.getPageRecorder().record(inverseCmd, this.getPageID());
        cmdsRedo.push(cmd);
        postEvent(new RedoEvent(true));
        if (cmdsUndo.isEmpty()) {
            postEvent(new UndoEvent(false));
            return;

        }
    }

    // 实现接口 - 操作返回
    @Override
    public void redo() {
        if (cmdsRedo.isEmpty()) {
            postEvent(new RedoEvent(false));
            return;
        }
        ICmd cmd = cmdsRedo.pop();

        cmd.run(draw, this);
        draw.postTaskToUiThread(new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        });
        //
        ICmd copy = cmd.copy();

        copy.setTime(draw.getPageRecorder().recordTimeNow());

        draw.getPageRecorder().record(copy, this.getPageID());
        addUndoCmd(cmd);
        postEvent(new UndoEvent(true));
        if (cmdsRedo.isEmpty()) {
            postEvent(new RedoEvent(false));
            return;

        }
    }

    // 实现接口 - 更新撤销和返回的状态
    @Override
    public void updateUndoRedoState() {
        // 操作返回
        if (cmdsRedo.isEmpty()) {// 返回集合为空
            postEvent(new RedoEvent(false));// 禁用操作返回
        } else {
            postEvent(new RedoEvent(true));// 启用操作返回
        }
        // 操作撤销
        if (cmdsUndo.isEmpty()) {// 撤销集合为空
            postEvent(new UndoEvent(false));// 禁用操作撤销
        } else {
            postEvent(new UndoEvent(true));// 启用操作撤销
        }
    }

    // 实现接口 - 清除笔记
    @Override
    public void clearPenShapes() {
        recycleBufferBitmap();
        bufferBitmap = createBufferBitmap();
        bufferCanvas = new Canvas(bufferBitmap);

        for (Shape_ sp : penShapes) {
            allShapeViews.remove(sp.getShapeID());
        }
        penShapes.clear();
        Iterator<ICmd> itUndo = cmdsUndo.iterator();
        while (itUndo.hasNext()) {
            ICmd cd = itUndo.next();
            if (cd instanceof CmdCreatePen || cd instanceof CmdDeletePen) {
                itUndo.remove();
            }
        }
        Iterator<ICmd> itRedo = cmdsRedo.iterator();
        while (itRedo.hasNext()) {
            ICmd cd = itRedo.next();
            if (cd instanceof CmdCreatePen || cd instanceof CmdDeletePen) {
                itRedo.remove();
            }
        }
        postEvent(new EraserEvent(false));
        updateUndoRedoState();
        invalidate();
    }

    // 实现接口——清除所有
    @Override
    public void clearShapeAndViews() {
        recycleBufferBitmap();
        bufferBitmap = createBufferBitmap();
        bufferCanvas = new Canvas(bufferBitmap);
        penShapes.clear();
        //
        this.removeAllViews();
        //
        allShapeViews.clear();

        cmdsUndo.clear();
        cmdsRedo.clear();
        postEvent(new EraserEvent(false));
        updateUndoRedoState();

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isMakeMode()) {
            if (event.getAction() == MotionEvent.ACTION_UP)
                draw.showVideoController();
            return true;
        }
        return DrawModeManager.getIns().doTouchEvent(event, draw);
    }

    @Override
    public void refresh() {
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (bufferBitmap != null) {
            canvas.drawBitmap(bufferBitmap, 0, 0, bufferPaint);
        }
        if (mPath_ != null && !mPath_.isEraser()) {
            canvas.drawPath(mPath_.getPath(), new DrawPaint(mPath_.getColor(), mPath_.getStokeWidth()).getPaint());
        }
    }

    private void addGif(String gifName) {
        try {
            String imagpath = String.format("%s%s%s", draw.getRecordDir(), File.separator, gifName);
            final OuterImage img = new OuterImage(this.getContext(), draw, this.makeShapeId());
            GifDrawable gifd = new GifDrawable(imagpath);

            img.setResourceID(gifName);
            //
            Bitmap srcbmp = gifd.getCurrentFrame();
            float cW = srcbmp.getWidth();
            float cH = srcbmp.getHeight();
            float pW = DrawPage.this.getWidth();
            float pH = DrawPage.this.getHeight();
            float factorY = pH / cH;
            img.setScaleX(factorY);
            img.setScaleY(factorY);
            img.setPosition((int) ((pW - cW) / 2), (int) ((pH - cH) / 2), false);
            // locate to center.
            //
            draw(img);
            saveShape(img);
            DrawModeManager.getIns().setDrawMode(new DrawModeChoice());
            //
            CmdCreateImage cmd = new CmdCreateImage();
            cmd.setTime(draw.getPageRecorder().recordTimeNow());
            ImageCommonData data = new ImageCommonData();
            OuterImage.copyAttrsToData(data, img);

            cmd.setData(data);
            sendCommand(cmd, false);
        } catch (Exception e) {
            e.printStackTrace();
            T.showShort(getContext(), "图片添加失败！");
        }

    }

    @Override
    public void addImg(String resName) {
        if (TextUtils.isEmpty(resName)) {
            T.showShort(getContext(), "图片添加失败！");
            return;
        }
        if (BitmapScaleUtil.isGif(resName)) {
            addGif(resName);
        } else {
            addStaticPic(resName);
        }
    }

    private void addStaticPic(String resName) {
        try {
            final OuterImage img = new OuterImage(this.getContext(), draw, this.makeShapeId());
            img.setResourceID(resName);
            BitmapDrawable bd = (BitmapDrawable) img.getDrawable();
            Bitmap srcbmp = bd.getBitmap();
            //
            float cW = srcbmp.getWidth();
            float cH = srcbmp.getHeight();
            float pW = DrawPage.this.getWidth();
            float pH = DrawPage.this.getHeight();
            float factorY = pH / cH;

            img.setScaleX(factorY);
            img.setScaleY(factorY);
            int px = (int) ((pW - cW) / 2);
            int py = (int) ((pH - cH) / 2);

            img.setPosition(px, py, false);

            //
            draw(img);
            saveShape(img);
            DrawModeManager.getIns().setDrawMode(new DrawModeChoice());
            //
            CmdCreateImage cmd = new CmdCreateImage();
            cmd.setTime(draw.getPageRecorder().recordTimeNow());
            ImageCommonData data = new ImageCommonData();
            OuterImage.copyAttrsToData(data, img);

            cmd.setData(data);
            sendCommand(cmd, false);
            //
        } catch (Exception e) {
            e.printStackTrace();
            T.showShort(getContext(), "图片添加失败！");
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        /**
         * use to save info in make mode.
         */
        if (isMakeMode()) {
            ScreenInfoBean info = new ScreenInfoBean();
            info.w = this.getWidth();
            info.h = this.getHeight();
            info.density = DisplayUtil.getScreenDensity(getContext());
            ScreenFitUtil.setCurrentDeviceInfo(info);
            ScreenFitUtil.setInputDeviceInfo(info);
        }
        //
        super.onLayout(changed, l, t, r, b);
    }

    private boolean isMakeMode() {
        return draw.getPlayMode() == PlayMode.MAKE;
    }

    @Override
    public View view() {
        return this;
    }

    // 实现接口 - 发送命令
    @Override
    public void sendCommand(ICmd cmd, boolean isJustSendToPlaying) {
        ICmd copy = cmd.copy();
        if (draw.getRecorderContext().isRunning() || copy == null)
            draw.getPageRecorder().record(cmd, this.getPageID());
        else
            draw.getPageRecorder().record(copy, this.getPageID());
        if (isJustSendToPlaying)
            return;
        if (copy != null)
            addUndoCmd(copy);
        else
            addUndoCmd(cmd);
        cmdsRedo.clear();
        //
        postEvent(new RedoEvent(false));
        postEvent(new UndoEvent(true));
    }

    @Override
    public int getPageID() {
        return id;
    }

    private Map<Integer, Shape_> allShapeViews = Maps.newHashMap();

    @Override
    public void deleteShape(int id) {
        Shape_ obj = allShapeViews.remove(id);
        if (obj instanceof PenShape_)
            unDraw((PenShape_) obj);
        else
            unDraw((ViewShape_) obj);
    }

    @Override
    public Shape_ shape(int id) {
        return allShapeViews.get(id);

    }

    @Override
    public Page_BG getBackgroundType() {
        return backgroundType;
    }

    @Override
    public void setBackgroundType(Page_BG type) {
        final long maxMem = 5 * 1024 * 1024;
        //
        if (Page_BG.White.equals(type)) {
            this.setBackgroundColor(Color.WHITE);
        } else if (Page_BG.Line.equals(type)) {
            Bitmap bpscl = BitmapScaleUtil.decodeSampledBitmapFromResource(getResources(), R.drawable.line_bg, maxMem);
            if (bpscl != null)
                this.setBackgroundDrawable(new BitmapDrawable(bpscl));

        } else if (Page_BG.Grid.equals(type)) {
            Bitmap bpscl = BitmapScaleUtil.decodeSampledBitmapFromResource(getResources(), R.drawable.grid_bg, maxMem);
            if (bpscl != null)
                this.setBackgroundDrawable(new BitmapDrawable(bpscl));

        } else if (Page_BG.Cor.equals(type)) {
            Bitmap bpscl = BitmapScaleUtil.decodeSampledBitmapFromResource(getResources(),
                    R.drawable.draw_coordinate_bg, maxMem);
            if (bpscl != null)
                this.setBackgroundDrawable(new BitmapDrawable(bpscl));
        } else {
            this.setBackgroundColor(Color.WHITE);
        }
        backgroundType = type;
    }

    @Override
    public int makeShapeId() {
        if (isMakeMode())
            return ++shapeId;
        else
            return ++playShapeId;
    }

    public static void resetShapeId(Draw draw) {
        if (draw.getPlayMode() == PlayMode.MAKE)
            shapeId = 0;
        else
            playShapeId = 0;
    }

    @Override
    public void drawOnTemp(Path_ path) {
        mPath_ = path;
    }

    @Override
    public void drawOnBuffer(Path_ path) {
        if (!path.isEraser())
            bufferCanvas.drawPath(path.getPath(), new DrawPaint(path.getColor(), path.getStokeWidth()).getPaint());
        else
            bufferCanvas.drawPath(path.getPath(), new EraserPaint(path.getStokeWidth()).getPaint());
    }

    @Override
    public void drawOnBuffer(Point_ point) {
        if (!point.isEraser())
            bufferCanvas.drawPoint(point.getmX(), point.getmY(),
                    new DrawPaint(point.getColor(), point.getStokeWidth()).getPaint());
        else
            bufferCanvas.drawPoint(point.getmX(), point.getmY(), new EraserPaint(point.getStokeWidth()).getPaint());
    }

    @Override
    public void draw(ViewShape_ viewShape) {
        this.addView(viewShape.view());
    }

    @Override
    public void unDraw(ViewShape_ viewShape) {
        this.removeView(viewShape.view());
    }

    @Override
    public void unDraw(PenShape_ shape) {
        removePenShape(shape);
        recycleBufferBitmap();
        bufferBitmap = createBufferBitmap();
        bufferCanvas = new Canvas(bufferBitmap);
        for (int i = 0; i < penShapes.size(); i++) {
            PenShape_ penShape = penShapes.get(i);
            penShape.drawToPage(this);
        }
        invalidate();
    }

}
