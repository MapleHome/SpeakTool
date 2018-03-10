package com.speektool.impl.cmd.delete;

import com.speektool.api.Draw;
import com.speektool.api.Page;
import com.speektool.bean.ActivePageData;
import com.speektool.impl.cmd.CmdBase;
import com.speektool.impl.cmd.ICmd;
/**
 * 删除界面
 * @author shaoshuai
 *
 */
public class CmdDeletePage extends CmdBase<ActivePageData> {
	public CmdDeletePage() {
		super();
		setType(TYPE_DELETE_PAGE);
	}

	@Override
	public void run(final Draw draw, Page bw) {
		draw.postTaskToUiThread(new Runnable() {
			@Override
			public void run() {
				draw.deletePageImpl(getData().getPageID());
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
