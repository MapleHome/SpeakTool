package com.speaktool.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Maple on 2018/4/7.
 */
public class FileIOUtils {

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

}
