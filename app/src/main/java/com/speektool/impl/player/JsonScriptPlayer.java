package com.speektool.impl.player;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.speektool.Const;
import com.speektool.api.Draw;
import com.speektool.bean.LocalRecordBean;
import com.speektool.bean.TransformShapeData;
import com.speektool.busevents.PlayTimeChangedEvent;
import com.speektool.impl.cmd.ICmd;
import com.speektool.utils.RecordFileUtils;

import de.greenrobot.event.EventBus;

/**
 * JSON脚本播放器
 * 
 * @author Maple Shao
 * 
 */
@SuppressWarnings("rawtypes")
public class JsonScriptPlayer {
	private static final String TAG = JsonScriptPlayer.class.getSimpleName();

	private JsonScriptParser parser;
	private Draw draw;
	private MediaPlayer mSoundPlayer;

	private File recordDir;
	private File mJsonFile;
	private int mPlayDuration;

	private volatile boolean isExit = false;
	private volatile boolean isRequestStopPlayThread = false;
	private volatile boolean isSounFinish = true;
	private volatile boolean isPlayComplete = false;
	private volatile boolean isUserPlaying = false;

	public JsonScriptPlayer(LocalRecordBean rec, Draw draw) {
		super();
		Preconditions.checkNotNull(draw, "draw 不能为空.");
		Preconditions.checkNotNull(rec, "rec 不能为空.");
		String recordDirPath = rec.getRecordDir();
		Preconditions.checkArgument(!TextUtils.isEmpty(recordDirPath), "记录目录不能为空.");
		this.recordDir = new File(recordDirPath);
		Preconditions.checkArgument(recordDir.isDirectory() && recordDir.exists(), "recordDir is not correct.");
		draw.setRecordDir(recordDirPath);

		this.draw = draw;

		parser = new JsonScriptParser(draw.context(), RecordFileUtils.getScreenInfoFile(recordDir));
		//
		mJsonFile = new File(recordDir, Const.RELEASE_JSON_SCRIPT_NAME);
		File soundfile = new File(recordDir, Const.RELEASE_SOUND_NAME);
		if (!mJsonFile.exists() || !soundfile.exists()) {
			throw new IllegalArgumentException("播放文件不存在！");
		}

		mSoundPlayer = new MediaPlayer();
		mSoundPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mSoundPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				isSounFinish = true;
			}
		});
		try {
			mSoundPlayer.setDataSource(soundfile.getAbsolutePath());
			mSoundPlayer.prepare();
			mPlayDuration = mSoundPlayer.getDuration();
		} catch (Exception e) {
			e.printStackTrace();
		}
		startCmdPlayThread();
		startRefreshProgressUi();
	}

	private void showLoading() {
		// Intent it = new Intent(draw.context(), DialogActivity.class);
		// it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// draw.context().startActivity(it);
	}

	private void closeLoading() {
		// draw.postTaskToUiThread(new Runnable() {
		// @Override
		// public void run() {
		// Intent it = new Intent(DialogActivity.ACTION_CLOSE_DIALOG);
		// draw.context().sendBroadcast(it);
		// }
		// });

	}

	public void exitPlayer() {
		isExit = true;
		isSounFinish = true;
		isRequestStopPlayThread = true;

		mRefreshProgressTimer.cancel();
		mRefreshProgressTimer = null;

		mSoundPlayer.stop();
		mSoundPlayer.release();
		mSoundPlayer = null;

	}

	private Timer mRefreshProgressTimer;

	private void startRefreshProgressUi() {

		mRefreshProgressTimer = new Timer();
		mRefreshProgressTimer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				if (!isSounFinish) {
					long currentPosition = getPlayerCurrentPosition();
					EventBus.getDefault().post(new PlayTimeChangedEvent(currentPosition, mPlayDuration));
				} else {
					EventBus.getDefault().post(new PlayTimeChangedEvent(mPlayDuration, mPlayDuration));
				}
			}
		}, 500, 1000);
	}

	public void play() {
		play(-1);
	}

	private List<ICmd> orgCmds;

	private void play(final int seekPosition) {
		showLoading();
		runTask(new Runnable() {
			@Override
			public void run() {
				isUserPlaying = true;
				isRequestStopPlayThread = false;
				isPlayComplete = false;
				isSounFinish = false;
				//
				if (orgCmds == null) {
					orgCmds = parser.jsonFileToCmds(mJsonFile.getAbsolutePath());
				}
				List<ICmd> seekedCmds = getSeekedCmds(orgCmds, seekPosition);
				final int size = seekedCmds.size();
				draw.postTaskToUiThread(new Runnable() {
					@Override
					public void run() {
						draw.resetAllViews();
						draw.onPlayStart();
					}
				});

				for (int i = 0; i < size; i++) {
					ICmd cd = seekedCmds.get(i);
					long cmdOrgTime = cd.getTime();
					if (isRequestStopPlayThread)
						return;
					// sound cannot delete when rerecord.
					if (cmdOrgTime < seekPosition) {// seek>>>>>>>>>>>>>>>>>>>>>>>>>>
						cd.setTime(ICmd.TIME_DELETE_FLAG);
						ICmd copy = cd.copy();
						if (copy != null) {
							cd.setTime(cmdOrgTime);
							cd = copy;
						}
					}
					if (cd.getTime() == ICmd.TIME_DELETE_FLAG && mSoundPlayer.isPlaying()) {
						mSoundPlayer.pause();
					} else {
						if (!isSounFinish && cd.getTime() != ICmd.TIME_DELETE_FLAG && isUserPlaying)
							mSoundPlayer.start();
					}

					if (cd.getTime() != ICmd.TIME_DELETE_FLAG) {// normal cmd.
						draw.postTaskToUiThread(new Runnable() {
							@Override
							public void run() {
								closeLoading();
								draw.hideViewFlipperOverlay();
								draw.getCurrentBoard().refresh();
							}
						});
					}
					while (!isRequestStopPlayThread && !isSounFinish && getPlayerCurrentPosition() < cd.getTime()) {
						// 声音可能失败。
						if (isRequestStopPlayThread) {
							cd.setTime(cmdOrgTime);// 还原时间。
							return;
						}
						SystemClock.sleep(300);
					}// while end.
					cd.run(draw, null);
					cd.setTime(cmdOrgTime);// 还原时间。
				}//
				/***
				 * for end,all cmd finish.
				 */
				draw.postTaskToUiThread(new Runnable() {// 所有CMD完成后刷新
					@Override
					public void run() {
						closeLoading();
						draw.hideViewFlipperOverlay();
						draw.getCurrentBoard().refresh();
					}
				});

				if (!isSounFinish)
					mSoundPlayer.start();
				while (!isRequestStopPlayThread && !isSounFinish) {
					// wait sound.
					if (isRequestStopPlayThread)
						return;
					SystemClock.sleep(100);
				}
				if (isRequestStopPlayThread)
					return;
				EventBus.getDefault().post(new PlayTimeChangedEvent(mPlayDuration, mPlayDuration));
				isPlayComplete = true;
				isUserPlaying = false;
				isSounFinish = true;
				//
				draw.onPlayComplete();
			}
		});
	}

	private static List<ICmd> getSeekedCmds(List<ICmd> orgCmds, int seekPosition) {
		if (seekPosition <= 0)
			return orgCmds;
		List<ICmd> filteredCmds = Lists.newArrayList();
		Map<Integer, Boolean> transformCmd = Maps.newHashMap();
		for (int j = orgCmds.size() - 1; j >= 0; j--) {
			ICmd cmd = orgCmds.get(j);
			if (cmd.getTime() <= seekPosition) {// unrecord.
				// filter repeat transform cmds.
				if (cmd.getType().equals(ICmd.TYPE_TRANSFORM_SHAPE)) {
					TransformShapeData data = (TransformShapeData) cmd.getData();
					if (!transformCmd.containsKey(data.getShapeID())) {
						transformCmd.put(data.getShapeID(), true);
						filteredCmds.add(0, cmd);
					} else {
						Log.e(TAG, "repeat cmd>>>>>>>>>>>>>>>>>>>>>>");
					}
				} else {// not transform.
					filteredCmds.add(0, cmd);
				}
			} else {// record cmd.
				filteredCmds.add(0, cmd);
			}
		}// for cmds end.
		return filteredCmds;
	}

	private long getPlayerCurrentPosition() {
		if (isPlayComplete)
			return mPlayDuration;
		else
			return mSoundPlayer.getCurrentPosition();
	}

	private final BlockingDeque<Runnable> mLinkedBlockingDeque = new LinkedBlockingDeque<Runnable>(1);

	private void startCmdPlayThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!isExit) {
					try {
						Runnable tk = mLinkedBlockingDeque.take();// block.
						tk.run();
					} catch (InterruptedException e) {
						e.printStackTrace();
						continue;
					}
				}
			}
		}).start();
	}

	private void runTask(Runnable task) {
		try {
			mLinkedBlockingDeque.clear();
			mLinkedBlockingDeque.put(task);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void pause() {
		if (mSoundPlayer != null) {
			mSoundPlayer.pause();
		}
		isUserPlaying = false;
	}

	public void goOn() {
		if (mSoundPlayer != null) {
			mSoundPlayer.start();
		}
		isUserPlaying = true;
	}

	public boolean isPlaying() {
		return isUserPlaying;
	}

	public static final int MAX_PROGRESS = 1000;

	public void seekTo(final long position) {
		/**
		 * seekforward or seekback.
		 */
		isRequestStopPlayThread = true;
		final int positionTimeMills = (int) (((float) position / MAX_PROGRESS) * mPlayDuration);
		if (position > 0) {// seek.
			mSoundPlayer.seekTo(positionTimeMills);
		} else {// replay.
			mSoundPlayer.seekTo(0);
		}
		mSoundPlayer.pause();
		isUserPlaying = false;
		draw.removeAllHandlerTasks();
		draw.postTaskToUiThread(new Runnable() {
			@Override
			public void run() {
				draw.showViewFlipperOverlay();
				play((int) positionTimeMills);
			}
		});
	}

	public boolean isPlayComplete() {
		return isPlayComplete;
	}

	public void rePlay() {
		seekTo(0);
	}
}
