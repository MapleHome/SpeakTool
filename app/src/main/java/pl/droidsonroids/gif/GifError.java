package pl.droidsonroids.gif;

import java.util.Locale;

/**
 * 在本机代码中发生错误的解码包。 三位码等于giflib错误代码。
 */
public enum GifError {
	// 匹配本地的错误代码常量
	/** 特殊值表示没有错误 */
	NO_ERROR(0, "No error"),
	/** 未能打开给定的输入 */
	OPEN_FAILED(101, "Failed to open given input"),
	/** 从给定的输入读取失败 */
	READ_FAILED(102, "Failed to read from given input"),
	/** 数据不在GIF格式 */
	NOT_GIF_FILE(103, "Data is not in GIF format"),
	/** 没有检测到屏幕的描述符 */
	NO_SCRN_DSCR(104, "No screen descriptor detected"),
	/** 没有检测到的图像描述符 */
	NO_IMAG_DSCR(105, "No image descriptor detected"),
	/** 无论是全局还是局部彩色地图 */
	NO_COLOR_MAP(106, "Neither global nor local color map found"),
	/** 错误记录类型检测 */
	WRONG_RECORD(107, "Wrong record type detected"),
	/** 像素大于宽度*高度 */
	DATA_TOO_BIG(108, "Number of pixels bigger than width * height"),
	/** 分配所需内存失败 */
	NOT_ENOUGH_MEM(109, "Failed to allocate required memory"),
	/** 未能关闭给定的输入 */
	CLOSE_FAILED(110, "Failed to close given input"),
	/** 给定文件未被打开以读取 */
	NOT_READABLE(111, "Given file was not opened for read"),
	/** 图像是有缺陷的，解码中止 */
	IMAGE_DEFECT(112, "Image is defective, decoding aborted"),
	/**
	 * Image EOF detected before image complete. EOF means GIF terminator, NOT
	 * end of input source.
	 */
	EOF_TOO_SOON(113, "Image EOF detected before image complete"),
	/** 没有找到帧，至少有一个帧的要求 */
	NO_FRAMES(1000, "No frames found, at least one frame required"),
	/** 无效屏幕尺寸，尺寸必须为正 */
	INVALID_SCR_DIMS(1001, "Invalid screen size, dimensions must be positive"),
	/** 无效的图像大小，尺寸必须是正 */
	INVALID_IMG_DIMS(1002, "Invalid image size, dimensions must be positive"),
	/** 图像大小超过屏幕大小 */
	IMG_NOT_CONFINED(1003, "Image size exceeds screen size"),
	/** 输入源倒带已经失败，动画停止 */
	REWIND_FAILED(1004, "Input source rewind has failed, animation is stopped"),
	/** 未知错误，不应该出现 */
	UNKNOWN(-1, "Unknown error");

	/** 人类可读的错误描述 */
	public final String description;
	private int errorCode;

	private GifError(int code, String description) {
		errorCode = code;
		this.description = description;
	}

	static GifError fromCode(int code) {
		for (GifError err : GifError.values())
			if (err.errorCode == code)
				return err;
		GifError unk = UNKNOWN;
		unk.errorCode = code;
		return unk;
	}

	/**
	 * 获取错误代码
	 */
	public int getErrorCode() {
		return errorCode;
	}

	/**
	 * 获取格式化错误描述
	 */
	protected String getFormattedDescription() {
		return String.format(Locale.US, "GifError %d: %s", errorCode, description);
	}
}