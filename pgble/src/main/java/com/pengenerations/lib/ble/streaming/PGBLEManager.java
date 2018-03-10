package com.pengenerations.lib.ble.streaming;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import com.pengenerations.lib.util.PGUtils;

public class PGBLEManager {

	final String TAG = "PGBLEManager";

	static public enum OperatingMode {
		RELIABLE, LOW_LATENCY, RESERVED2, RESERVED3, RESERVED4, RESERVED5, RESERVED6, RESERVED7, NONE
	};

	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothGatt mConnectedGatt = null;

	public static final String EXTRAS_DEVICE_STATUS = "SUCCESS";
	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_PAIRED = "DEVICE_PAIRED";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

	private static final UUID CONFIG_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

	private String mDeviceAddress;

	private static final long CONNECTION_PERIOD = 0;

	private byte m_BleInterval = 13;

	BluetoothDevice mConnectDev = null;

	Context m_Context = null;
	Handler m_Handler = null;
	PGUtils m_Utils = null;
	SeqHandler m_SeqHandler = null;

	boolean m_bfDevPaired;

	public PGBLEManager(Context context, boolean bfDevPaired) {
		m_Context = context;
		m_bfDevPaired = bfDevPaired;
		m_Handler = new Handler();
		m_Utils = new PGUtils();
	}

	public void Destroy() {
		if (mConnectedGatt != null) {
			mConnectedGatt.disconnect();
			mConnectedGatt.close();
			mConnectedGatt = null;
		}

		if (m_Handler != null)
			m_Handler = null;
		if (m_Utils != null)
			m_Utils = null;

		if (mBluetoothAdapter != null)
			mBluetoothAdapter = null;

		// m_Context.unregisterReceiver(PGServiceReceiver);
	}

	/**
	 * 初始化
	 * 
	 * @param devAddress
	 *            - 设备的蓝牙地址
	 */
	public void initialize(String devAddress) {
		if (m_Handler == null)
			m_Handler = new Handler();
		if (m_Utils == null)
			m_Utils = new PGUtils();
		if (mBluetoothAdapter == null) {
			BluetoothManager manager = (BluetoothManager) m_Context.getSystemService(m_Context.BLUETOOTH_SERVICE);
			mBluetoothAdapter = manager.getAdapter();
		}
		if (mBluetoothAdapter.isDiscovering())
			mBluetoothAdapter.cancelDiscovery();
		mDeviceAddress = devAddress;
		/*
		 * Make a connection with the device using the special LE-specific
		 * connectGatt() method, passing in a callback for GATT events
		 */
		bleDevConnect();
		m_BlePenStatus = BLEPenStatus.BPS_DISCONNECTED;
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
		// filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		// m_Context.registerReceiver(PGServiceReceiver, filter);
	}

	@SuppressLint("NewApi")
	private final BroadcastReceiver PGServiceReceiver = new BroadcastReceiver() {
		@Override
		public synchronized void onReceive(Context context, Intent intent) {
			if (intent == null)
				return;
			String action = intent.getAction();
			if (action == null)
				return;
			if (action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
				Log.e("PGServiceReceiver", "PGServiceReceiver] ACTION_PAIRING_REQUEST");

				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// byte[] pinBytes = BluetoothDevice.convertPinToBytes("1234");
				// device.setPin(pinBytes);
				device.setPairingConfirmation(false);

				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					Log.e("PGServiceReceiver", "PGServiceReceiver] BOND_BONDED");
				} else {
					Log.e("PGServiceReceiver", "PGServiceReceiver] Else BOND_BONDED");
				}
			}
		}
	};

	@SuppressLint("NewApi")
	public void bleDevConnect() {
		try {
			if (m_Handler != null)
				m_Handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (mBluetoothAdapter != null && mDeviceAddress != null) {
							// try
							// {
							if (mBluetoothAdapter.isDiscovering()) {
								mBluetoothAdapter.cancelDiscovery();
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							mConnectDev = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
							if (m_bfDevPaired == false) {
								Log.d(TAG, "连接到 " + mConnectDev.getName());
								unpairEachDevice(mConnectDev);
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}

								mConnectDev.createBond();
								mConnectDev.setPairingConfirmation(true);

								StartCheckBleBond();
							} else {
								Log.d(TAG, "Connecting to " + mConnectDev.getName());
								mConnectedGatt = mConnectDev.connectGatt(m_Context, false, mGattCallback);
								// mConnectedGatt =
								// mConnectDev.connectGatt(m_Context, false,
								// mGattCallback);
								// mHandler.sendMessage(Message.obtain(null,
								// MSG_PROGRESS,
								// "Connecting to "+mConnectDev.getName()+"..."));
							}
							// }catch(Exception e){}
						}
					}
				}, CONNECTION_PERIOD);
		} catch (Exception e) {
		}
	}

	public void reConnect() {
		if (mConnectedGatt != null)
			mConnectedGatt.connect();
	}

	private void unpairDevice(BluetoothDevice targetDev) {
		Set<BluetoothDevice> pairedDev = mBluetoothAdapter.getBondedDevices();
		for (BluetoothDevice dev : pairedDev) {
			try {
				if (targetDev.getAddress().contains(dev.getAddress()) == true) {
					Method m = dev.getClass().getMethod("removeBond", (Class[]) null);
					m.invoke(dev, (Object[]) null);
				}
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}

	/** 流消息类型 */
	public enum PGStreamingMessageType {
		MT_REQUEST_COMMAND(0x10), //
		MT_EVENT(0x11), //
		MT_COORDINATE(0x12), //
		MT_NO_COORDINATE(0x13), //
		MT_GET_PEN_INFO(0x14), //
		MT_GET_PEN_INFO_EXT(0x15), //
		MT_MULTI_COORIDNATE(0x16);//

		private int value;

		private PGStreamingMessageType(int value) {
			this.value = value;
		}
	};

	public int GetIndexPGStreamingMessageType(byte value) {
		for (int i = 0; i < PGStreamingMessageType.values().length; i++)
			if (PGStreamingMessageType.values()[i].value == value)
				return i;

		return -1;
	}

	public PGStreamingMessageType GetPGStreamingMessageType(int index) {
		return PGStreamingMessageType.values()[index];
	}

	public RequestHandlerCommand GetSctCommand(byte b) {
		return RequestHandlerCommand.values()[b];
	}

	// request enumerate
	public enum RequestHandlerCommand {
		RHC_DEV_RTS, // = 0x00,
		RHC_DEV_ACK, // = 0x01,
		RHC_DEV_BAT_INFO, // = 0x02,
		RHC_DEV_MEM_INFO, // = 0x03,
		RHC_DEV_PEN_INFO, // = 0x04,
		RHC_DEV_RESERVED1, // = 0x05,
		RHC_DEV_PEN_MODE, // = 0x06,
		RHC_DEV_ERASE_ALL, // = 0x07,
		RHC_DEV_HOVER_MODE, // = 0x08,
		RHC_DEV_PEN_EXT_INFO, // = 0x09,
		RHC_DEV_FLOW_CTRL, // = 0x0A,
		RHC_DEV_BLE_INTERVAL, // = 0x0B,
		RHC_DEV_RESERVED2, // = 0x0C,
		RHC_DEV_SOUND_CTRL, // = 0x0D,

	};

	// response enumerate
	public enum ResponseHandlerCommand {
		RHS_HOST_CTS, // = 0x00,
		RHS_HOST_RSP_ACK, // = 0x01,
		RHS_HOST_RSP_NAK, // = 0x02,
		RHS_HOST_REQ_BAT_INFO, // = 0x03,
		RHS_HOST_REQ_MEM_INFO, // = 0x04,
		RHS_HOST_REQ_PEN_INFO, // = 0x05,
		RHS_HOST_REQ_PEN_MODE, // = 0x06,
		RHS_HOST_REQ_ERASE_ALL, // = 0x07,
		RHS_HOST_REQ_HOVER_MODE, // = 0x08,
		RHS_HOST_REQ_PEN_EXT_INFO, // = 0x09,
		RHS_HOST_REQ_RESERVED1, // = 0x0A,
		RHS_HOST_REQ_RESERVED2, // = 0x0B,
		RHS_HOST_REQ_RESERVED3, // = 0x0C,
		RHS_HOST_REQ_SOUND_CTRL, // = 0x0D,
	};

	// event for EventMessageType
	final int EVENT_PEN_DOWN = 5;
	final int EVENT_PEN_UP = 6;

	/**
	 * Check Bluetooth LE packet with checksum
	 * 
	 * @param data
	 * @return true : valid data false : invalid data
	 */
	boolean CheckCRCBLEPacket(byte[] data) {
		// size : type + length + payload size
		int size = data[2] + 2;

		byte checksum = 0;

		for (int i = 0; i < size; i++) {
			checksum += data[1 + i];
		}

		if (checksum == data[size + 1])
			return true;

		return false;
	}

	final int BT_STREAMING_MASK = 0x10;
	final int BT_USB_COMMAND_MASK = 0x20;

	/**
	 * Main Pasor for Bluetooth LE
	 * 
	 * @param characteristic
	 */
	void parseBLEPakcet(BluetoothGattCharacteristic characteristic) {
		// packet structure
		// | preamble | type | length | payload | crc |
		final byte[] data = characteristic.getValue();

		// check crc check
		if (CheckCRCBLEPacket(data) == true) {
			byte preamble = data[0];
			// -2 is 0xFE
			if (preamble == -2) {
				if ((data[1] & BT_USB_COMMAND_MASK) == BT_USB_COMMAND_MASK) {
					Log.d(TAG, "BT Command Mask] Enter");
					UsbCommandParsor(data);
				} else if ((data[1] & BT_STREAMING_MASK) == BT_STREAMING_MASK) {
					// Log.d(TAG,"BT Streaming Mask] Enter");
					StreamingParsor(data);
				} else {
					Log.d(TAG, "data[0]:" + data[0] + ",data[1]:" + data[1] + ",data[2]:" + data[2]);
					Log.d(TAG, "Not support command");
				}
			}
		} else {
			Log.e(TAG, "CRC Chehck ERROR");
		}
	}

	/**
	 * Streaming Parsor
	 * 
	 * @param data
	 */
	private void StreamingParsor(byte[] data) {
		// packet structure| preamble | type | length | payload | crc |
		byte[] command = new byte[data[2]];
		System.arraycopy(data, 3, command, 0, data[2]);
		// 根据类型值对蓝牙进行详细解析
		switch (GetPGStreamingMessageType(GetIndexPGStreamingMessageType(data[1]))) {
		case MT_REQUEST_COMMAND:
			handlerCommand(command);
			break;
		case MT_EVENT:
			handlerEvent(command);
			break;
		case MT_COORDINATE:
			handlerCoordinate(command);
			break;
		case MT_NO_COORDINATE:
			handlerNoCoordinate(command);
			break;
		case MT_GET_PEN_INFO:
			DisplayPenInfo(command);
			break;
		case MT_GET_PEN_INFO_EXT:
			break;
		case MT_MULTI_COORIDNATE:
			handlerMultiCoordinate(command);
			break;
		default:
			Log.e(TAG, "不支持的消息类型");
			break;
		}
	}

	/**
	 * ReqMessageType handler
	 * 
	 * @param cmd
	 */
	private void handlerCommand(byte[] cmd) {
		byte[] subPayload = null;
		byte subLength = 0;

		switch (GetSctCommand(cmd[0])) {
		case RHC_DEV_RTS:// rts
		{
			subLength = 2;
			subPayload = new byte[subLength];
			subPayload[0] = (byte) ResponseHandlerCommand.RHS_HOST_CTS.ordinal();// cts
			subPayload[1] = 0;

			// notify pen connected
			NotifyConnection(notifyConnection.NC_CONNECT, cmd[1]);

			m_SeqHandler = new SeqHandler(cmd[2]);
		}
			break;
		case RHC_DEV_ACK:// ack
			subLength = 3;
			subPayload = new byte[subLength];

			if (m_SeqHandler.GetSeqStatus() == true)
				subPayload[0] = (byte) ResponseHandlerCommand.RHS_HOST_RSP_ACK.ordinal();// ack
			else {
				subPayload[0] = (byte) ResponseHandlerCommand.RHS_HOST_RSP_NAK.ordinal();// nak
				m_SeqHandler.initSeq(cmd[1]);
			}
			subPayload[1] = cmd[1]; // start seq;
			subPayload[2] = cmd[2]; // last seq;

			break;
		case RHC_DEV_BAT_INFO:
			Log.d(TAG, "Batter Capacity : " + cmd[1]);
			// notify pen connected
			NotifyStreaming(notifyStreaming.NS_BATTERY, new notifyArgs(cmd[1]));
			break;
		case RHC_DEV_MEM_INFO:
			Log.d(TAG, "Memory Fill Level : " + cmd[1]);
			// notify pen connected
			NotifyStreaming(notifyStreaming.NS_MEMORY, new notifyArgs(cmd[1]));
			break;
		case RHC_DEV_SOUND_CTRL:
			Log.d(TAG, "All Sound : " + cmd[1] + ", Sleep Sound : " + cmd[2]);
			NotifyStreaming(notifyStreaming.NS_SOUND_STATUS, new notifyArgs(cmd[1], cmd[2]));
			break;
		default:
			// 不支持的类型
			break;
		}
		if (subLength > 0) {
			SendHostCommand(subPayload, subLength);
		}
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * EventMessageType data handler
	 * 
	 * @param data
	 */
	private void handlerEvent(byte[] data) {
		Log.d("SEQ_CHECK", "handlerEvent] seq : " + data[1]);

		if (m_SeqHandler.SetCurSeq(data[1]) == true) {
			// Notify Event
			NotifyStreaming(notifyStreaming.NS_EVENT, new notifyArgs(data[0]));
		}
		/*
		 * MONI 2014-08-01 if( data[0] == 0x05 ) m_nFsrCnt = 0;
		 */
	}

	/**
	 * CoordinateMessageType data handler
	 * 
	 * @param data
	 */
	int m_nFsrCnt = 0;

	byte[] m_PrivousPageAddress = new byte[8];

	private void handlerCoordinate(byte[] data) {
		final ByteBuffer sBuf = ByteBuffer.allocate(data.length);
		sBuf.position(0);
		// sBuf.put(data, 0, data.length);
		// page address
		sBuf.put(data[7]);
		sBuf.put(data[6]);
		sBuf.put(data[5]);
		sBuf.put(data[4]);
		sBuf.put(data[3]);
		sBuf.put(data[2]);
		sBuf.put(data[1]);
		sBuf.put(data[0]);
		// x coordinate
		sBuf.put(data[9]);
		sBuf.put(data[8]);
		// y coordinate
		sBuf.put(data[11]);
		sBuf.put(data[10]);
		// force
		sBuf.put(data[12]);

		// backup page address
		System.arraycopy(data, 0, m_PrivousPageAddress, 0, 8);

		long pageAddr = sBuf.getLong(0);
		short x = sBuf.getShort(8);
		short y = sBuf.getShort(10);
		byte force = data[12];
		byte seq = data[13];

		if (m_SeqHandler.SetCurSeq(seq) == true) {
			Log.d("SEQ_CHECK", "handlerCoordinate] seq : " + data[13]);
			NotifyStreaming(notifyStreaming.NS_COORDINATE, new notifyArgs(0, pageAddr, x, y, force));
		} else {
			Log.d("MissCoord", "Miss Coordinate : x : " + x + ", y : " + y);
		}

	}

	/**
	 * Muti-CoordinateMessageType data handler
	 * 
	 * @param data
	 */
	private void handlerMultiCoordinate(byte[] data) {
		final ByteBuffer sBuf = ByteBuffer.allocate(data.length);
		sBuf.position(0);
		// sBuf.put(data, 0, data.length);
		// page address
		sBuf.put(data[1]);
		sBuf.put(data[0]);
		sBuf.put(data[3]);
		sBuf.put(data[2]);
		sBuf.put(data[4]);
		sBuf.put(data[5]);
		sBuf.put(data[6]);
		sBuf.put(data[7]);
		// x coordinate
		sBuf.put(data[9]);
		sBuf.put(data[8]);
		// y coordinate
		sBuf.put(data[11]);
		sBuf.put(data[10]);
		// force
		sBuf.put(data[12]);

		short x = sBuf.getShort(8);
		short y = sBuf.getShort(10);
		byte force = data[12];
		byte seq = data[13];

		if (m_SeqHandler.SetCurSeq(seq) == true) {
			final ByteBuffer sTmp = ByteBuffer.allocate(m_PrivousPageAddress.length);
			sTmp.position(0);
			sTmp.put(m_PrivousPageAddress[7]);
			sTmp.put(m_PrivousPageAddress[6]);
			sTmp.put(m_PrivousPageAddress[5]);
			sTmp.put(m_PrivousPageAddress[4]);
			sTmp.put(m_PrivousPageAddress[3]);
			sTmp.put(m_PrivousPageAddress[2]);
			sTmp.put(m_PrivousPageAddress[1]);
			sTmp.put(m_PrivousPageAddress[0]);
			long pageAddr = sTmp.getLong(0);

			Log.d("SEQ_CHECK", "handlerMultiCoordinate] x : " + x + " , y : " + y + ", seq : " + seq);
			NotifyStreaming(notifyStreaming.NS_COORDINATE, new notifyArgs(0, pageAddr, x, y, force));

			x = sBuf.getShort(0);
			y = sBuf.getShort(2);
			force = data[4];
			seq = data[5];

			if (m_SeqHandler.SetCurSeq(seq) == true) {
				Log.d("SEQ_CHECK", "handlerMultiCoordinate] x : " + x + ", y : " + y + ", seq : " + seq);
				NotifyStreaming(notifyStreaming.NS_COORDINATE, new notifyArgs(0, pageAddr, x, y, force));
			}
		} else {
			Log.d("MissCoord", "Miss Coordinate : x : " + x + ", y : " + y);
		}

	}

	/**
	 * NoCoordinateMessageType data handler
	 * 
	 * @param data
	 */
	private void handlerNoCoordinate(byte[] data) {
		// Not Implemented
		Log.d(TAG, "handlerNoCoordinate] Enter");

		if (m_SeqHandler.SetCurSeq(data[1]) == true) {
		}
	}

	/**
	 * GetPenInfoMessageType display handler
	 */
	public String m_PenInfo = null;

	private void DisplayPenInfo(byte[] data) {
		// for(int i=0;i<data.length;i++)
		// Log.d(TAG,data.toString());
		Log.d(TAG, "PenInfo Size : " + data.length);
		String sw_ver = m_Utils.bytesToHex(data[1]) + m_Utils.bytesToHex(data[0]);
		String pen_id = m_Utils.bytesToHex(data[9]) + m_Utils.bytesToHex(data[8]) + m_Utils.bytesToHex(data[7])
				+ m_Utils.bytesToHex(data[6]) + m_Utils.bytesToHex(data[5]) + m_Utils.bytesToHex(data[4])
				+ m_Utils.bytesToHex(data[3]) + m_Utils.bytesToHex(data[2]);

		String vid = m_Utils.bytesToHex(data[11]) + m_Utils.bytesToHex(data[10]);
		String pid = m_Utils.bytesToHex(data[13]) + m_Utils.bytesToHex(data[12]);

		final ByteBuffer sBuf = ByteBuffer.allocate(data.length);
		sBuf.position(0);
		sBuf.put(data);

		short swVer = sBuf.getShort(0);
		short svid = sBuf.getShort(10);
		short spid = sBuf.getShort(12);
		long serial = sBuf.getLong(2);

		byte[] dst = new byte[8];
		System.arraycopy(sBuf.array(), 2, dst, 0, 8);
		String sPenID = m_Utils.GetPenID(serial);

		m_PenInfo = "sw_ver :" + sw_ver + ", pen_id : " + sPenID + ", vid : " + vid + ", pid : " + pid;

		byte[] subPayload = null;
		byte subLength = 2;
		subPayload = new byte[subLength];
		subPayload[0] = (byte) ResponseHandlerCommand.RHS_HOST_CTS.ordinal();// cts
		subPayload[1] = 0;
		if (subLength > 0) {
			SendHostCommand(subPayload, subLength);
		}
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		NotifyStreaming(notifyStreaming.NS_NEW_SESSION, new notifyArgs(svid, spid, serial, swVer));
		Log.d(TAG, "Pen Info] " + m_PenInfo);
	}

	/**
	 * Send Command to Device
	 */
	public synchronized void SendHostCommand(byte[] subPayload, byte subLength) {
		int length = 2 + subLength; // header size + payload size;
		byte[] payload = new byte[length];

		payload[0] = 0x10; // host command type
		payload[1] = subLength; // param length

		for (int i = 0; i < subLength; i++)
			payload[2 + i] = subPayload[i];

		Send2ReliableBluetoothLE(payload, length);
	}

	Object m_oBLEWriteSync = new Object();
	boolean m_bfBLEWriteResult = false;

	boolean WaitForSync() {
		synchronized (m_oBLEWriteSync) {
			try {
				m_oBLEWriteSync.wait(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return m_bfBLEWriteResult;
	}

	void NotifyForSync(boolean result) {
		m_bfBLEWriteResult = result;
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		synchronized (m_oBLEWriteSync) {
			m_oBLEWriteSync.notify();
		}
	}

	/**
	 * Send data to Bluetooth LE device
	 * 
	 * @param data
	 * @param size
	 */
	void Send2ReliableBluetoothLE(byte[] data, int size) {
		int retry = 3;
		int totalSize = 4 + size;
		byte[] packet = new byte[totalSize];
		byte checksum = 0;
		// make header
		packet[0] = (byte) 0xef; // preamble
		packet[1] = (byte) 0x01; // type
		packet[2] = (byte) size; // payload length
		System.arraycopy(data, 0, packet, 3, size); // copy data to payload
													// buffer

		// calc checksum
		for (int i = 1; i < totalSize - 1; i++) {
			checksum += packet[i];
		}
		packet[totalSize - 1] = checksum;

		// request write to Bluetooth LE device
		if (mWriteCharacteristic != null) {
			for (int i = 0; i < retry; i++) {
				try {
					mWriteCharacteristic.setValue(packet);
					// mConnectedGatt.beginReliableWrite();
					mConnectedGatt.writeCharacteristic(mWriteCharacteristic);
					// mConnectedGatt.executeReliableWrite();
				} catch (Exception e) {
				}

				if (WaitForSync() == true)
					break;
				else
					Log.e(TAG, "BLE Write Error");
			}
		}

		try {
			Thread.sleep(m_BleInterval);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Send data to Bluetooth LE device
	 * 
	 * @param data
	 * @param size
	 */
	void Send2BluetoothLE(byte[] data, int size) {
		int totalSize = 4 + size;
		byte[] packet = new byte[totalSize];
		byte checksum = 0;

		// make header
		packet[0] = (byte) 0xef; // preamble
		packet[1] = (byte) 0x01; // type
		packet[2] = (byte) size; // payload length
		System.arraycopy(data, 0, packet, 3, size); // copy data to payload
													// buffer

		// calc checksum
		for (int i = 1; i < totalSize - 1; i++) {
			checksum += packet[i];
		}

		packet[totalSize - 1] = checksum;

		// request write to Bluetooth LE device
		if (mWriteCharacteristic != null) {
			for (int i = 0; i < 3; i++) {
				try {
					// mConnectedGatt.beginReliableWrite();
					mWriteCharacteristic.setValue(packet);
					mConnectedGatt.writeCharacteristic(mWriteCharacteristic);
					// mConnectedGatt.executeReliableWrite();
				} catch (Exception e) {
				}

				if (WaitForSync() == true)
					break;
				else
					Log.e(TAG, "BLE Write Error");
			}
		}
	}

	public void Clear() {
	}

	/**
	 * Send IBIS USB Command
	 */
	public void SendUsbCommand(byte cmd, byte lparam, byte rparam) {
		byte[] payload = new byte[4];

		payload[0] = cmd; // mode change
		payload[1] = 0x00;
		payload[2] = lparam; // lparam
		payload[3] = rparam; // rparam

		Send2BluetoothLE(payload, 4);
	}

	/**
	 * Usb Command Parsor
	 * 
	 * @param data
	 */
	public static final byte UMT_NONT = 0x00;
	public static final byte UMT_STREAMING = 0x01;
	public static final byte UMT_USB_COMMAND = 0x02;
	public static final byte UMT_FIRMWARE_UPGRADE = 0x03;
	public static final byte UMT_DUT_TEST = 0x04;
	public static final byte UMT_DEBUG = 0x05;

	private void UsbCommandParsor(byte[] data) {
		switch (data[1]) {
		case UMT_NONT:
			break;
		case UMT_STREAMING:
			break;
		case UMT_USB_COMMAND:
			break;
		case UMT_DUT_TEST:
			break;
		case UMT_DEBUG:
			break;
		}
	}

	/************************************************************************************************
	 * BletoothLE Service related
	 ***********************************************************************************************/

	/**
	 * Global variable
	 */
	// Recevice Characteristics of Service
	private BluetoothGattCharacteristic mReadCharacteristic = null;
	// Transfer Characteristics of Service
	private BluetoothGattCharacteristic mWriteCharacteristic = null;

	/**
	 * Search Write Service of IBIS Pen
	 */
	void SearchWriteCharacters() {
		BluetoothGattCharacteristic characteristic;
		// mConnectedGatt.writeCharacteristic(characteristic);
		characteristic = mConnectedGatt.getService(
				PGGattAttributes.String2UUID(PGGattAttributes.PG_STREAMING_SERVICE_UUID)).getCharacteristic(
				PGGattAttributes.String2UUID(PGGattAttributes.PG_STREAMING_SERVICE_WRITE_UUID));
	}

	/**
	 * Enable notification each service of IBIS Pen
	 * 
	 * @param gatt
	 */
	boolean enablePGCharateristics(BluetoothGatt gatt) {
		Log.d(TAG, "enablePGCharateristics] Enable Characteristic");

		mReadCharacteristic = enableCharateristics(gatt,
				UUID.fromString(PGGattAttributes.PG_STREAMING_SERVICE_READ_UUID), true);
		mWriteCharacteristic = enableCharateristics(gatt,
				UUID.fromString(PGGattAttributes.PG_STREAMING_SERVICE_WRITE_UUID), false);

		// notify event
		if (mReadCharacteristic != null && mWriteCharacteristic != null)
			return true;

		return false;
	}

	/**
	 * Enable notification of service
	 * 
	 * @param gatt
	 * @param uuid
	 * @param notify
	 * @return
	 */
	BluetoothGattCharacteristic enableCharateristics(BluetoothGatt gatt, UUID uuid, boolean notify) {
		BluetoothGattCharacteristic characteristic = null;
		try {
			Log.d(TAG, "enableGetCharateristics]Enter");
			BluetoothGattService btGattService = gatt.getService(UUID
					.fromString(PGGattAttributes.PG_STREAMING_SERVICE_UUID));

			if (btGattService != null) {

				BluetoothGattCharacteristic GattChar;
				GattChar = btGattService.getCharacteristic(uuid);

				if (GattChar != null) {
					if (notify == true) {
						gatt.setCharacteristicNotification(GattChar, true);

						BluetoothGattDescriptor desc = GattChar.getDescriptor(CONFIG_DESCRIPTOR);
						desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
						gatt.writeDescriptor(desc);
					}

					characteristic = GattChar;
				}
			}
		} catch (Exception e) {
			Log.d(TAG, "enableCharateristics]E] " + e.toString());
			characteristic = null;
		}

		return characteristic;
	}

	/*
	 * In this callback, we've created a bit of a state machine to enforce that
	 * only one characteristic be read or written at a time until all of our
	 * sensors are enabled and we are registered to get notifications.
	 */
	private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			Log.d(TAG, "Connection State Change: " + status + " -> " + connectionState(newState));
			Log.d("BluetoothGattCallback", "State Change: " + status + " -> New State : " + newState + ", "
					+ connectionState(newState));

			if (newState == BluetoothProfile.STATE_CONNECTED) {
				if (status == BluetoothGatt.GATT_SUCCESS) {
					Log.d("BluetoothGattCallback", "STATE_CONNECTED] GATT_SUCCESS");
					/*
					 * Once successfully connected, we must next discover all
					 * the services on the device before we can read and write
					 * their characteristics.
					 */
					gatt.discoverServices();

					m_BlePenStatus = BLEPenStatus.BPS_CONNECTTING;

				} else if (status == 133) {
					Log.d("BluetoothGattCallback", "STATE_CONNECTED] GATT_133");
					// gatt error
					gatt.disconnect();
					gatt.close();
					mWriteCharacteristic = null;

					try {
						NotifyConnection(notifyConnection.NC_CONNECT_FAIL,
								(byte) conFailReason.NFR_GATT_ERROR.ordinal());
						// NotifyStreaming(notifyStreaming.NS_DISCONNECT, new
						// notifyArgs(-1));
					} catch (Exception e) {
					}

				} else if (status == BluetoothGatt.GATT_FAILURE) {
					Log.d("BluetoothGattCallback", "STATE_CONNECTED] GATT_FAILURE");
					/*
					 * If there is a failure at any stage, simply disconnect
					 */
					gatt.disconnect();
					gatt.close();
					mWriteCharacteristic = null;

					try {
						NotifyConnection(notifyConnection.NC_CONNECT_FAIL,
								(byte) conFailReason.NFR_NOT_FOUND_DEVICE.ordinal());
					} catch (Exception e) {
					}
				} else {
					Log.d("BluetoothGattCallback", "STATE_CONNECTED] GATT_NotDefine");
				}
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

				if (status == BluetoothGatt.GATT_SUCCESS) {
					Log.d("BluetoothGattCallback", "STATE_DISCONNECTED] GATT_SUCCESS");

					// If there is a failure at any stage, simply disconnect
					gatt.disconnect();
					gatt.close();
					mWriteCharacteristic = null;
					try {
						NotifyStreaming(notifyStreaming.NS_DISCONNECT, new notifyArgs(-1));
					} catch (Exception e) {
					}
				}
			} else {
				Log.d("BluetoothGattCallback", "STATE_NOT_DEFINE] GATT_NotDefine");
				// If there is a failure at any stage, simply disconnect
				gatt.disconnect();
				gatt.close();
				mWriteCharacteristic = null;
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			Log.d("BluetoothGattCallback", "Services Discovered: " + status);
			/*
			 * With services discovered, we are going to reset our state machine
			 * and start working through the sensors we need to enable
			 */

			try {
				if (enablePGCharateristics(gatt) == true) {
					// Notify Event
					NotifyConnection(notifyConnection.NC_START_PEN_SERVICE, 0);
				} else {
					// Notify Event
					NotifyConnection(notifyConnection.NC_CONNECT_FAIL,
							(byte) conFailReason.NFR_NOT_FOUND_SERVICE.ordinal());
				}
			} catch (Exception e) {
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

			try {
				parseBLEPakcet(characteristic);
			} catch (Exception e) {
			}
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS)
				NotifyForSync(true);
			else
				NotifyForSync(false);
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			/*
			 * After notifications are enabled, all updates from the device on
			 * characteristic value changes will be posted here. Similar to
			 * read, we hand these up to the UI thread to update the display.
			 */
			try {
				parseBLEPakcet(characteristic);
			} catch (Exception e) {
			}
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
			// Log.e("BluetoothGattCallback","onDescriptorWrite");
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Log.e("BluetoothGattCallback", "onDescriptorWrite] GATT_SUCCESS");
				// if
				// (PGGattAttributes.PG_STREAMING_SERVICE_WRITE_UUID.equals(descriptor.getCharacteristic().getUuid()))
				// {
				// mCallbacks.onGlucoseMeasurementNotificationEnabled();
				//
				// if (mGlucoseMeasurementContextCharacteristic != null) {
				// enableGlucoseMeasurementContextNotification(gatt);
				// } else {
				// enableRecordAccessControlPointIndication(gatt);
				// }
				// }
				//
				// if
				// (GM_CONTEXT_CHARACTERISTIC.equals(descriptor.getCharacteristic().getUuid()))
				// {
				// mCallbacks.onGlucoseMeasurementContextNotificationEnabled();
				// enableRecordAccessControlPointIndication(gatt);
				// }
				//
				// if
				// (RACP_CHARACTERISTIC.equals(descriptor.getCharacteristic().getUuid()))
				// {
				// mCallbacks.onRecordAccessControlPointIndicationsEnabled();
				// }
			} else if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION) {
				// this is where the tricky part comes
				Log.e("BluetoothGattCallback", "onDescriptorWrite] GATT_INSUFFICIENT_AUTHENTICATION");

				if (gatt.getDevice().getBondState() == BluetoothDevice.BOND_NONE) {
					// mConnectedGatt.onBondingRequired();
					// I'm starting the Broadcast Receiver that will listen for
					// bonding process changes
					// final IntentFilter filter = new
					// IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
					// mContext.registerReceiver(mBondingBroadcastReceiver,
					// filter);
				} else {
					// this situation happens when you try to connect for the
					// second time to already bonded device
					// it should never happen, in my opinion
					// Logger.e(TAG,
					// "The phone is trying to read from paired device without encryption. Android Bug?");
					// I don't know what to do here
					// This error was found on Nexus 7 with KRT16S build of
					// Andorid 4.4. It does not appear on Samsung S4 with
					// Andorid 4.3.
				}
			} else {
				Log.e("BluetoothGattCallback", "onDescriptorWrite] not supprot");
				// mCallbacks.onError(ERROR_WRITE_DESCRIPTOR, status);
			}
		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			Log.d(TAG, "Remote RSSI: " + rssi);
		}

		@Override
		public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
			Log.d(TAG, "Resutl : " + status);

		}
	};

	private String connectionState(int status) {
		switch (status) {
		case BluetoothProfile.STATE_CONNECTED:
			return "Connected";
		case BluetoothProfile.STATE_DISCONNECTED:
			return "Disconnected";
		case BluetoothProfile.STATE_CONNECTING:
			return "Connecting";
		case BluetoothProfile.STATE_DISCONNECTING:
			return "Disconnecting";
		default:
			return String.valueOf(status);
		}
	}

	/************************************************************************************************/
	// notify functions

	OnPenStreamListener m_OnPenStreamListener = null;

	/** 注册点阵笔流监听 */
	public void RegisterOnPenStreamListener(OnPenStreamListener onPenStreamListener) {
		m_OnPenStreamListener = onPenStreamListener;
	}

	OnPenConnectListener m_OnPenConnectListener = null;

	/** 注册点阵笔连接监听 */
	public void RegisterOnPenConnectListener(OnPenConnectListener onPenConnectListener) {
		m_OnPenConnectListener = onPenConnectListener;
	}

	enum notifyConnection {
		NC_CONNECT, NC_CONNECT_FAIL, NC_START_PEN_SERVICE,
	}

	public enum conFailReason {
		NFR_DISCONNECTED, NFR_NOT_FOUND_SERVICE, NFR_NOT_FOUND_DEVICE, NFR_GATT_ERROR,
	}

	void NotifyConnection(notifyConnection notify, int args) {
		if (m_OnPenConnectListener == null) {
			// not registered listener
			return;
		}

		switch (notify) {
		case NC_CONNECT:

			m_BlePenStatus = BLEPenStatus.BPS_CONNECTED;

			m_OnPenConnectListener.onConnected(args);
			break;
		case NC_CONNECT_FAIL:

			m_BlePenStatus = BLEPenStatus.BPS_CONNECT_FAIL;

			m_OnPenConnectListener.onConnectFailed(args);
			break;
		case NC_START_PEN_SERVICE:

			m_BlePenStatus = BLEPenStatus.BPS_CONNECTTING;

			m_OnPenConnectListener.onPenServiceStarted();
			break;
		}
	}

	enum notifyStreaming {
		NS_NEW_SESSION, NS_EVENT, NS_COORDINATE, NS_PENINFO, NS_BATTERY, NS_MEMORY, NS_SOUND_STATUS, NS_DISCONNECT,
	}

	class notifyArgs {
		short m_Vid;
		short m_Pid;
		long m_Serial;
		short m_swVer;
		long m_nTimeStamp;
		long m_ullPageAddress;
		short m_nX;
		short m_nY;
		byte m_nForce;
		byte m_AllSound;
		byte m_SleepSound;

		public notifyArgs(long nTimeStamp, long ullPageAddress, short nX, short nY, byte nForce) {
			m_nTimeStamp = nTimeStamp;
			m_ullPageAddress = ullPageAddress;
			m_nX = nX;
			m_nY = nY;
			m_nForce = nForce;
		}

		int m_Event;

		public notifyArgs(int event) {
			m_Event = event;
		}

		public notifyArgs(short vid, short pid, long serial, short swVer) {
			m_swVer = swVer;
			m_Vid = vid;
			m_Pid = pid;
			m_Serial = serial;
		}

		public notifyArgs(byte allSnd, byte sleepSnd) {
			m_AllSound = allSnd;
			m_SleepSound = sleepSnd;
		}
	}

	void NotifyStreaming(notifyStreaming notify, notifyArgs args) {
		if (m_OnPenStreamListener == null) {
			// not registered listener
			return;
		}

		switch (notify) {
		case NS_NEW_SESSION:
			m_OnPenStreamListener.onNewSession(0, args.m_Vid, args.m_Pid, args.m_Serial, args.m_swVer);
			break;
		case NS_EVENT:
			if (args.m_Event == EVENT_PEN_DOWN)
				m_OnPenStreamListener.onPendown();
			else if (args.m_Event == EVENT_PEN_UP)
				m_OnPenStreamListener.onPenup();
			break;
		case NS_COORDINATE:
			m_OnPenStreamListener
					.onCoord(args.m_nTimeStamp, args.m_ullPageAddress, args.m_nX, args.m_nY, args.m_nForce);
			break;
		case NS_PENINFO:
			break;
		case NS_BATTERY:
			m_OnPenStreamListener.onRemainBattery(args.m_Event);
			break;
		case NS_MEMORY:
			m_OnPenStreamListener.onMemoryFillLevel(args.m_Event);
			break;
		case NS_SOUND_STATUS:
			m_OnPenStreamListener.onSoundStatus(args.m_AllSound, args.m_SleepSound);
			break;
		case NS_DISCONNECT:

			m_BlePenStatus = BLEPenStatus.BPS_DISCONNECTED;

			m_OnPenStreamListener.onDisconnected();

			break;
		}
	}

	public enum BLEPenStatus {
		BPS_CONNECTED, BPS_DISCONNECTED, BPS_CONNECTTING, BPS_CONNECT_FAIL,
	};

	BLEPenStatus m_BlePenStatus = BLEPenStatus.BPS_DISCONNECTED;

	public BLEPenStatus GetBLEStatus() {
		return m_BlePenStatus;
	}

	/***************************************************************************
	 * Scan Handler Functions
	 ***************************************************************************/
	public void StartCheckBleBond() {
		CheckBleBondThread thd = new CheckBleBondThread();
		thd.start();
	}

	private void unpairEachDevice(BluetoothDevice targetDev) {
		try {
			Method m = targetDev.getClass().getMethod("removeBond", (Class[]) null);
			m.invoke(targetDev, (Object[]) null);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	class CheckBleBondThread extends Thread {
		public CheckBleBondThread() {
		}

		@Override
		public void run() {
			boolean bfPaired = false;

			// wait 10 seconds.
			for (int i = 0; i < 100; i++) {
				if (bfPaired == true)
					break;

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				try {
					Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
					// If there are paired devices
					if (pairedDevices.size() > 0) {
						// Loop through paired devices
						for (BluetoothDevice device : pairedDevices) {
							// Add the name and address to an array adapter to
							// show in a ListView
							if (mConnectDev.getAddress().contains(device.getAddress()) == true) {
								bfPaired = true;
								Log.d("PAIR", "Paired Address : " + device.getAddress());
								break;
							}
						}
					}
				} catch (Exception e) {
					break;
				}
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// mConnectedGatt = mConnectDev.connectGatt(m_Context, false,
			// mGattCallback);

			if (bfPaired) {
				Log.d(TAG, "Connecting to " + mConnectDev.getName());

				mConnectedGatt = mConnectDev.connectGatt(m_Context, false, mGattCallback);
			} else {
				try {
					NotifyConnection(notifyConnection.NC_CONNECT_FAIL,
							(byte) conFailReason.NFR_NOT_FOUND_DEVICE.ordinal());
					// NotifyStreaming(notifyStreaming.NS_DISCONNECT, new
					// notifyArgs(-1));
				} catch (Exception e) {
				}

			}
		}
	}
}
