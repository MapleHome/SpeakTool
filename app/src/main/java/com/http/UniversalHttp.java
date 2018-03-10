package com.http;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 * 通用Http
 * 
 * @author shaoshuai
 * 
 */
public class UniversalHttp {
	// 设置URLConnection的连接超时时间
	private final static int CONNET_TIMEOUT = 10 * 1000;
	// 设置URLConnection的读取超时时间
	private final static int READ_TIMEOUT = 10 * 1000;

	/**
	 * HTTP GET请求
	 * 
	 * @param url
	 *            请求链接
	 * @param params
	 *            HTTP GET请求的QueryString封装map集合
	 * @return null indicate reponse fail.
	 */
	public static String get(String url, Map<String, String> params) {
		try {
			String realUrl = generateUrl(url, params);
			HttpClient client = getNewHttpClient();
			HttpGet getMethod = new HttpGet(realUrl);
			HttpResponse response = client.execute(getMethod);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				StringBuilder builder = new StringBuilder();
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				for (String s = reader.readLine(); s != null; s = reader.readLine()) {
					builder.append(s);
				}
				String result = builder.toString();
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * HTTP POST请求
	 * 
	 * @param url
	 *            请求链接
	 * @param params
	 *            HTTP POST请求body的封装map集合
	 * @return null indicate reponse fail.
	 */
	public static String post(String url, Map<String, String> params) {
		try {
			HttpClient client = getNewHttpClient();
			HttpPost postMethod = new HttpPost(url);
			List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

			if (params != null && params.size() > 0) {
				Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<String, String> param = iterator.next();
					String key = param.getKey();
					String value = param.getValue();
					BasicNameValuePair pair = new BasicNameValuePair(key, value);
					pairs.add(pair);
				}
				postMethod.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
			}
			HttpResponse response = client.execute(postMethod);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return EntityUtils.toString(response.getEntity());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取HttpClient
	 * 
	 * @return
	 */
	private static HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
			SSLSocketFactory sf = new SSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			HttpConnectionParams.setConnectionTimeout(params, CONNET_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, READ_TIMEOUT);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));
			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	/**
	 * 
	 * @param url
	 *            HTTP GET请求的基础链接
	 * @param params
	 *            HTTP GET请求参数列表
	 * @return 拼接后url链接
	 */
	private static String generateUrl(String url, Map<String, String> params) {
		StringBuilder urlBuilder = new StringBuilder(url);
		if (null != params) {
			urlBuilder.append("?");
			Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, String> param = iterator.next();
				String key = param.getKey();
				String value = param.getValue();
				urlBuilder.append(key).append('=').append(value);
				if (iterator.hasNext()) {
					urlBuilder.append('&');
				}
			}
		}
		return urlBuilder.toString();
	}

	// 设置请求参数的字符编码格式
	// private final static String QUERY_ENCODING = "UTF-8";
	// 设置返回请求结果的字符编码格式
	private final static String ENCODING = "GBK";

	/**
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
	public static String post(String url, Map<String, String> params, Map<String, File> files) {
		HttpURLConnection conn = null;
		DataOutputStream outStream = null;
		try {
			String BOUNDARY = UUID.randomUUID().toString();
			String PREFIX = "--", LINEND = "\r\n";
			String MULTIPART_FROM_DATA = "multipart/form-data";

			URL uri = new URL(url);
			conn = (HttpURLConnection) uri.openConnection();
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
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);
			//
			outStream = new DataOutputStream(conn.getOutputStream());
			if (params != null) {
				// 首先组拼文本类型的参数
				StringBuilder sb = new StringBuilder();
				for (Entry<String, String> entry : params.entrySet()) {
					sb.append(PREFIX);
					sb.append(BOUNDARY);
					sb.append(LINEND);
					sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
					sb.append("Content-Type: text/plain; charset=" + ENCODING + LINEND);
					sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
					sb.append(LINEND);
					sb.append(entry.getValue());
					sb.append(LINEND);
				}

				outStream.write(sb.toString().getBytes());
			}
			// 发送文件数据
			if (files != null) {
				for (Entry<String, File> file : files.entrySet()) {
					StringBuilder sb1 = new StringBuilder();
					sb1.append(PREFIX);
					sb1.append(BOUNDARY);
					sb1.append(LINEND);
					sb1.append("Content-Disposition: form-data; name=\"" + file.getKey() + "\"; filename=\""
							+ file.getValue().getName() + "\"" + LINEND);
					sb1.append("Content-Type: application/octet-stream; charset=" + ENCODING + LINEND);
					sb1.append(LINEND);
					outStream.write(sb1.toString().getBytes());

					InputStream is = new FileInputStream(file.getValue());
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = is.read(buffer)) != -1) {
						outStream.write(buffer, 0, len);
					}
					is.close();
					outStream.write(LINEND.getBytes());
				}
			}
			// 请求结束标志
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
			outStream.write(end_data);
			outStream.flush();
			String sb2 = null;
			if (conn.getResponseCode() == HttpStatus.SC_OK) {
				InputStream in = conn.getInputStream();
				sb2 = readStreamToString(in);

			}

			return sb2;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {

			try {
				outStream.close();
				conn.disconnect();
			} catch (Exception e) {
				// ignore.
				e.printStackTrace();
			}
		}

	}

	/**
	 * 从输入流中获取数据
	 * 
	 * @param inStream
	 *            输入流
	 * @return
	 * @throws Exception
	 */
	private static String readStreamToString(InputStream inStream) {
		ByteArrayOutputStream outStream = null;
		try {
			outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			inStream.close();
			outStream.flush();
			String ret = outStream.toString();
			outStream.close();
			return ret;
		} catch (Exception e) {
			try {
				inStream.close();
				outStream.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return null;
		}
	}

	// ===========================================================
	private static void configConnection(URLConnection conn) {

		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setRequestProperty(
				"Accept",
				"image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
		conn.setRequestProperty("Accept-Language", "zh-CN");
		conn.setRequestProperty(
				"User-Agent",
				"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
		conn.setRequestProperty("Charset", "UTF-8");
		conn.setConnectTimeout(CONNET_TIMEOUT);
		conn.setReadTimeout(READ_TIMEOUT);
	}

	public static byte[] downloadFile(String urlstr, String referer) {
		try {
			URL url = new URL(urlstr);// cause speed low.
			URLConnection con = url.openConnection();
			configConnection(con);
			con.setRequestProperty("Referer", referer);
			con.setDoInput(true);
			con.connect();
			InputStream ins = con.getInputStream();
			byte[] buffer = new byte[10240];
			int len = -1;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			while ((len = ins.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}
			ins.close();
			bos.flush();
			byte[] ret = bos.toByteArray();
			bos.close();
			return ret;
		} catch (Error e) {

			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		}
		return null;
	}

	/**
	 * 下载文件
	 * 
	 * @param urlstr
	 *            - 源目录
	 * @param saveFile
	 *            - 保存目录
	 * @return
	 */
	public static File downloadFile(String urlstr, File saveFile) {
		return downloadFile(urlstr, "", saveFile);
	}

	public static File downloadFile(String urlstr, String referer, File saveFile) {
		try {
			URL url = new URL(urlstr);// cause speed low.
			URLConnection con = url.openConnection();
			configConnection(con);
			con.setRequestProperty("Referer", referer);
			con.setDoInput(true);
			con.connect();
			InputStream ins = con.getInputStream();
			final int bufsize = 102400;

			byte[] buffer = new byte[bufsize];
			int len = -1;
			FileOutputStream bos = new FileOutputStream(saveFile);
			while ((len = ins.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}
			ins.close();
			bos.close();
			return saveFile;
		} catch (Error e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
