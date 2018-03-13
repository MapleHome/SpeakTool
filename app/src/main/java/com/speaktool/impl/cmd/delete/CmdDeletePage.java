package com.speaktool.impl.cmd.delete;

import com.speaktool.api.Draw;
import com.speaktool.api.Page;
import com.speaktool.bean.ActivePageData;
import com.speaktool.impl.cmd.CmdBase;
import com.speaktool.impl.cmd.ICmd;
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
