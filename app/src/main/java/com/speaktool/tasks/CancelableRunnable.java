package com.speaktool.tasks;

/**
 * 可撤销线程
 * 
 * @author shaoshuai
 * 
 */
public abstract class CancelableRunnable implements Runnable {

	public static class Tag {
		public volatile boolean isCanceled = false;
	}

	protected Tag tag;

	public CancelableRunnable(Tag tag) {
		super();
		this.tag = tag;
	}

}
