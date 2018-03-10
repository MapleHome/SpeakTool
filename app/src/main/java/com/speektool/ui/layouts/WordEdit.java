package com.speektool.ui.layouts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.common.collect.Lists;
import com.speektool.R;
import com.speektool.api.Draw;
import com.speektool.api.FocusedView;
import com.speektool.api.Page;
import com.speektool.bean.ChangeEditData;
import com.speektool.bean.DeleteShapeData;
import com.speektool.bean.EditCommonData;
import com.speektool.bean.MoveData;
import com.speektool.bean.PositionData;
import com.speektool.busevents.CloseEditPopupWindowEvent;
import com.speektool.impl.cmd.create.CmdCreateEdit;
import com.speektool.impl.cmd.delete.CmdDeleteEdit;
import com.speektool.impl.cmd.transform.CmdChangeEditNoSeq;
import com.speektool.impl.cmd.transform.CmdMoveEdit;
import com.speektool.impl.modes.DrawModeCode;
import com.speektool.impl.shapes.EditWidget;
import com.speektool.impl.shapes.ViewShape_;
import com.speektool.manager.DrawModeManager;
import com.speektool.utils.DensityUtils;
import com.speektool.utils.MyColors;

import java.util.List;

import de.greenrobot.event.EventBus;

@SuppressWarnings("deprecation")
public class WordEdit extends EditText implements OnEditorActionListener, FocusedView, EditWidget {

    private Page mDrawBoard;
    private Draw draw;
    private Drawable leftIcon;
    private boolean isLocked = false;
    private boolean isInFocus = false;

    private PositionData getPos() {
        LayoutParams lp = (LayoutParams) this.getLayoutParams();
        if (lp == null)
            return null;
        return new PositionData(lp.x, lp.y);
    }

    private int id;

    public WordEdit(Context context, Draw draw, int id) {
        super(context);
        initBorderPaint();
        this.draw = draw;
        this.id = id;
        mDrawBoard = draw.getCurrentBoard();
        init();
    }

    private void init() {
        /*
		 * this.setPivotX(0); this.setPivotY(0);
		 */
        this.setOnEditorActionListener(this);
        this.setImeOptions(EditorInfo.IME_ACTION_UNSPECIFIED | EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        this.setSingleLine(false);
        this.setFocusable(true);

        this.setFocusableInTouchMode(true);

        this.requestFocus();
        this.setTextColor(MyColors.BLACK);
        leftIcon = getResources().getDrawable(R.drawable.edittext_left_icon);
        leftIcon.setBounds(10, 20, 20, 30);
        this.setCursorVisible(true);
        displayLeftIcon();

        this.setHint(" ");
        //
        this.setBackgroundDrawable(null);
        // this.clearFocus();
        this.setCursorVisible(false);
        hideLeftIcon();
        switchInputMethod(false);
        isEditState = false;
        this.setGravity(Gravity.LEFT);
        //
        this.setTextSize(DensityUtils.dp2px(getContext(), DEFAULT_TEXT_SIZE_DP));
    }

    private static final int DEFAULT_TEXT_SIZE_DP = 20;

    @Override
    public void setTextSize(float size) {
        super.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    private void displayLeftIcon() {
        this.setCompoundDrawables(null, null, leftIcon, null);
    }

    private void hideLeftIcon() {
        this.setCompoundDrawables(null, null, null, null);

    }

    private int currentX;
    private int currentY;

    private int downLpx;
    private int downLpy;

    private boolean isChanged = false;

    private CmdMoveEdit cmdMove;
    List<MoveData> sequence;

    private float lockDownx;

    private float lockDowny;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (isEditState)
            return super.onTouchEvent(event);
        DrawModeCode modeCode = DrawModeManager.getIns().getModeCode();
        if (modeCode == DrawModeCode.WORD || modeCode == DrawModeCode.PATH || modeCode == DrawModeCode.ERASER) {
            return false;// handle to father.
        }

        /**
         * mode choice.
         */
        if (isLocked) {

            if (event.getAction() == MotionEvent.ACTION_UP) {

                if (Math.abs(event.getRawX() - lockDownx) < 10 && Math.abs(event.getRawY() - lockDowny) < 10) {
                    whenClick();
                }
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                lockDownx = event.getRawX();
                lockDowny = event.getRawY();
            }
            return true;
        }

        // go move below.

        int tmpX = (int) event.getRawX();
        int tmpY = (int) event.getRawY();
        int dx = tmpX - currentX;
        int dy = tmpY - currentY;
        LayoutParams lp = (LayoutParams) this.getLayoutParams();
        int oldX = lp.x;
        int oldY = lp.y;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                isChanged = false;
                currentX = tmpX;
                currentY = tmpY;

                downLpx = oldX;
                downLpy = oldY;
                setPosition(lp.x, lp.y, false);
                //
                cmdMove = new CmdMoveEdit();
                cmdMove.setTime(draw.getPageRecorder().recordTimeNow());
                saveOldData();
                cmdMove.setOlddata(olddata);
                sequence = Lists.newArrayList();
                sequence.add(new MoveData(draw.getPageRecorder().recordTimeNow() - cmdMove.getTime(), getZZpivotX(lp.x),
                        getZZpivotY(lp.y)));

            }
            break;
            case MotionEvent.ACTION_MOVE: {
                if ((Math.abs(dx) > 10 || Math.abs(dy) > 10)) {

                    lp.x = (int) oldX + dx;
                    lp.y = (int) oldY + dy;

                    currentX = tmpX;
                    currentY = tmpY;
                    setPosition(lp.x, lp.y, false);
                    isChanged = true;
                    sequence.add(new MoveData(draw.getPageRecorder().recordTimeNow() - cmdMove.getTime(),
                            getZZpivotX(lp.x), getZZpivotY(lp.y)));
                }
            }
            break;
            case MotionEvent.ACTION_UP:
                if (isChanged) {
                    setPosition(lp.x, lp.y, false);
                    sequence.add(new MoveData(draw.getPageRecorder().recordTimeNow() - cmdMove.getTime(),
                            getZZpivotX(lp.x), getZZpivotY(lp.y)));
                    ChangeEditData<MoveData> data = new ChangeEditData<MoveData>();
                    copyAttrsToData(data, this);
                    data.setSequence(sequence);// memory problem.!!
                    cmdMove.setData(data);
                    cmdMove.setEndTime(draw.getPageRecorder().recordTimeNow());
                    //
                    mDrawBoard.sendCommand(cmdMove, false);
                } else {// no move==click.
                    whenClick();
                }
                break;
        }
        return true;
    }

    private void whenClick() {
        if (mDrawBoard.getFocusedView() == this) {
            mDrawBoard.getFocusedView().exitFocus();
            EventBus.getDefault().post(new CloseEditPopupWindowEvent());
            return;
        }
        if (mDrawBoard.getFocusedView() != null) {
            mDrawBoard.getFocusedView().exitFocus();
            EventBus.getDefault().post(new CloseEditPopupWindowEvent());
        }
        this.intoFocus();
        showEditWindow(this);
    }

    public static void copyAttrsToData(EditCommonData dest, WordEdit edit) {
        dest.setAlpha(edit.getAlpha());

        dest.setRotation((int) edit.getRotation());
        dest.setText(edit.getText().toString());
        dest.setShapeID(edit.getShapeID());
        // GLogger.e("lich", "edit.getShapeId():" + edit.getId());
        String colorhex = "#" + Integer.toHexString(edit.getCurrentTextColor()).substring(2);
        dest.setColor(colorhex);

        /**
         * note.
         */
        PositionData pos = edit.getPos();
        pos.setX(edit.getZZpivotX(pos.getX()));
        pos.setY(edit.getZZpivotY(pos.getY()));
        dest.setPosition(pos);
        dest.setScale(edit.getScaleX());

        dest.setFontSize((int) edit.getTextSize());
    }

    public static void inflateDataToAttrs(EditCommonData srcData, WordEdit edit) {
        edit.setAlpha(srcData.getAlpha());
        edit.setRotation(srcData.getRotation());
        edit.setText(srcData.getText());
        edit.setTextColor(Color.parseColor(srcData.getColor()));

        float scl = srcData.getScale();
        edit.setScaleX(scl);
        edit.setScaleY(scl);
        edit.setTextSize(srcData.getFontSize());
        // must called after scale.
        edit.setPosition(srcData.getPosition().getX(), srcData.getPosition().getY(), true);

    }

    public static void copyAttrs(WordEdit src, WordEdit edit) {
        edit.setAlpha(src.getAlpha());
        edit.setRotation(src.getRotation());
        edit.setText(src.getText());
        edit.setTextColor(src.getCurrentTextColor());
        PositionData srcP = src.getPos();// ??
        int offset = (int) (src.getHeight() * src.getScaleX());
        srcP.setY(srcP.getY() + offset);
        edit.setPosition(srcP.getX(), srcP.getY(), false);

        edit.setScaleX(src.getScaleX());
        edit.setScaleY(src.getScaleY());
        edit.setTextSize(src.getTextSize());
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            outEdit();
        }
        return false;
    }

    public void switchInputMethod(boolean show) {
        final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (show) {
            imm.showSoftInput(this, InputMethodManager.SHOW_FORCED);
        } else
            imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
    }

    public final void showEditWindow(final EditWidget edit) {
        draw.showEditClickPopup(edit);
    }

    private boolean isEditState = false;
    private boolean isCreate = false;

    @Override
    public void intoFocus() {
        mDrawBoard.setFocusedView(this);
        isInFocus = true;
        invalidate();
    }

    @Override
    public void exitFocus() {// note.
        if (isInFocus) {
            mDrawBoard.setFocusedView(null);
            isInFocus = false;
            invalidate();
        } else if (isEditState) {
            outEdit();
        }
    }

    @Override
    public void delete() {
        saveOldData();
        CmdDeleteEdit cmd = new CmdDeleteEdit();
        cmd.setTime(draw.getPageRecorder().recordTimeNow());
        DeleteShapeData data = new DeleteShapeData();
        data.setShapeID(getShapeID());
        cmd.setData(data);
        //
        cmd.setOlddata(olddata);
        //
        mDrawBoard.deleteShape(getShapeID());
        mDrawBoard.sendCommand(cmd, false);

    }

    @Override
    public void copy() {
        WordEdit edit = new WordEdit(getContext(), draw, mDrawBoard.makeShapeId());
        copyAttrs(this, edit);

        edit.setSelection(edit.getText().length());

        mDrawBoard.draw(edit);
        mDrawBoard.saveShape(edit);
        edit.intoFocus();
        showEditWindow(edit);
        // send cmd.

        CmdCreateEdit cmd = new CmdCreateEdit();
        EditCommonData data = new EditCommonData();
        cmd.setTime(draw.getPageRecorder().recordTimeNow());
        copyAttrsToData(data, edit);
        cmd.setData(data);

        mDrawBoard.sendCommand(cmd, false);
    }

    private EditCommonData olddata;

    private void saveOldData() {
        olddata = new EditCommonData();
        copyAttrsToData(olddata, this);
    }

    @Override
    public void intoEdit(boolean isCreate) {
        exitFocus();
        //
        saveOldData();
        this.isCreate = isCreate;

        this.setCursorVisible(true);
        this.requestFocus();
        displayLeftIcon();

        switchInputMethod(true);
        isEditState = true;
        mDrawBoard.setFocusedView(this);
        invalidate();
    }

    private final Paint borderPaint = new Paint();
    private final static float BORDER_PAINT_WIDTH = 3f;

    private void initBorderPaint() {
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeJoin(Paint.Join.ROUND);
        borderPaint.setStrokeCap(Paint.Cap.ROUND);
        borderPaint.setStrokeWidth(BORDER_PAINT_WIDTH);
        borderPaint.setFlags(Paint.DITHER_FLAG);
        borderPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setAntiAlias(true);
        borderPaint.setDither(true);
        borderPaint.setColor(Color.GREEN);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        if (isInFocus) {
            float scaledWidth = BORDER_PAINT_WIDTH / this.getScaleX();
            borderPaint.setColor(Color.GREEN);
            borderPaint.setStrokeWidth(scaledWidth);
            canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), borderPaint);
        } else if (isEditState) {

            float scaledWidth = BORDER_PAINT_WIDTH / this.getScaleX();
            borderPaint.setColor(Color.GRAY);
            borderPaint.setStrokeWidth(scaledWidth);
            canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), borderPaint);
        }
    }

    @Override
    public void scaleSmall() {
        saveOldData();

        float old = this.getTextSize();
        this.setTextSize(old * 3 / 4f);

        CmdChangeEditNoSeq cmd = new CmdChangeEditNoSeq();
        cmd.setTime(draw.getPageRecorder().recordTimeNow());
        EditCommonData data = new EditCommonData();
        copyAttrsToData(data, this);
        cmd.setData(data);
        cmd.setOlddata(olddata);
        // data.set
        mDrawBoard.sendCommand(cmd, false);
    }

    @Override
    public void changeColor(int newColor) {
        saveOldData();
        this.setTextColor(newColor);
        //
        CmdChangeEditNoSeq cmd = new CmdChangeEditNoSeq();
        cmd.setTime(draw.getPageRecorder().recordTimeNow());
        EditCommonData data = new EditCommonData();
        copyAttrsToData(data, this);
        cmd.setData(data);
        cmd.setOlddata(olddata);
        // data.set
        mDrawBoard.sendCommand(cmd, false);
    }

    @Override
    public void scaleBig() {
        saveOldData();
        float old = this.getTextSize();
        this.setTextSize(old * 4 / 3f);
        // mEditTransformer.scaleBy(4 / 3f, 4 / 3f, this);
        //
        CmdChangeEditNoSeq cmd = new CmdChangeEditNoSeq();
        cmd.setTime(draw.getPageRecorder().recordTimeNow());
        EditCommonData data = new EditCommonData();
        copyAttrsToData(data, this);
        cmd.setData(data);
        cmd.setOlddata(olddata);
        // data.set
        mDrawBoard.sendCommand(cmd, false);
    }

    @Override
    public Page getPage() {
        return mDrawBoard;
    }

    @Override
    public void outEdit() {
        this.setBackgroundDrawable(null);

        this.setCursorVisible(false);
        hideLeftIcon();
        switchInputMethod(false);
        isEditState = false;
        //
        if (this.getText().toString().length() == 0) {

            if (!isCreate) {
                // todo send cmd del.
                delete();
            } else {
                // mDrawBoard.unDraw(this);
                mDrawBoard.deleteShape(getShapeID());
            }
        } else {// not 0.
            if (isCreate) {
                CmdCreateEdit cmd = new CmdCreateEdit();
                EditCommonData data = new EditCommonData();
                cmd.setTime(draw.getPageRecorder().recordTimeNow());
                copyAttrsToData(data, this);
                cmd.setData(data);

                mDrawBoard.sendCommand(cmd, false);
            } else if (!this.getText().toString().equals(olddata.getText())) {
                // transform.

                CmdChangeEditNoSeq cmd = new CmdChangeEditNoSeq();
                cmd.setTime(draw.getPageRecorder().recordTimeNow());
                EditCommonData data = new EditCommonData();
                copyAttrsToData(data, this);
                cmd.setData(data);
                cmd.setOlddata(olddata);
                // data.set
                mDrawBoard.sendCommand(cmd, false);
            }
        }
        mDrawBoard.setFocusedView(null);
        invalidate();
    }

    @Override
    public void switchLock() {
        isLocked = !isLocked;
    }

    @Override
    public View view() {
        return this;
    }

    @Override
    public void refresh() {
        invalidate();
    }

    @Override
    public boolean isInFocus() {
        return isInFocus;
    }

    @Override
    public void setIsInFocus(boolean isfocus) {
        isInFocus = isfocus;
    }

    @Override
    public boolean isInEdit() {
        return isEditState;
    }

    @Override
    public ViewShape_ copySelf() {
        WordEdit dest = new WordEdit(getContext(), draw, id);
        // use same id,because copy cmds.
        dest.setAlpha(this.getAlpha());
        dest.setRotation(this.getRotation());
        dest.setText(this.getText());
        dest.setTextColor(this.getCurrentTextColor());
        PositionData srcP = this.getPos();// ??
        int offset = 0;// (int) (this.getHeight() * this.getScaleX());
        srcP.setY(srcP.getY() + offset);
        dest.setPosition(srcP.getX(), srcP.getY(), false);

        dest.setScaleX(this.getScaleX());
        dest.setScaleY(this.getScaleY());
        dest.setTextSize(this.getTextSize());
        return dest;
    }

    @Override
    public int getShapeID() {
        return id;
    }

    @Override
    public void drawToPage(Page page) {
        page.draw(this);

    }

    @Override
    public void deleteFromPage(Page page) {
        page.unDraw(this);
    }

    private int getTextWidth() {
        String text = getText().toString();
        if (TextUtils.isEmpty(text))
            return 0;
        Paint p = new Paint();
        p.setTextSize(this.getTextSize());
        String[] lines = text.split("\n");
        float maxW = 0;
        for (String line : lines) {
            float w = p.measureText(line);
            if (w > maxW) {
                maxW = w;
            }
        }
        return (int) maxW;
    }

    private int getTextHeight() {
        String text = getText().toString();
        if (TextUtils.isEmpty(text))
            return 0;
        Paint p = new Paint();
        p.setTextSize(this.getTextSize());
        String[] lines = text.split("\n");
        int h = lines.length * getFontHeight(p);

        return h;
    }

    private static int getFontHeight(Paint paint) {

        FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.top) + 2;
    }

    private static final boolean CONVERT_PIVOT = false;

    private int getCenterPivotX(float x) {
        if (!CONVERT_PIVOT)
            return (int) x;

        float dw = (this.getTextWidth() * this.getScaleX() - this.getTextWidth()) / 2;

        return (int) (x + dw);
    }

    private int getCenterPivotY(float y) {
        if (!CONVERT_PIVOT)
            return (int) y;
        float dh = (getTextHeight() * getScaleY() - getTextHeight()) / 2;
        return (int) (y + dh);

    }

    private int getZZpivotX(float x) {
        if (!CONVERT_PIVOT)
            return (int) x;
        float dw = (this.getTextWidth() * this.getScaleX() - this.getTextWidth()) / 2;
        return (int) (x - dw);

    }

    private int getZZpivotY(float y) {
        if (!CONVERT_PIVOT)
            return (int) y;
        float dh = (getTextHeight() * getScaleY() - getTextHeight()) / 2;
        return (int) (y - dh);

    }

    @Override
    public void setPosition(int x, int y, boolean isNeedConvertPivot) {
        if (isNeedConvertPivot) {
            x = getCenterPivotX(x);
            y = getCenterPivotY(y);
        }
        LayoutParams lp = (LayoutParams) this.getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT, x, y);
        } else {
            lp.x = x;
            lp.y = y;
        }
        this.setLayoutParams(lp);
    }
}
