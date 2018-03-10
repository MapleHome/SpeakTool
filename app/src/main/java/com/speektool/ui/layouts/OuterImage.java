package com.speektool.ui.layouts;

import java.io.File;
import java.util.ArrayList;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsoluteLayout;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.speektool.api.Draw;
import com.speektool.api.FocusedView;
import com.speektool.api.Page;
import com.speektool.api.ViewTransformer;
import com.speektool.bean.ChangeImageData;
import com.speektool.bean.DeleteShapeData;
import com.speektool.bean.ImageCommonData;
import com.speektool.bean.MoveData;
import com.speektool.bean.PositionData;
import com.speektool.bean.ScaleData;
import com.speektool.busevents.CloseEditPopupWindowEvent;
import com.speektool.impl.DefViewTransformer;
import com.speektool.impl.cmd.create.CmdCreateImage;
import com.speektool.impl.cmd.delete.CmdDeleteImage;
import com.speektool.impl.cmd.transform.CmdChangeImageNoSeq;
import com.speektool.impl.cmd.transform.CmdMoveImage;
import com.speektool.impl.cmd.transform.CmdScaleImage;
import com.speektool.impl.modes.DrawModeCode;
import com.speektool.impl.shapes.ImageWidget;
import com.speektool.impl.shapes.ViewShape_;
import com.speektool.manager.DrawModeManager;
import com.speektool.utils.BitmapScaleUtil;
import com.speektool.utils.ScreenFitUtil;

import de.greenrobot.event.EventBus;

@SuppressWarnings("deprecation")
public class OuterImage extends GifImageView implements ImageWidget, FocusedView, OnClickListener {

	private final Paint borderPaint = new Paint();
	private final static float BORDER_PAINT_WIDTH = 3f;
	private final ViewTransformer mImageDecorator = new DefViewTransformer();
	private Draw draw;
	private Page mPage;
	private int id;

	@Override
	protected void onAttachedToWindow() {
		Drawable old = this.getDrawable();
		if (old == null) {
			setResourceID(resourceID);

		}
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		Drawable old = this.getDrawable();

		if (old != null) {
			this.setImageDrawable(null);

			if (old instanceof GifDrawable) {
				GifDrawable o = (GifDrawable) old;
				o.stop();
				o.recycle();

			} else if (old instanceof BitmapDrawable) {
				BitmapDrawable o = (BitmapDrawable) old;
				Bitmap bmp = o.getBitmap();
				if (bmp != null)
					bmp.recycle();
			}

		}

		super.onDetachedFromWindow();
	}

	public OuterImage(Context context, Draw draw, int id) {
		super(context);
		initBorderPaint();
		this.id = id;
		this.draw = draw;
		mPage = draw.getCurrentBoard();
		this.setScaleType(ScaleType.FIT_XY);
		this.setOnClickListener(this);
		//
		LayoutParams lp = (LayoutParams) this.getLayoutParams();
		if (lp == null) {
			lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, 0, 0);
		} else {
			lp.width = LayoutParams.WRAP_CONTENT;
			lp.height = LayoutParams.WRAP_CONTENT;
		}
		this.setLayoutParams(lp);

	}

	private PositionData getPos() {
		LayoutParams lp = (LayoutParams) this.getLayoutParams();
		if (lp == null)
			return null;
		return new PositionData(lp.x, lp.y);
	}

	private static final boolean CONVERT_PIVOT = false;

	private int getCenterPivotX(float x) {
		if (!CONVERT_PIVOT)
			return (int) x;
		float dw = (this.getBmpWidth() * this.getScaleX() - this.getBmpWidth()) / 2;
		return (int) (x + dw);
	}

	private int getCenterPivotY(float y) {
		if (!CONVERT_PIVOT)
			return (int) y;
		float dh = (getBmpHeight() * getScaleY() - getBmpHeight()) / 2;
		return (int) (y + dh);
	}

	private int getZZpivotX(float x) {
		if (!CONVERT_PIVOT)
			return (int) x;
		float dw = (this.getBmpWidth() * this.getScaleX() - this.getBmpWidth()) / 2;
		return (int) (x - dw);
	}

	private int getZZpivotY(float y) {
		if (!CONVERT_PIVOT)
			return (int) y;
		float dh = (getBmpHeight() * getScaleY() - getBmpHeight()) / 2;
		return (int) (y - dh);
	}

	private String resourceID;
	private int bpW;
	private int bpH;

	private int getBmpWidth() {
		return bpW;
	}

	private int getBmpHeight() {
		return bpH;
	}

	@Override
	public void setResourceID(String resourceID) {
		this.resourceID = resourceID;
		String imagpath = String.format("%s%s%s", draw.getRecordDir(), File.separator, resourceID);

		if (BitmapScaleUtil.isGif(resourceID)) {// gif.
			GifDrawable gifd;
			try {
				gifd = new GifDrawable(imagpath);
				Bitmap srcbmp = gifd.getCurrentFrame();
				bpW = srcbmp.getWidth();
				bpH = srcbmp.getHeight();
				setImageDrawable(gifd);
			} catch (Error e) {
				e.printStackTrace();
				Toast.makeText(getContext(), "图片添加失败！", 0).show();
				// SimpleAlertTool.simpleToast(getResources().getString(
				// R.string.addImage_fail));
				return;
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getContext(), "图片添加失败！", 0).show();
				// SimpleAlertTool.simpleToast(getResources().getString(
				// R.string.addImage_fail));
				return;
			}

		} else {// bitmap.
			try {
				Bitmap bm = BitmapFactory.decodeFile(imagpath);// already scaled
																// to dir.
				bpW = bm.getWidth();
				bpH = bm.getHeight();
				setImageBitmap(bm);
			} catch (Error e) {
				e.printStackTrace();
				Toast.makeText(getContext(), "图片添加失败！", 0).show();
				// SimpleAlertTool.simpleToast(getResources().getString(
				// R.string.addImage_fail));
				return;
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getContext(), "图片添加失败！", 0).show();
				// SimpleAlertTool.simpleToast(getResources().getString(
				// R.string.addImage_fail));
				return;
			}
		}
		/***
		 * screen fit below.
		 */
		float ratioHW = (float) bpH / (float) bpW;
		int neww = (int) (bpW * ScreenFitUtil.getFactorX());
		int newh = (int) (bpH * ScreenFitUtil.getFactorY());
		Point size = ScreenFitUtil.getKeepRatioScaledSize(ratioHW, neww, newh);
		neww = size.x;
		newh = size.y;
		//
		bpW = neww;
		bpH = newh;
		LayoutParams lp = (LayoutParams) this.getLayoutParams();
		if (lp == null)
			lp = new LayoutParams(neww, newh, (int) this.getX(), (int) this.getY());
		else {
			lp.width = neww;
			lp.height = newh;
		}
		this.setLayoutParams(lp);
	}

	/**
	 * data to img.
	 * 
	 * @param src
	 * @param img
	 */
	public static void inflateDataToAttrs(ImageCommonData srcData, OuterImage img) {
		float alp = srcData.getAlpha();
		img.setAlpha(alp);
		img.setRotation(srcData.getRotation());
		String resName = srcData.getResourceID();
		img.setResourceID(resName);
		float scl = srcData.getScale();
		img.setScaleX(scl);
		img.setScaleY(scl);
		// must called after scale.
		img.setPosition(srcData.getPosition().getX(), srcData.getPosition().getY(), true);

	}

	/**
	 * image to data.
	 * 
	 * @param data
	 * @param img
	 */
	public static void copyAttrsToData(ImageCommonData data, OuterImage img) {
		data.setAlpha(img.getAlpha());
		data.setRotation((int) img.getRotation());
		data.setShapeID(img.getShapeID());
		// note.
		PositionData pos = img.getPos();
		pos.setX(img.getZZpivotX(pos.getX()));
		pos.setY(img.getZZpivotY(pos.getY()));
		data.setPosition(pos);
		data.setScale(img.getScaleX());
		data.setResourceID(img.getResourceID());

	}

	public static void copyAttrs(OuterImage src, OuterImage dest) {
		dest.setAlpha(src.getAlpha());
		dest.setRotation(src.getRotation());

		PositionData srcP = src.getPos();// ??
		int offset = 10;// (int) (src.getHeight()*src.getScaleX());
		srcP.setY(srcP.getY() + offset);
		dest.setPosition(srcP.getX(), srcP.getY(), false);
		dest.setScaleX(src.getScaleX());
		dest.setScaleY(src.getScaleY());
		dest.setResourceID(src.getResourceID());

	}

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

	private long downTime;

	private int currentRawX;
	private int currentRawY;

	private final int DRAG = 1;
	private final int ZOOM = 2;

	private int mode;

	private float oldDist;

	private static float getSpace(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		if (x < 0) {
			x = -x;
		}
		float y = event.getY(0) - event.getY(1);
		if (y < 0) {
			y = -y;
		}
		return FloatMath.sqrt(x * x + y * y);
	}

	private static final int DOUBLE_POINT_DISTANCE = 10;

	private float oldRotation = 0;

	private boolean isChanged = false;
	private CmdMoveImage cmdMove;
	private ArrayList<MoveData> sequence;
	private CmdScaleImage cmdScale;
	private ArrayList<ScaleData> sequenceScale;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		DrawModeCode modeCode = DrawModeManager.getIns().getModeCode();
		if (modeCode == DrawModeCode.WORD || modeCode == DrawModeCode.PATH || modeCode == DrawModeCode.ERASER) {
			return false;// handle to father.
		}
		if (isLocked)
			return super.onTouchEvent(event);
		int tmpRawX = (int) event.getRawX();
		int tmpRawY = (int) event.getRawY();
		int dx = tmpRawX - currentRawX;
		int dy = tmpRawY - currentRawY;
		LayoutParams lp = (LayoutParams) this.getLayoutParams();
		int oldX = lp.x;
		int oldY = lp.y;
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			isChanged = false;
			mode = DRAG;

			currentRawX = tmpRawX;
			currentRawY = tmpRawY;

			downTime = System.currentTimeMillis();
			//
			setPosition(lp.x, lp.y, false);
			//
			cmdMove = new CmdMoveImage();
			cmdMove.setTime(draw.getPageRecorder().recordTimeNow());
			saveOldData();
			cmdMove.setOlddata(olddata);
			sequence = Lists.newArrayList();
			sequence.add(new MoveData(draw.getPageRecorder().recordTimeNow() - cmdMove.getTime(), getZZpivotX(lp.x),
					getZZpivotY(lp.y)));
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			isChanged = false;
			currentRawX = tmpRawX;
			currentRawY = tmpRawY;
			oldDist = getSpace(event);
			oldRotation = getRotation(event);
			//
			mode = ZOOM;
			//
			cmdScale = new CmdScaleImage();
			cmdScale.setTime(draw.getPageRecorder().recordTimeNow());
			saveOldData();
			cmdScale.setOlddata(olddata);
			sequenceScale = Lists.newArrayList();
			sequenceScale.add(new ScaleData(draw.getPageRecorder().recordTimeNow() - cmdScale.getTime(), this
					.getScaleX(), (int) this.getRotation(), getZZpivotX(lp.x), getZZpivotY(lp.y)));
			// 2,rotation.
			break;
		case MotionEvent.ACTION_MOVE:

			if (mode == ZOOM) {// double hand.
				float rotation = getRotation(event) - oldRotation;
				float newDist = getSpace(event);
				float scale = newDist / oldDist;
				if (scale > 1.1 || scale < 0.9) {
					mImageDecorator.scaleBy(scale, scale, this);
					isChanged = true;
				}
				if (Math.abs(rotation) > 1) {
					mImageDecorator.rotateBy(rotation, this);
					isChanged = true;
				}
				//
				if (Math.abs(dx) > 10 || Math.abs(dy) > 10) {// double move.
					lp.x = (int) oldX + dx;
					lp.y = (int) oldY + dy;

					currentRawX = tmpRawX;
					currentRawY = tmpRawY;

					isChanged = true;
					setPosition(lp.x, lp.y, false);
				}
				if (isChanged) {
					sequenceScale.add(new ScaleData(draw.getPageRecorder().recordTimeNow() - cmdScale.getTime(), this
							.getScaleX(), (int) this.getRotation(), getZZpivotX(lp.x), getZZpivotY(lp.y)));
				}
				//

			} else if (mode == DRAG && (Math.abs(dx) > 10 || Math.abs(dy) > 10)) {// single
																					// hand.
				lp.x = (int) oldX + dx;
				lp.y = (int) oldY + dy;

				currentRawX = tmpRawX;
				currentRawY = tmpRawY;

				isChanged = true;
				//
				setPosition(lp.x, lp.y, false);
				//
				sequence.add(new MoveData(draw.getPageRecorder().recordTimeNow() - cmdMove.getTime(),
						getZZpivotX(lp.x), getZZpivotY(lp.y)));

			}
			break;
		case MotionEvent.ACTION_UP:
			mode = 0;
			if (isChanged) {
				setPosition(lp.x, lp.y, false);
				sequence.add(new MoveData(draw.getPageRecorder().recordTimeNow() - cmdMove.getTime(),
						getZZpivotX(lp.x), getZZpivotY(lp.y)));
				ChangeImageData<MoveData> data = new ChangeImageData<MoveData>();
				copyAttrsToData(data, this);
				data.setSequence(sequence);// memory problem.!!
				cmdMove.setData(data);
				cmdMove.setEndTime(draw.getPageRecorder().recordTimeNow());
				//
				mPage.sendCommand(cmdMove, false);
			} else {// no move==click.
				whenClick();
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mode = 0;
			// 1,scale.
			if (isChanged) {
				sequenceScale.add(new ScaleData(draw.getPageRecorder().recordTimeNow() - cmdScale.getTime(), this
						.getScaleX(), (int) this.getRotation(), getZZpivotX(lp.x), getZZpivotY(lp.y)));
				ChangeImageData<ScaleData> data = new ChangeImageData<ScaleData>();
				copyAttrsToData(data, this);
				data.setSequence(sequenceScale);// memory problem.!!
				cmdScale.setData(data);
				cmdScale.setEndTime(draw.getPageRecorder().recordTimeNow());
				//
				mPage.sendCommand(cmdScale, false);
				// 2,rotation.
			}
			break;
		}
		return true;
	}

	private void whenClick() {
		if (mPage.getFocusedView() == this) {
			mPage.getFocusedView().exitFocus();
			EventBus.getDefault().post(new CloseEditPopupWindowEvent());
			return;
		}
		if (mPage.getFocusedView() != null) {
			mPage.getFocusedView().exitFocus();
			EventBus.getDefault().post(new CloseEditPopupWindowEvent());
		}
		this.intoFocus();
		showEditWindow(this);
	}

	private float getRotation(MotionEvent event) {
		double delta_x = (event.getX(0) - event.getX(1));
		double delta_y = (event.getY(0) - event.getY(1));
		double radians = Math.atan2(delta_y, delta_x);
		return (float) Math.toDegrees(radians);
	}

	private boolean isLocked = false;

	/**
	 * must call after scale.
	 */
	private void locateToCenter(float scaleFactor) {
		float pW = ((View) this.getParent()).getWidth();
		float cW = this.getWidth();

		float pH = ((View) this.getParent()).getHeight();
		float cH = this.getHeight();
		int x = (int) ((pW - cW) / 2);
		int y = (int) ((pH - cH) / 2);// 103

		setPosition(x, y, false);

	}

	private float getAutoFitScaleX() {
		float pW = ((View) this.getParent()).getWidth();
		float cW = this.getWidth();
		float factorX = pW / cW;
		return factorX;

	}

	private float getAutoFitScaleY() {
		float pH = ((View) this.getParent()).getHeight();
		float cH = this.getHeight();
		float factorY = pH / cH;
		return factorY;
	}

	@Override
	public void widthAutoFit() {
		saveOldData();
		//
		float factorX = getAutoFitScaleX();

		this.setScaleX(factorX);
		this.setScaleY(factorX);
		this.setRotation(0);
		locateToCenter(factorX);
		//
		CmdChangeImageNoSeq cmd = new CmdChangeImageNoSeq();
		cmd.setTime(draw.getPageRecorder().recordTimeNow());
		ImageCommonData data = new ImageCommonData();
		copyAttrsToData(data, this);
		cmd.setData(data);
		cmd.setOlddata(olddata);
		// data.set
		mPage.sendCommand(cmd, false);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (isFocus) {
			float scaledWidth = BORDER_PAINT_WIDTH / this.getScaleX();
			borderPaint.setStrokeWidth(scaledWidth);
			canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), borderPaint);
		}
	}

	@Override
	public void heightAutoFit() {
		saveOldData();
		//
		float factorY = getAutoFitScaleY();
		this.setScaleX(factorY);
		this.setScaleY(factorY);
		this.setRotation(0);
		locateToCenter(factorY);
		//
		CmdChangeImageNoSeq cmd = new CmdChangeImageNoSeq();
		cmd.setTime(draw.getPageRecorder().recordTimeNow());
		ImageCommonData data = new ImageCommonData();
		copyAttrsToData(data, this);
		cmd.setData(data);
		cmd.setOlddata(olddata);
		// data.set
		mPage.sendCommand(cmd, false);

	}

	private ImageCommonData olddata;

	private void saveOldData() {
		olddata = new ImageCommonData();
		copyAttrsToData(olddata, this);

	}

	@Override
	public void delete() {
		saveOldData();
		//
		CmdDeleteImage cmd = new CmdDeleteImage();
		cmd.setTime(draw.getPageRecorder().recordTimeNow());
		DeleteShapeData data = new DeleteShapeData();
		data.setShapeID(getShapeID());
		cmd.setData(data);
		//
		cmd.setOlddata(olddata);
		//
		mPage.deleteShape(getShapeID());
		mPage.sendCommand(cmd, false);
	}

	@Override
	public void copy() {
		OuterImage img = new OuterImage(getContext(), draw, mPage.makeShapeId());
		copyAttrs(this, img);

		mPage.draw(img);
		mPage.saveShape(img);
		if (mPage.getFocusedView() != null)
			mPage.getFocusedView().exitFocus();

		img.intoFoc();
		showEditWindow(img);
		// send cmd.

		CmdCreateImage cmd = new CmdCreateImage();
		ImageCommonData data = new ImageCommonData();
		cmd.setTime(draw.getPageRecorder().recordTimeNow());
		copyAttrsToData(data, img);
		cmd.setData(data);

		mPage.sendCommand(cmd, false);

	}

	private final void showEditWindow(final ImageWidget imageWidget) {
		draw.showImageClickPopup(imageWidget);
	}

	@Override
	public void rotate() {
		saveOldData();
		mImageDecorator.rotateBy(45, this);
		//
		CmdChangeImageNoSeq cmd = new CmdChangeImageNoSeq();
		cmd.setTime(draw.getPageRecorder().recordTimeNow());
		ImageCommonData data = new ImageCommonData();
		copyAttrsToData(data, this);
		cmd.setData(data);
		cmd.setOlddata(olddata);
		// data.set
		mPage.sendCommand(cmd, false);
	}

	@Override
	public void switchLock() {
		isLocked = !isLocked;
	}

	@Override
	public void intoFocus() {
		intoFoc();
	}

	@Override
	public void exitFocus() {
		outFoc();
	}

	@Override
	public Page getPage() {
		return mPage;
	}

	private boolean isFocus = false;

	private void intoFoc() {
		mPage.setFocusedView(this);
		isFocus = true;
		invalidate();
	}

	private void outFoc() {
		mPage.setFocusedView(null);
		isFocus = false;
		invalidate();
	}

	@Override
	public void onClick(View v) {
		whenClick();
	}

	@Override
	public View view() {
		return this;
	}

	@Override
	public boolean isInFocus() {
		return isFocus;
	}

	@Override
	public void setIsInFocus(boolean isfocus) {
		isFocus = isfocus;
	}

	@Override
	public String getResourceID() {
		return resourceID;
	}

	@Override
	public ViewShape_ copySelf() {
		OuterImage dest = new OuterImage(getContext(), draw, id);
		dest.setAlpha(this.getAlpha());
		dest.setRotation(this.getRotation());

		PositionData srcP = this.getPos();// ??
		int offset = 0;// (int) (src.getHeight()*src.getScaleX());
		srcP.setY(srcP.getY() + offset);
		dest.setPosition(srcP.getX(), srcP.getY(), false);
		dest.setScaleX(this.getScaleX());
		dest.setScaleY(this.getScaleY());

		dest.setResourceID(this.getResourceID());

		return dest;
	}

	@Override
	public int getShapeID() {
		return id;
	}

	@Override
	public void drawToPage(Page page) {
		// page.drawView(this);
		page.draw(this);
	}

	@Override
	public void deleteFromPage(Page page) {
		page.unDraw(this);
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

	@Override
	public void refresh() {
		invalidate();
	}
}
