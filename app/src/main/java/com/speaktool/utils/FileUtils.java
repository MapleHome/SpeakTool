package com.speaktool.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 文件工具类
 *
 * @author maple
 * @time 2018/4/7
 */
public class FileUtils {

    public static void writeFile(File file, String text) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        if (file.canWrite()) {
            FileOutputStream fos = new FileOutputStream(file);
            byte[] bytes = text.getBytes();
            fos.write(bytes);
            fos.close();
        } else {
            new IOException("is no file and not write! ");
        }
    }

    public static String readFile(File file) throws IOException {
        String str = "";
        if (!file.exists()) {

        }
        return str;
    }

    public static void deleteDir(File dir) {
        if (dir == null || !dir.exists())
            return;
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File f : files)
                f.delete();
        }
        dir.delete();
    }
}
