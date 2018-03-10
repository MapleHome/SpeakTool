package com.speektool.impl.cmd.create;

import com.speektool.api.Draw;
import com.speektool.api.Page;
import com.speektool.bean.ActivePageData;
import com.speektool.impl.cmd.CmdBase;
import com.speektool.impl.cmd.ICmd;

/**
 * 互动页面
 * 
 * @author shaoshuai
 * 
 */
public class CmdActivePage extends CmdBase<ActivePageData> {

	public CmdActivePage() {
		super();
		setType(TYPE_SET_ACTIVE_PAGE);
	}

	@Override
	public void run(final Draw draw, Page board) {
		final int pageId = getData().getPageID();
		draw.postTaskToUiThread(new Runnable() {
			@Override
			public void run() {
				draw.setActivePageImpl(pageId);
			}
		});
	}

	@Override
	public ICmd inverse() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ICmd copy() {
		return null;
	}

}
