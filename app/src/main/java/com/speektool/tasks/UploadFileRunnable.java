package com.speektool.tasks;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpStatus;

/**
 * 上传文件线程
 * 
 * @author shaoshuai
 * 
 */
public class UploadFileRunnable extends CancelableRunnable {
	/** 设置URLConnection的连接超时时间 */
	private final static int CONNET_TIMEOUT = 10 * 1000;
	/** 设置URLConnection的读取超时时间 */
	private final static int READ_TIMEOUT = 10 * 1000;
	// 设置请求参数的字符编码格式
	// private final static String QUERY_ENCODING = "UTF-8";
	/** 设置返回请求结果的字符编码格式 */
	private final static String ENCODING = "UTF-8";
	// private static final String tagLog = UploadFileRunnable.class
	// .getSimpleName();
	private String url;
	private Map<String, String> params;
	private Map<String, File> files;
	private UploadCallback mProgressCallback;

	/**
	 * 长传回调接口
	 * 
	 * @author shaoshuai
	 * 
	 */
	public static interface UploadCallback {
		/** 长传进度 */
		void onProgressChanged(int progress);

		/** 成功 */
		void onSuccess(String result);

		/** 失败 */
		void onFail();

		/** 取消 */
		void onCancel();

		/** 开始 */
		void onStart();

	}

	/**
	 * 上传文件线程
	 * 
	 * @param tag
	 * @param url
	 * @param params
	 * @param files
	 * @param pProgressCallback
	 */
	public UploadFileRunnable(Tag tag, String url, Map<String, String> params,
			Map<String, File> files, UploadCallback pProgressCallback) {
		super(tag);
		this.url = url;
		this.params = params;
		this.files = files;
		mProgressCallback = pProgressCallback;
	}

	@Override
	public void run() {
		post(url, params, files);

	}

	private void publishProgress(int progress) {
		mProgressCallback.onProgressChanged(progress);

	}

	/**
	 * 上传文件
	 * 
	 * @param url
	 *            请求链接
	 * @param params
	 *            HTTP POST请求文本参数map集合
	 * @param files
	 *            HTTP POST请求文件参数map集合
	 * @return HTTP POST请求结果
	 * @throws IOException
	 */
	private void post(String url, Map<String, String> params,
			Map<String, File> files) {
		HttpURLConnection conn = null;
		DataOutputStream outStream = null;
		try {
			mProgressCallback.onStart();
			publishProgress(0);
			Collection<File> fileList = files.values();
			long totalSize = 0;
			for (File f : fileList) {
				if (tag.isCanceled) {
					mProgressCallback.onCancel();
					return;
				}
				totalSize += f.length();
			}
			//
			String BOUNDARY = UUID.randomUUID().toString();
			String PREFIX = "--", LINEND = "\r\n";
			String MULTIPART_FROM_DATA = "multipart/form-data";

			URL uri = new URL(url);
			conn = (HttpURLConnection) uri.openConnection();
			conn.setConnectTimeout(CONNET_TIMEOUT);
			// 缓存的最长时间
			conn.setReadTimeout(READ_TIMEOUT);
			// 允许输入
			conn.setDoInput(true);
			// 允许输出
			conn.setDoOutput(true);
			// 不允许使用缓存
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
					+ ";boundary=" + BOUNDARY);
			outStream = new DataOutputStream(conn.getOutputStream());
			if (params != null) {
				// 首先组拼文本类型的参数
				StringBuilder sb = new StringBuilder();
				for (Map.Entry<String, String> entry : params.entrySet()) {
					if (tag.isCanceled) {
						mProgressCallback.onCancel();
						conn.disconnect();
						return;
					}
					sb.append(PREFIX);
					sb.append(BOUNDARY);
					sb.append(LINEND);
					sb.append("Content-Disposition: form-data; name=\""
							+ entry.getKey() + "\"" + LINEND);
					sb.append("Content-Type: text/plain; charset=" + ENCODING
							+ LINEND);
					sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
					sb.append(LINEND);
					sb.append(entry.getValue());
					sb.append(LINEND);
				}

				outStream.write(sb.toString().getBytes());
			}
			// 发送文件数据
			long bytesWritten = 0;
			if (files != null) {
				for (Map.Entry<String, File> file : files.entrySet()) {
					if (tag.isCanceled) {
						mProgressCallback.onCancel();
						outStream.close();
						conn.disconnect();
						return;
					}
					StringBuilder sb1 = new StringBuilder();
					sb1.append(PREFIX);
					sb1.append(BOUNDARY);
					sb1.append(LINEND);
					sb1.append("Content-Disposition: form-data; name=\""
							+ file.getKey() + "\"; filename=\""
							+ file.getValue().getName() + "\"" + LINEND);
					sb1.append("Content-Type: application/octet-stream; charset="
							+ ENCODING + LINEND);
					sb1.append(LINEND);
					outStream.write(sb1.toString().getBytes());

					InputStream is = new FileInputStream(file.getValue());
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = is.read(buffer)) != -1) {
						if (tag.isCanceled) {
							mProgressCallback.onCancel();
							is.close();
							outStream.close();
							conn.disconnect();
							return;
						}
						outStream.write(buffer, 0, len);
						//
						bytesWritten += len;
						float f = ((float) bytesWritten) / ((float) totalSize);
						int percent = (int) (f * 100);
						publishProgress(percent);
					}
					is.close();
					outStream.write(LINEND.getBytes());
				}
			}

			// 请求结束标志
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
			outStream.write(end_data);
			outStream.flush();

			if (conn.getResponseCode() == HttpStatus.SC_OK) {
				InputStream in = conn.getInputStream();
				ByteArrayOutputStream bos = new ByteArrayOutputStream();

				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = in.read(buffer)) != -1) {
					if (tag.isCanceled) {
						mProgressCallback.onCancel();
						in.close();
						bos.close();
						outStream.close();
						conn.disconnect();
						return;
					}
					bos.write(buffer, 0, len);
				}
				in.close();
				bos.flush();
				String ret = bos.toString();
				bos.close();
				publishProgress(100);
				mProgressCallback.onSuccess(ret);// 上传成功
			} else {// 上传失败
				mProgressCallback.onFail();
			}
		} catch (IOException e) {
			mProgressCallback.onFail();// 上传失败
			e.printStackTrace();
		}
		try {
			outStream.close();
			conn.disconnect();
		} catch (Exception e) {
			// ignore.
			e.printStackTrace();
		}
	}
}
