package com.speektool.impl.cmd.clear;

import com.speektool.api.Draw;
import com.speektool.api.Page;
import com.speektool.bean.ClearPageData;
import com.speektool.impl.cmd.CmdBase;
import com.speektool.impl.cmd.ICmd;

/**
 * 界面清除-操作命令
 * 
 * @author shaoshuai
 * 
 */
public class CmdClearPage extends CmdBase<ClearPageData> {

	public CmdClearPage() {
		super();
		setType(TYPE_CLEAR_PAGE);
	}

	@Override
	public void run(final Draw draw, Page board) {
		draw.postTaskToUiThread(new Runnable() {
			@Override
			public void run() {
				draw.clearPageImpl(getData().getPageId(), getData().getOption());
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
