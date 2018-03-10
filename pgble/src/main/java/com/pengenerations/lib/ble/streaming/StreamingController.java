package com.pengenerations.lib.ble.streaming;

import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import com.pengenerations.lib.ble.streaming.PGBLEManager.OperatingMode;

/**
 * 流控制器
 * 
 * @author shaoshuai
 * 
 */
public class StreamingController {

	final String TAG = "StreamingController";

	boolean m_penStatus = false;
	boolean m_firstFlag = false;

	float m_scaleX;
	float m_scaleY;

	float m_x, m_y;
	int m_force;
	String m_page;

	int m_event = -1;
	String m_pageaddress = "";
	String m_battery = "";
	String m_memory = "";

	long m_CoordinateCnt = 0;
	long m_DrawCoordinateCnt = 0;

	int lastLogX = 0;
	int lastLogY = 0;

	Paint m_paint = new Paint();
	Path m_path = new Path();

	// Context
	public StreamingController(int width, int height) {

		if (width > height) {
			m_scaleX = (float) ((float) (width) / 8200f);
			m_scaleY = (float) ((float) (height) / 5800f);
		} else {
			m_scaleX = (float) ((float) (width) / 5800f);
			m_scaleY = (float) ((float) (height) / 8200f);
		}

		m_paint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
		m_paint.setAntiAlias(true);
		m_paint.setStyle(Paint.Style.STROKE);
		m_paint.setStrokeJoin(Paint.Join.ROUND);
		m_paint.setStrokeCap(Paint.Cap.ROUND);
		m_paint.setStrokeWidth(2);
		m_paint.setPathEffect(new CornerPathEffect(10));
		m_paint.setDither(true);
		m_paint.setLinearText(true);
		m_paint.setSubpixelText(true);
		m_paint.setColor(Color.BLACK);
		m_paint.setFilterBitmap(true);
	}

	public void clear() {
		m_path.reset();
		m_DrawCoordinateCnt = 0;
	}

	public void PenEvent(int event) {
		m_event = event;
	}

	// 笔抬起
	public void PenUp() {
		m_penStatus = false;
		m_firstFlag = false;

		m_path.moveTo((m_x * m_scaleX), (m_y * m_scaleY));
	}

	public boolean GetPenStatus() {
		return m_penStatus;
	}

	// 笔按下
	public void PenDown() {
		if (m_penStatus == true) {
			m_firstFlag = false;
		}

		m_penStatus = true;
	}

	String m_PenSerial = "";

	// 设置笔ID
	public void SetPenID(String penId) {
		m_PenSerial = penId;
	}

	public void SetPageAddress(String page) {
		m_pageaddress = page;
	}

	String m_PageInfo = "";

	public void SetPageInfo(int shelf, int book, int page) {
		m_PageInfo = "Shelf : " + shelf + ", Book : " + book + ", Page : " + page;
	}

	public void SetBattery(String battery) {
		m_battery = battery;
	}

	public void SetMemory(String memory) {
		m_memory = memory;
	}

	String m_AllSound = "Off";
	String m_SleepSound = "Off";

	public void SetSoundStatus(byte allsnd, byte sleepsnd) {
		if (allsnd == 0x00)
			m_AllSound = "Off";
		else
			m_AllSound = "On";
		if (sleepsnd == 0x00)
			m_SleepSound = "Off";
		else
			m_SleepSound = "On";
	}

	OperatingMode m_PenMode = OperatingMode.NONE;

	public void SetPenMode(OperatingMode mode) {
		m_PenMode = mode;
	}

	String m_Vid;
	String m_Pid;

	public void SetVidPid(String vid, String pid) {
		m_Vid = vid;
		m_Pid = pid;
	}

	String m_swVer;

	public void SetSWVer(String swVer) {
		m_swVer = swVer;
	}

	public Paint GetPaint() {
		return m_paint;
	}

	public Path GetPath() {
		return m_path;
	}

	String m_PenInfo = null;

	public void SetPenInfo(String penInfo) {
		m_PenInfo = penInfo;
	}

	public void ClearCoordinateInfo() {
		m_CoordinateCnt = 0;
	}

	public void addCoordinate(int x, int y, int force, long ulPage) {
		// 笔按下
		if (m_penStatus == true && m_firstFlag == false) {
			m_firstFlag = true;
			m_path.moveTo((x * m_scaleX), (y * m_scaleY));

			m_x = x;
			m_y = y;
			m_force = force;

			Log.e("COOORD", "In] X = " + (x * m_scaleX) + "   Y = " + (y * m_scaleY) + " ] X = " + x + "   Y =" + y);
		} else {
			if (m_penStatus == false)
				return;
			// 笔抬起
			m_path.lineTo((x * m_scaleX), (y * m_scaleY));

			m_x = x;
			m_y = y;
			m_force = force;

			Log.e("COOORD", "Out] X = " + (x * m_scaleX) + "   Y = " + (y * m_scaleY) + " ] X = " + x + "   Y =" + y);
		}

		lastLogX = (int) (x * m_scaleX);
		lastLogY = (int) (y * m_scaleY);

		m_CoordinateCnt++;
	}

	public String GetStreamingInfo() {
		String m_str;

		m_str = "Pen ID : " + m_PenSerial + ", Pen S/W Version : " + m_swVer + "\n";
		m_str += "Pen Mode : " + m_PenMode.toString() + ", Vid : " + m_Vid + ", Pid : " + m_Pid + "\n";

		m_str += "Page Info : " + m_PageInfo + "\n";

		m_str += "Coordinate X : " + m_x + ", Y : " + m_y + ", Force : " + m_force + "\n";

		if (m_penStatus == true) {
			m_str += "Pen Status : Pen Down" + "\n";
		} else {
			m_str += "Pen Status : Pen Up" + "\n";
		}

		switch (m_event) {
		case 0:
			m_str += "EVENT : STATUS_NO_POSTION_DECODE_FAILED" + "\n";
			break;
		case 1:
			m_str += "EVENT : STATUS_NO_POSTION_LOCKED_SEGMENT" + "\n";
			break;
		case 2:
			m_str += "EVENT : STATUS_NO_POSTION_NON_ANOTO_PAPER" + "\n";
			break;
		case 3:
			m_str += "EVENT : STATUS_NO_POSTION_FRAME_SKIPPED" + "\n";
			break;
		case 4:
			m_str += "EVENT : STATUS_NO_POSTION_CAMERA_RESTARTED" + "\n";
			break;
		default:
			break;
		}
		m_event = -1;
		m_str += "Remained Battery : " + m_battery + ", Memory Fill Level : " + m_memory + "\n";
		m_str += "All Sound : " + m_AllSound + ", Sleep Sound : " + m_SleepSound;
		return m_str;
	}

	public String GetPageAddressInfo() {
		String m_str;

		m_str = "Page Address : " + m_pageaddress;

		return m_str;
	}

}
