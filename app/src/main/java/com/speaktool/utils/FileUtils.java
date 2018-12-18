package com.speaktool.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
            // 字符流写入
            FileWriter writer = new FileWriter(file);
            writer.write(text);
            writer.close();
            // 字节流写入
//            FileOutputStream fos = new FileOutputStream(file);
//            fos.write(text.getBytes());
//            fos.close();
        } else {
            new IOException("is no file and not write! ");
        }
    }

    public static String readFile(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (file.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
        }
        return sb.toString();
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
