package com.speektool.impl.cmd.transform;

import com.speektool.api.Draw;
import com.speektool.api.Page;
import com.speektool.bean.PageBackgroundData;
import com.speektool.impl.cmd.CmdBase;
import com.speektool.impl.cmd.ICmd;

/**
 * 改变背景
 * 
 * @author Maple Shao
 * 
 */
public class CmdChangePageBackground extends CmdBase<PageBackgroundData> {

	public CmdChangePageBackground() {
		super();
		setType(TYPE_CHANGE_PAGE_BACKGROUND);
	}

	@Override
	public void run(final Draw draw, Page board) {
		draw.postTaskToUiThread(new Runnable() {

			@Override
			public void run() {
				draw.setPageBackgroundImpl(getData().getPageID(), getData().getBackgroundType());
			}
		});

	}

	@Override
	public ICmd inverse() {
		return null;
	}

	@Override
	public ICmd copy() {
		return null;
	}

}
