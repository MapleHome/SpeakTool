package com.speektool;

import java.io.File;

import android.content.Context;
import android.graphics.Point;
import android.os.Environment;

import com.ishare_lib.utils.DensityUtils;
import com.speektool.utils.DisplayUtil;

/**
 * 常量
 * 
 * @author shaoshuai
 * 
 */
public class Const {
	/** 在欢迎页面至少停留时间 */
	public static final long SplashMinTime = 2000;
	/** 是否第一次进入标记 */
	public static final String First_ComeIn = "isFirstComeIn";

	/** SD卡路径 */
	public static final String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
	/** 记录保存根路径 */
	public static final String RECORD_DIR = SD_PATH + "/.spktl/records/";
	/** 临时记录路径 */
	public static final String TEMP_DIR = SD_PATH + "/.spktl/tempdir/";
	/** 异常错误保存路径 */
	public static final String ERR_DIR = SD_PATH + "/.spktl/exception/";
	/** 下载路径 */
	public static final String DOWNLOAD_DIR = SD_PATH + "/.spktl/download/";

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

	public static final String CMD_FILE_SUFFIX = ".json";
	public static final String SOUND_FILE_SUFFIX = ".amr";
	public static final String REALEASE_SOUND_FILE_TYPE = "mp3";
	public static final String UN_RECORD_FILE_FLAG = "#";
	// 发布文件
	/** 文件特征信息 */
	public static final String INFO_FILE_NAME = "info.properties";
	/** 操作文本 */
	public static final String RELEASE_JSON_SCRIPT_NAME = "release.json";
	public static final String RELEASE_SOUND_NAME = "release.mp3";
	public static final String IMAGE_SUFFIX = ".jpg";
	public static final String GIF_SUFFIX = ".gif";

	public static final String RECORD_SOUNDLIST_TEXT = "soundlist.txt";
	public static final int SCRIPT_VERSION = 1;
	public static final int SCRIPT_INPUT_RATE = 60;

	/** 当前APP服务器API版本 */
	public static final String CURRENT_APPSERVER_API_VERSION = "1.1.0";
	/** 讲讲服务器地址 */
	public static final String SPEEKTOOL_SERVER__URL = "http://www.speaktool.com/";
	/** 讲讲服务器API地址 */
	public static final String SPEEKTOOL_SERVER_API_URL = "http://www.speaktool.com/api/";
	/** 讲讲论坛地址 */
	public static final String SPEEKTOOL_BBS_URL = "http://bbs.speaktool.com:8080/";

	/** Dialog最小宽度 */
	public static final int DIALOG_MIN_WIDTH = DensityUtils.dp2px(SpeekToolApp.app(), 600);// pix
	/** Dialog最小高度 */
	public static final int DIALOG_MIN_HEIGHT = DensityUtils.dp2px(SpeekToolApp.app(), 500);// pixv

	public static Point getDialogSize(Context context) {
		Point screen = DisplayUtil.getScreenSize(context);
		int stH = DisplayUtil.getStatusbarHeightPix(context);
		int w = screen.x > DIALOG_MIN_WIDTH ? DIALOG_MIN_WIDTH : screen.x - stH;
		int h = screen.y > DIALOG_MIN_HEIGHT ? DIALOG_MIN_HEIGHT : screen.y - stH;
		return new Point(w, h);
	}

	/** BMP图片最大可以分配内存 */
	public static final long MAX_MEMORY_BMP_CAN_ALLOCATE = 1 * 1024 * 1024;
	/** 上传课程 */
	public static final String COURSE_UPLOAD_URL = SPEEKTOOL_SERVER_API_URL + "uploadCourse.do";
	/** 课程列表 */
	public static final String COURSE_SEARCH_URL = SPEEKTOOL_SERVER_API_URL + "getCourseList.do";
	/** 删除课程 */
	public static final String COURSE_DELETE_URL = SPEEKTOOL_SERVER_API_URL + "deleteCourse.do";
	/** 课程类型列表 */
	public static final String COURSE_TYPES_URL = SPEEKTOOL_SERVER_API_URL + "getCategoryList.do";
	/** 最新版本 */
	public static final String UPDATE_URL = SPEEKTOOL_SERVER_API_URL + "getLatestVersion.do";
	/** 注册用户 */
	public static final String USER_REGISTER_URL = SPEEKTOOL_SERVER_API_URL + "addUser.do";
	/** 用户登陆 */
	public static final String USER_LOGIN_URL = SPEEKTOOL_SERVER_API_URL + "userLogin.do";
	/** 用户修改 */
	public static final String USER_MODIFY_URL = SPEEKTOOL_SERVER_API_URL + "modifyUser.do";
	/** 用户退出 */
	public static final String USER_LOGINOUT_URL = SPEEKTOOL_SERVER_API_URL + "userLogout.do";
	/** 搜索检查用户是否存在 */
	public static final String USER_CHECK_EXIST = SPEEKTOOL_SERVER_API_URL + "widgetUserSearch.do";
	/** 通过ID查询用户信息 */
	public static final String USER_GET_INFO_BY_UID = SPEEKTOOL_SERVER_API_URL + "searchByUserid.do";
	/** 上传活动异常 */
	public static final String EXCEPTION_UPLOAD_URL = SPEEKTOOL_SERVER_API_URL + "uploadMobileException.do";

	/** 获取魔灯网学URL */
	public static final String GET_THIRDPARTY_URL = "http://www.speaktool.com/api/getThirdPartyList.do";
	/** 音乐列表URL */
	public static final String MUSIC_LIST_URL = "http://www.speaktool.com/api/getMusicList.do";
	/** 音乐类型URL */
	public static final String MUSIC_CATEGORY_URL = "http://www.speaktool.com/api/getMusicCategory.do";
}
