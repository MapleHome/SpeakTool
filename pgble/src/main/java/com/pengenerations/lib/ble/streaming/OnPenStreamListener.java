package com.pengenerations.lib.ble.streaming;

/**
 * 点阵笔流监听--去监听从笔发出的事件
 * 
 * @author shaoshuai
 * 
 */
public abstract interface OnPenStreamListener {
	/** 解码失败 */
	public final static int STREAM_ERROR_DECODE_FAILED = 0x01;
	/**
	 * Error event of the used dot-pattern is not allowed
	 */
	public final static int STREAM_ERROR_LOCKED_SEGMENT = 0x02;
	/**
	 * Error event of the used dot-pattern is not Anoto pattern
	 */
	public final static int STREAM_ERROR_NON_ANOTO_PAPER = 0x03;
	/**
	 * 帧跳过 Error event of frame skipped
	 */
	public final static int STREAM_ERROR_FRAME_SKIPPED = 0x04;
	/**
	 * Error event of camera is restarted
	 */
	public final static int STREAM_ERROR_CAMERA_RESTARTED = 0x05;

	/**
	 * This callback is called when pen is disconnected. No reason code of
	 * disconnection is not existed.
	 * 
	 * @return
	 */
	public abstract int onDisconnected();

	/**
	 * This callback is called when ADP601's pen information is received from
	 * pen
	 * 
	 * @param nTimeStamp
	 *            time stamp for the first stream event
	 * @param nVid
	 *            Pen's vendor id
	 * @param nPid
	 *            Pen's product id
	 * @param penSerial
	 *            Pen's serial number
	 * @return 0
	 */
	public abstract int onNewSession(long nTimeStamp, int nVid, int nPid, long penSerial, int swVer);

	/**
	 * This callback is called when coordinate data is received from pen
	 * 
	 * @param nTimeStamp
	 *            time stamp for each coordinate is arrived
	 * @param ullPageAddress
	 *            page address
	 * @param nX
	 *            logical coordinate X
	 * @param nY
	 *            logical coordinate Y
	 * @param nForce
	 *            force value of each coordinate (max : 128 min:0)
	 * @return - 0
	 */
	public abstract int onCoord(long nTimeStamp, long ullPageAddress, short nX, short nY, byte nForce);

	/**
	 * This callback is call when pen down event happened Note : Not used.
	 * 
	 * @return 0
	 */
	public abstract int onPendown();

	/**
	 * This callback is call when pen up event happened
	 * 
	 * @return 0
	 */
	public abstract int onPenup();

	/**
	 * This callback is call when error events are received
	 * 
	 * @param m_nEventType
	 *            STREAM_ERROR_DECODE_FAILED : decoding failure
	 *            STREAM_ERROR_LOCKED_SEGMENT : the used dot-pattern is not
	 *            allowed STREAM_ERROR_NON_ANOTO_PAPER : the used dot-pattern is
	 *            not Anoto pattern STREAM_ERROR_FRAME_SKIPPED : frame skipped
	 *            STREAM_ERROR_CAMERA_RESTARTED : Error event of camera is
	 *            restarted
	 * @return 0
	 */
	public abstract int onNoCoord(int m_nEventType);

	public abstract int onRemainBattery(int percent);

	public abstract int onMemoryFillLevel(int percent);

	public abstract int onSoundStatus(byte allSound, byte sleepSound);
}
