package com.speaktool.ffmpeg;

/**
 * 自定义异常
 * 
 * @author shaoshuai
 * 
 */
public class FFmpegException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FFmpegException() {
		super();
	}

	public FFmpegException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public FFmpegException(String detailMessage) {
		super(detailMessage);
	}

	public FFmpegException(Throwable throwable) {
		super(throwable);
	}

}
