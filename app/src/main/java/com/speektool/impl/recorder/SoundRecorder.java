package com.speektool.impl.recorder;

import java.io.IOException;

import android.media.MediaRecorder;
import android.util.Log;

import com.speektool.impl.cmd.ICmd;

/**
 * 录音机
 * 
 * @author shaoshuai
 * 
 */
public class SoundRecorder {
	private static final String tag = SoundRecorder.class.getSimpleName();
	private MediaRecorder mRecorder;
	private static RecordWorldTime refreshUiTime = new RecordWorldTime(true, true);
	private static RecordWorldTime logicTime = new RecordWorldTime(false, true);

	public SoundRecorder() {
		super();
		mRecorder = new MediaRecorder();
		mRecorder.setAudioChannels(2);
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		if (refreshUiTime == null)
			refreshUiTime = new RecordWorldTime(true, true);
		if (logicTime == null)
			logicTime = new RecordWorldTime(false, true);

	}

	public void startRecord(String outputFile) {
		mRecorder.setOutputFile(outputFile);
		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e(tag, "prepare() failed");
		}
		mRecorder.start();
		if (!refreshUiTime.isBooted()) {
			refreshUiTime.boot(0);
		} else if (!refreshUiTime.isTicking()) {
			refreshUiTime.goOn();
		}
		//
		if (!logicTime.isBooted()) {
			logicTime.boot(0);
		} else if (!logicTime.isTicking()) {
			logicTime.goOn();
		}
	}

	public static void resetRefreshUiTime(long time) {

		refreshUiTime.setNowTime(time);
	}

	public static long getRefreshUiTime() {
		if (refreshUiTime == null) {
			return ICmd.TIME_DELETE_FLAG;
		}
		return refreshUiTime.now();

	}

	public void destroy() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
		refreshUiTime.pause();
		logicTime.pause();

	}

	public static long getCurrentTime() {
		if (logicTime == null)
			return ICmd.TIME_DELETE_FLAG;
		return logicTime.now();
	}

	public static void closeWorldTimer() {// do at drawactivity finish.
		if (refreshUiTime != null) {

			refreshUiTime.stop();
			refreshUiTime = null;
		}
		//
		if (logicTime != null) {

			logicTime.stop();
			logicTime = null;
		}
	}
}
