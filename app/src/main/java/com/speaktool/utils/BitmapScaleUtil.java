package com.speaktool.utils;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapScaleUtil {

	private static final String tag = BitmapScaleUtil.class.getSimpleName();

	/**
	 * 
	 * @param res
	 * @param resId
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, long reqMemorySize) {
		try {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeResource(res, resId, options);
			options.inSampleSize = calculateInSampleSize(options, reqMemorySize);
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeResource(res, resId, options);
		} catch (Error err) {// mem error.
			err.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Bitmap decodeSampledBitmapFromInputstream(InputStream is, long reqMemorySize) {
		try {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(is, null, options);
			options.inSampleSize = calculateInSampleSize(options, reqMemorySize);
			options.inJustDecodeBounds = false;
			Bitmap ret = BitmapFactory.decodeStream(is, null, options);

			is.close();

			return ret;
		} catch (Error err) {
			err.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param urlpath
	 * @param reqMemorySize
	 * @param referer
	 * @return bmp when no error,otherwise return null.
	 */
	public static Bitmap decodeSampledBitmapFromUrl(String urlpath, long reqMemorySize, String referer) {
		try {
			URL url = new URL(urlpath);
			URLConnection con = url.openConnection();
			configConnection(con);
			con.setRequestProperty("Referer", referer);
			con.setDoInput(true);
			con.connect();
			InputStream ins = con.getInputStream();// cause speed low.
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;

			BitmapFactory.decodeStream(ins, null, options);
			//
			ins.close();

			options.inSampleSize = calculateInSampleSize(options, reqMemorySize);

			options.inJustDecodeBounds = false;

			con = url.openConnection();
			configConnection(con);
			con.setRequestProperty("Referer", "http://img4.duitang.com");
			con.setDoInput(true);
			con.connect();
			ins = con.getInputStream();

			Bitmap bp = BitmapFactory.decodeStream(ins, null, options);
			//
			ins.close();

			return bp;

		} catch (Error err) {
			err.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public static boolean isGif(String key) {
		return key.toLowerCase().endsWith(".gif");

	}

	/**
	 * assume one pix use 4 bytes.Bitmap.Config.ARGB_8888.
	 */
	private final static int calculateInSampleSize(BitmapFactory.Options options, long reqMemorySize) {
		final int onePixBytes = 4;
		int reqPixs = (int) (reqMemorySize / onePixBytes);
		final int height = options.outHeight;
		final int width = options.outWidth;
		int orgPixs = height * width;
		int inSampleSize = 1;
		while (orgPixs / Math.pow(inSampleSize, 2) > reqPixs) {
			inSampleSize *= 2;
		}
		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromPath(String localpath, long reqMemorySize) {
		try {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(localpath, options);
			options.inSampleSize = calculateInSampleSize(options, reqMemorySize);
			options.inJustDecodeBounds = false;
			Bitmap bp = null;
			bp = BitmapFactory.decodeFile(localpath, options);
			return bp;
		} catch (Error err) {
			err.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Bitmap decodeSampledBitmapFromByteArray(byte[] bytes, long reqMemorySize) {
		try {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
			options.inSampleSize = calculateInSampleSize(options, reqMemorySize);
			options.inJustDecodeBounds = false;
			Bitmap bp = null;

			bp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

			return bp;
		} catch (Error err) {
			err.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 配置连接
	 */
	private static void configConnection(URLConnection conn) {

		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setRequestProperty(
				"Accept",
				"image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash,"
						+ " application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application,"
						+ " application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
		conn.setRequestProperty("Accept-Language", "zh-CN");
		conn.setRequestProperty("User-Agent",
				"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727;"
						+ " .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
		conn.setRequestProperty("Charset", "UTF-8");
		conn.setConnectTimeout(10000);
		conn.setReadTimeout(10000);
	}

}
