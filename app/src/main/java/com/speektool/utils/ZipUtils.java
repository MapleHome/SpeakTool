package com.speektool.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.text.TextUtils;

/**
 * 负责解压相关的zip文件
 * 
 */
public class ZipUtils {
	private final static int DEFAULT_BUF_SIZE = 8192;

	private static final int BUFF_SIZE = 1024 * 1024; // 1M Byte

	/**
	 * 把 zipFilePath对应的zip文件 解压到 destDir目录下
	 * 
	 * @param destDir
	 * @throws IOException
	 * @throws ZipException
	 * @throws Exception
	 */
	public static void unZip(File zipBaseFile, String destDir) throws ZipException, IOException {
		// 创建zip文件对象,即包装上面的 file即可
		ZipFile zipFile = new ZipFile(zipBaseFile);
		// 得到zip文件条目枚举对象
		Enumeration<?> zipEnum = zipFile.entries();
		// 用输入输出流来完成 “解压到”功能
		InputStream input = null;
		OutputStream output = null;
		while (zipEnum.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) zipEnum.nextElement();
			// 得到当前条目
			String entryName = entry.getName();
			// Log.d("解压缩后的名字：", entryName);
			input = zipFile.getInputStream(entry);
			output = new FileOutputStream(new File(destDir, entryName));
			byte[] buffer = new byte[DEFAULT_BUF_SIZE];
			int readLen = 0;
			while ((readLen = input.read(buffer, 0, DEFAULT_BUF_SIZE)) != -1) {
				output.write(buffer, 0, readLen);
			}
			input.close();
			output.flush();
			output.close();
		}
	}

	public static void zipStream(String out_file, String input_file) throws IOException {
		final FileOutputStream zfos = new FileOutputStream(out_file, false);
		final ZipOutputStream zos = new ZipOutputStream(zfos);
		zos.setLevel(Deflater.BEST_COMPRESSION);

		final ZipEntry zipEntry = new ZipEntry(input_file);
		zos.putNextEntry(zipEntry);

		final FileInputStream zfis = new FileInputStream(input_file);

		int len = 0;
		final byte[] buffer = new byte[DEFAULT_BUF_SIZE];
		while ((len = zfis.read(buffer, 0, DEFAULT_BUF_SIZE)) != -1) {
			zos.write(buffer, 0, len);
		}

		zfis.close();
		zos.closeEntry();
		zos.close();
		zfos.close();

		// check zip, throw exception when zip failed
		ZipFile zf = new ZipFile(out_file);
		zf.close();
	}

	public static void zipStream(String out_file, String[] input_file_list) throws IOException {
		final FileOutputStream zfos = new FileOutputStream(out_file, false);
		final ZipOutputStream zos = new ZipOutputStream(zfos);
		zos.setLevel(Deflater.BEST_COMPRESSION);

		for (String input_file : input_file_list) {
			ZipEntry zipEntry = new ZipEntry(input_file);
			zos.putNextEntry(zipEntry);

			final FileInputStream zfis = new FileInputStream(input_file);

			int len = 0;
			final byte[] buffer = new byte[DEFAULT_BUF_SIZE];
			while ((len = zfis.read(buffer, 0, DEFAULT_BUF_SIZE)) != -1) {
				zos.write(buffer, 0, len);
			}

			zos.closeEntry();
			zfis.close();
		}

		/* the zos will close the zofs */
		zos.close();
		zfos.close();

		// check zip, throw exception when zip failed
		ZipFile zf = new ZipFile(out_file);
		zf.close();
	}

	/**
	 * 压缩文件
	 * 
	 * @param srcfile
	 *            File[] 需要压缩的文件列表
	 * @param zipfile
	 *            File 压缩后的文件
	 */
	public static void ZipFiles(File[] srcfile, File zipfile) {
		byte[] buf = new byte[1024];
		try {
			// Create the ZIP file
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
			out.setLevel(Deflater.BEST_COMPRESSION);
			// 压缩文件
			for (int i = 0; i < srcfile.length; i++) {
				if (srcfile[i].exists()) {
					FileInputStream in = new FileInputStream(srcfile[i]);
					// 添加压缩进入到输出流。
					out.putNextEntry(new ZipEntry(srcfile[i].getName()));
					// 从文件传输到压缩文件
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					// 完成输入
					out.closeEntry();
					in.close();
				}
			}
			// 完成压缩
			out.close();
			// 检查压缩，当压缩失败时抛出异常
			ZipFile zf = new ZipFile(zipfile);
			zf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加一些压缩相关接口
	 * 
	 * @author qyz date 2013.3.18
	 */

	/**
	 * 对字符串进行压缩
	 * 
	 * @param str
	 *            目标字符串
	 * @return 压缩后的字符串
	 * @throws IOException
	 */
	public static String compressString(String str) throws IOException {
		if (TextUtils.isEmpty(str))
			return str;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(str.getBytes());
		gzip.close();
		return out.toString("iso-8859-1");
	}

	/**
	 * 对目标字符串进行解压缩
	 * 
	 * @param str
	 *            目标字符串
	 * @return 解压缩后的字符串
	 * @throws IOException
	 */
	public static String decompressString(String str) throws IOException {
		if (TextUtils.isEmpty(str)) {
			return str;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("iso-8859-1"));
		GZIPInputStream gzipIs = new GZIPInputStream(in);
		byte[] buffer = new byte[1024];
		int len;
		while ((len = gzipIs.read(buffer)) > 0) {
			out.write(buffer, 0, len);
		}
		gzipIs.close();
		return out.toString();
	}

	/**
	 * 压缩字符串为 byte[] 储存可以使用new sun.misc.BASE64Encoder().encodeBuffer(byte[] b)方法
	 * 保存为字符串
	 * 
	 * @param str
	 *            压缩前的文本
	 * @return
	 */

	public static final byte[] compress(String str) {
		if (TextUtils.isEmpty(str)) {
			return null;
		}
		byte[] compressed = null;
		ByteArrayOutputStream out = null;
		ZipOutputStream zout = null;

		try {
			out = new ByteArrayOutputStream();
			zout = new ZipOutputStream(out);
			zout.putNextEntry(new ZipEntry("0"));
			zout.write(str.getBytes());
			zout.closeEntry();
		} catch (IOException e) {
			compressed = null;
		} finally {
			try {
				if (zout != null)
					zout.close();
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (out != null)
			compressed = out.toByteArray();
		return compressed;
	}

	/**
	 * 将压缩后的 byte[] 数据解压缩
	 * 
	 * @param compressed
	 *            压缩后的 byte[] 数据
	 * @return 解压后的字符串
	 */
	public static final String decompress(byte[] compressed) {
		if (compressed == null) {
			return null;
		}
		ByteArrayOutputStream out = null;
		ByteArrayInputStream in = null;
		ZipInputStream zin = null;
		String decompressed = null;
		try {
			out = new ByteArrayOutputStream();
			in = new ByteArrayInputStream(compressed);
			zin = new ZipInputStream(in);
			zin.getNextEntry();
			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = zin.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
		} catch (IOException e) {
			decompressed = null;
		} finally {
			try {
				if (zin != null) {
					zin.close();
				}
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (out != null)
			decompressed = out.toString();
		return decompressed;
	}

	/**
	 * 对文件或文件夹进行压缩
	 * 
	 * @param sourceFile
	 *            目标文件或文件夹
	 * @param destinationFile
	 *            压缩后的文件
	 */
	public static boolean compressFile(String sourceFile, String destinationFile) {
		File srcFile = new File(sourceFile);
		if (!srcFile.exists() || srcFile.isDirectory()) {
			return false;
		}
		File destFile = new File(destinationFile);
		if (!destFile.exists() || (destFile.exists() && destFile.isFile())) {
			destFile.mkdirs();
		}
		File[] files = srcFile.listFiles();
		List<File> fileList = Arrays.asList(files);
		try {
			zipFiles(fileList, destFile);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 对目标文件进行解压缩
	 * 
	 * @param sourceFile
	 *            目标文件
	 * @param destinationFile
	 *            解压缩后的文件或文件夹
	 */
	public static boolean decompressFile(String sourceFile, String destinationFile) {
		File srcFile = new File(sourceFile);
		if (!srcFile.exists() || srcFile.isDirectory()) {
			return false;
		}
		File destFile = new File(destinationFile);
		if (!destFile.exists() || destFile.isFile()) {
			destFile.mkdirs();
		}
		try {
			upZipFile(srcFile, destFile.getAbsolutePath());
			return true;
		} catch (ZipException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 批量压缩文件（夹）
	 * 
	 * @param resFileList
	 *            要压缩的文件（夹）列表
	 * @param zipFile
	 *            生成的压缩文件
	 * @throws IOException
	 *             当压缩过程出错时抛出
	 */
	public static void zipFiles(Collection<File> resFileList, File zipFile) throws IOException {
		ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile), BUFF_SIZE));
		for (File resFile : resFileList) {
			zipFile(resFile, zipout, "");
		}
		zipout.close();
		// check zip, throw exception when zip failed
		ZipFile zf = new ZipFile(zipFile);
		zf.close();
	}

	/**
	 * 批量压缩文件（夹）
	 * 
	 * @param resFileList
	 *            要压缩的文件（夹）列表
	 * @param zipFile
	 *            生成的压缩文件
	 * @param comment
	 *            压缩文件的注释
	 * @throws IOException
	 *             当压缩过程出错时抛出
	 */
	public static void zipFiles(Collection<File> resFileList, File zipFile, String comment) throws IOException {
		ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile), BUFF_SIZE));
		for (File resFile : resFileList) {
			zipFile(resFile, zipout, "");
		}
		zipout.setComment(comment);
		zipout.close();

		// check zip, throw exception when zip failed
		ZipFile zf = new ZipFile(zipFile);
		zf.close();
	}

	/**
	 * 解压缩一个文件
	 * 
	 * @param zipFile
	 *            压缩文件
	 * @param folderPath
	 *            解压缩的目标目录
	 * @throws IOException
	 *             当解压缩过程出错时抛出
	 */
	public static void upZipFile(File zipFile, String folderPath) throws ZipException, IOException {
		File desDir = new File(folderPath);
		if (!desDir.exists()) {
			desDir.mkdirs();
		}
		ZipFile zf = new ZipFile(zipFile);
		for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
			ZipEntry entry = ((ZipEntry) entries.nextElement());
			InputStream in = zf.getInputStream(entry);
			String str = folderPath + File.separator + entry.getName();
			str = new String(str.getBytes("8859_1"), "GB2312");
			File desFile = new File(str);
			if (!desFile.exists()) {
				File fileParentDir = desFile.getParentFile();
				if (!fileParentDir.exists()) {
					fileParentDir.mkdirs();
				}
				desFile.createNewFile();
			}
			OutputStream out = new FileOutputStream(desFile);
			byte buffer[] = new byte[BUFF_SIZE];
			int realLength;
			while ((realLength = in.read(buffer)) > 0) {
				out.write(buffer, 0, realLength);
			}
			in.close();
			out.close();
		}
	}

	/**
	 * 解压文件名包含传入文字的文件
	 * 
	 * @param zipFile
	 *            压缩文件
	 * @param folderPath
	 *            目标文件夹
	 * @param nameContains
	 *            传入的文件匹配名
	 * @throws ZipException
	 *             压缩格式有误时抛出
	 * @throws IOException
	 *             IO错误时抛出
	 */

	public static ArrayList<File> upZipSelectedFile(File zipFile, String folderPath, String nameContains)
			throws ZipException, IOException {
		ArrayList<File> fileList = new ArrayList<File>();

		File desDir = new File(folderPath);
		if (!desDir.exists()) {
			desDir.mkdir();
		}

		ZipFile zf = new ZipFile(zipFile);
		for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
			ZipEntry entry = ((ZipEntry) entries.nextElement());
			if (entry.getName().contains(nameContains)) {
				InputStream in = zf.getInputStream(entry);
				String str = folderPath + File.separator + entry.getName();
				str = new String(str.getBytes("8859_1"), "GB2312");
				// str.getBytes("GB2312"),"8859_1" 输出
				// str.getBytes("8859_1"),"GB2312" 输入
				File desFile = new File(str);
				if (!desFile.exists()) {
					File fileParentDir = desFile.getParentFile();
					if (!fileParentDir.exists()) {
						fileParentDir.mkdirs();
					}
					desFile.createNewFile();
				}
				OutputStream out = new FileOutputStream(desFile);
				byte buffer[] = new byte[BUFF_SIZE];
				int realLength;
				while ((realLength = in.read(buffer)) > 0) {
					out.write(buffer, 0, realLength);
				}
				in.close();
				out.close();
				fileList.add(desFile);
			}
		}
		return fileList;
	}

	/**
	 * 获得压缩文件内文件列表
	 * 
	 * @param zipFile
	 *            压缩文件
	 * @return 压缩文件内文件名称
	 * @throws ZipException
	 *             压缩文件格式有误时抛出
	 * @throws IOException
	 *             当解压缩过程出错时抛出
	 */

	public static ArrayList<String> getEntriesNames(File zipFile) throws ZipException, IOException {
		ArrayList<String> entryNames = new ArrayList<String>();
		Enumeration<?> entries = getEntriesEnumeration(zipFile);
		while (entries.hasMoreElements()) {
			ZipEntry entry = ((ZipEntry) entries.nextElement());
			entryNames.add(new String(getEntryName(entry).getBytes("GB2312"), "8859_1"));
		}
		return entryNames;
	}

	/**
	 * 获得压缩文件内压缩文件对象以取得其属性
	 * 
	 * @param zipFile
	 *            压缩文件
	 * @return 返回一个压缩文件列表
	 * @throws ZipException
	 *             压缩文件格式有误时抛出
	 * @throws IOException
	 *             IO操作有误时抛出
	 */
	public static Enumeration<?> getEntriesEnumeration(File zipFile) throws ZipException, IOException {
		ZipFile zf = new ZipFile(zipFile);
		return zf.entries();

	}

	/**
	 * 取得压缩文件对象的注释
	 * 
	 * @param entry
	 *            压缩文件对象
	 * @return 压缩文件对象的注释
	 * @throws UnsupportedEncodingException
	 */

	public static String getEntryComment(ZipEntry entry) throws UnsupportedEncodingException {
		return new String(entry.getComment().getBytes("GB2312"), "8859_1");
	}

	/**
	 * 取得压缩文件对象的名称
	 * 
	 * @param entry
	 *            压缩文件对象
	 * @return 压缩文件对象的名称
	 * @throws UnsupportedEncodingException
	 */

	public static String getEntryName(ZipEntry entry) throws UnsupportedEncodingException {
		return new String(entry.getName().getBytes("GB2312"), "8859_1");
	}

	/**
	 * 压缩文件
	 * 
	 * @param resFile
	 *            需要压缩的文件（夹）
	 * @param zipout
	 *            压缩的目的文件
	 * @param rootpath
	 *            压缩的文件路径
	 * @throws FileNotFoundException
	 *             找不到文件时抛出
	 * @throws IOException
	 *             当压缩过程出错时抛出
	 */
	private static void zipFile(File resFile, ZipOutputStream zipout, String rootpath) throws FileNotFoundException,
			IOException {
		rootpath = rootpath + (rootpath.trim().length() == 0 ? "" : File.separator) + resFile.getName();
		rootpath = new String(rootpath.getBytes("8859_1"), "GB2312");
		if (resFile.isDirectory()) {
			File[] fileList = resFile.listFiles();
			for (File file : fileList) {
				zipFile(file, zipout, rootpath);
			}
		} else {
			byte buffer[] = new byte[BUFF_SIZE];
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(resFile), BUFF_SIZE);
			zipout.putNextEntry(new ZipEntry(rootpath));
			int realLength;
			while ((realLength = in.read(buffer)) != -1) {
				zipout.write(buffer, 0, realLength);
			}
			in.close();
			zipout.flush();
			zipout.closeEntry();
		}
	}

}
