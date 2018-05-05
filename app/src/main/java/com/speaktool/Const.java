package com.speaktool;

import android.os.Environment;

import java.io.File;

/**
 * 常量
 *
 * @author shaoshuai
 */
public class Const {
    // 在欢迎页面至少停留时间
    public static final long SplashMinTime = 2000;
    // 是否第一次进入标记
    public static final String First_ComeIn = "isFirstComeIn";

    // SD卡路径
    public static final String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    // 记录保存根路径
    public static final String RECORD_DIR = SD_PATH + "/spktl/records/";
    // 临时记录路径
    public static final String TEMP_DIR = SD_PATH + "/spktl/tempdir/";
    // 异常错误保存路径
    public static final String ERR_DIR = SD_PATH + "/spktl/exception/";
    // 下载路径
    public static final String DOWNLOAD_DIR = SD_PATH + "/spktl/download/";

    static {
        File baseRecordDir = new File(RECORD_DIR);
        // 如果不存在，则创建这个目录
        if (!baseRecordDir.exists())
            baseRecordDir.mkdirs();

        File tempdir = new File(TEMP_DIR);
        if (!tempdir.exists())
            tempdir.mkdirs();
        File expdir = new File(ERR_DIR);
        if (!expdir.exists())
            expdir.mkdirs();
        File downloadDir = new File(DOWNLOAD_DIR);
        if (!downloadDir.exists())
            downloadDir.mkdirs();
    }

    public static final String CMD_FILE_SUFFIX = ".txt";
    public static final String SOUND_FILE_SUFFIX = ".amr";
    public static final String REALEASE_SOUND_FILE_TYPE = "mp3";
    public static final String UN_RECORD_FILE_FLAG = "#";
    // 发布文件
    public static final String INFO_FILE_NAME = "info.properties";
    public static final String RELEASE_JSON_SCRIPT_NAME = "release.json";
    public static final String RELEASE_SOUND_NAME = "release.mp3";
    public static final String IMAGE_SUFFIX = ".jpg";
    public static final String GIF_SUFFIX = ".gif";

    public static final String RECORD_SOUNDLIST_TEXT = "soundlist.txt";
    public static final int SCRIPT_VERSION = 1;
    public static final int SCRIPT_INPUT_RATE = 60;

    //讲讲服务器地址
    public static final String SPEEKTOOL_SERVER__URL = "http://www.speaktool.com/";
    //讲讲服务器API地址
    public static final String SPEEKTOOL_SERVER_API_URL = "http://www.speaktool.com/api/";
    //讲讲论坛地址
    public static final String SPEEKTOOL_BBS_URL = "http://bbs.speaktool.com:8080/";


    //BMP图片最大可以分配内存
    public static final long MAX_MEMORY_BMP_CAN_ALLOCATE = 1 * 1024 * 1024;
    //上传课程
    public static final String COURSE_UPLOAD_URL = SPEEKTOOL_SERVER_API_URL + "uploadCourse.do";

}
