package com.speektool.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.google.common.base.Preconditions;

public class NetUtil {
	private static final String tag = NetUtil.class.getSimpleName();

	public static boolean isNetPath(String path) {
		Preconditions.checkArgument(!TextUtils.isEmpty(path), tag + ".isNetPath:path cannot be empty.");
		if (path.startsWith("http://") || path.startsWith("https://"))
			return true;
		return false;

	}

	public static final int NETWORK_NONE = 0;

	public static final int NETWORK_MOBILE = 1;

	public static final int NETWORK_WIFI = 2;

	public static final int NETWORK_ALL = 3;

	public static final int NETWORK_CMWAP = 4;

	public static final int NETWORK_UNIWAP = 5;

	public static final int NETWORK_CTWAP = 6;

	public static final int NETWORK_3GWAP = 7;

	public static int getNetConnectType(Context context) {
		final ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		int ret = NETWORK_NONE;
		if (null != connMgr) {
			NetworkInfo info = connMgr.getActiveNetworkInfo();

			NetworkInfo resInfo = null;
			if (null != info && info.isConnected()) {
				resInfo = info;
			} else {
				NetworkInfo arrayInfo[] = connMgr.getAllNetworkInfo();
				if (null != arrayInfo) {
					for (int i = 0; i < arrayInfo.length; ++i) {
						if (null != arrayInfo[i] && arrayInfo[i].isConnected()) {
							resInfo = arrayInfo[i];
							break;
						}
					}
				}
			}
			if (null != resInfo) {
				if (ConnectivityManager.TYPE_WIFI == resInfo.getType()) {
					ret = NETWORK_WIFI;
				} else {
					ret = NETWORK_MOBILE;
				}
			}
		}
		return ret;
	}

	public static final String getNetTypeName(Context context) {
		NetworkInfo info = getNetInfo(context);
		String name = null;
		if (null != info) {
			name = info.getTypeName().toLowerCase();
			if (!name.equals("wifi")) {
				name = info.getExtraInfo();
				if (null != name) {
					name = name.toLowerCase();
				}
			}
		}
		return name;
	}

	public static final NetworkInfo getNetInfo(Context context) {
		final ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo resInfo = null;
		if (null != connMgr) {
			NetworkInfo info = connMgr.getActiveNetworkInfo();

			if (null != info && info.isConnected()) {
				resInfo = info;
			} else {
				NetworkInfo arrayInfo[] = connMgr.getAllNetworkInfo();
				if (null != arrayInfo) {
					for (int i = 0; i < arrayInfo.length; ++i) {
						if (null != arrayInfo[i] && arrayInfo[i].isConnected()) {
							resInfo = arrayInfo[i];
							break;
						}
					}
				}
			}
		}
		return resInfo;
	}

	public static boolean isHaveNet(Context context) {
		return (getNetConnectType(context) != NETWORK_NONE);
	}

	/**
	 * 判断联网代理类型 cmwap uniwap ctwap or other
	 * 
	 * @param context
	 * @return
	 */
	public static int getConnectProxyType(Context context) {
		final ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo active = connMgr.getActiveNetworkInfo();
		if (active == null) {
			return NETWORK_NONE;
		}
		if (active.getType() == ConnectivityManager.TYPE_MOBILE) {
			String info = active.getExtraInfo();
			if (info == null) {
				return NETWORK_NONE;
			}
			info = info.toLowerCase();
			if (info.equals("cmwap")) {
				return NETWORK_CMWAP;
			} else if (info.equals("uniwap")) {
				return NETWORK_UNIWAP;
			} else if (info.equals("ctwap")) {
				return NETWORK_CTWAP;
			} else if (info.equals("3gwap")) {
				return NETWORK_3GWAP;
			} else {
				return NETWORK_WIFI;
			}
		} else {
			return NETWORK_WIFI;
		}
	}

}
