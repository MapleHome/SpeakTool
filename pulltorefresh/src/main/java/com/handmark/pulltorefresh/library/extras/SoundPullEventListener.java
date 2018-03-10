/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.handmark.pulltorefresh.library.extras;

import java.util.HashMap;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.View;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;

public class SoundPullEventListener<V extends View> implements
		PullToRefreshBase.OnPullEventListener<V> {

	private final Context mContext;
	private final HashMap<State, Integer> mSoundMap;

	private MediaPlayer mCurrentMediaPlayer;

	public SoundPullEventListener(Context context) {
		mContext = context;
		mSoundMap = new HashMap<State, Integer>();
	}

	@Override
	public final void onPullEvent(PullToRefreshBase<V> refreshView,
			State event, Mode direction) {
		Integer soundResIdObj = mSoundMap.get(event);
		if (null != soundResIdObj) {
			playSound(soundResIdObj.intValue());
		}
	}

	/**
	 * 设置一个拉事件发生时要播放的声音。您指定的事件，通过调用此方法多次为每个事件的事件。
	 * <p/>
	 * 如果您已经设置了某个事件的声音，并为该事件添加另一个声音，则只会播放新的声音。
	 * 
	 * @param event
	 *            - 声音将被播放的事件.
	 * @param resId
	 *            - 播放声音文件的资源ID (e.g. <var>R.raw.pull_sound</var>)
	 */
	public void addSoundEvent(State event, int resId) {
		mSoundMap.put(event, resId);
	}

	/**
	 * 清除所有先前设置的声音和事件.
	 */
	public void clearSounds() {
		mSoundMap.clear();
	}

	/**
	 * 获取当前（或最后）MediaPlayer实例.
	 */
	public MediaPlayer getCurrentMediaPlayer() {
		return mCurrentMediaPlayer;
	}

	private void playSound(int resId) {
		// 如果有一个正在播放，停止当前的播放器
		if (null != mCurrentMediaPlayer) {
			mCurrentMediaPlayer.stop();
			mCurrentMediaPlayer.release();
		}

		mCurrentMediaPlayer = MediaPlayer.create(mContext, resId);
		if (null != mCurrentMediaPlayer) {
			mCurrentMediaPlayer.start();
		}
	}

}
