package com.ffmpeg;

import android.util.Log;

public class FFmpegNative {
	static {
		System.loadLibrary("ffmpeg_api");
	}

	public static final int FFMPEG_SUCCESS = 99;
	public static final int FFMPEG_FAIL = -99;

	public native static int ffmpegMain(String[] mainArgs);

	/**
	 * 将两个音频合并为一个音频
	 * 
	 * @param firstSoundpath
	 *            待合并音频
	 * @param secondSoundpath
	 *            待合并音频
	 * @param resultSoundpath
	 *            合成和的音频
	 * @return
	 */
	public static int audioMix(String firstSoundpath, String secondSoundpath, String resultSoundpath) {
		String[] args = new String[] { "ffmpeg", "-i", firstSoundpath, "-i", secondSoundpath, "-filter_complex",
				"amix=inputs=2:duration=first:dropout_transition=2", "-f", "mp3", "-y", resultSoundpath };

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			sb.append(args[i]);
		}
		Log.e("FFmpeg方法", "打印语句：" + sb.toString());
		return ffmpegMain(args);
	}

	/**
	 * 输入必须具有相同的格式，持续时间是最短的。
	 * 
	 * @param firstSoundpath
	 * @param secondSoundpath
	 * @param resultSoundpath
	 * @return
	 */
	@Deprecated
	public static int audioMerge(String firstSoundpath, String secondSoundpath, String resultSoundpath) {

		String[] args = new String[] { "ffmpeg", "-i", firstSoundpath, "-i", secondSoundpath, "-filter_complex",
				"amerge", "-c:a", "libmp3lame", "-q:a", "4", "-y", resultSoundpath };
		return ffmpegMain(args);
	}
}