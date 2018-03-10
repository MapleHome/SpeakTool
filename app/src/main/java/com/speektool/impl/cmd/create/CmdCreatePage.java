package com.speektool.impl.cmd.create;

import com.speektool.api.Draw;
import com.speektool.api.Page;
import com.speektool.bean.CreatePageData;
import com.speektool.impl.cmd.CmdBase;
import com.speektool.impl.cmd.ICmd;

/**
 * 创建画纸界面
 * 
 * @author shaoshuai
 * 
 */
public class CmdCreatePage extends CmdBase<CreatePageData> {

	public CmdCreatePage() {
		super();
		setType(TYPE_CREATE_PAGE);
	}

	@Override
	public void run(final Draw draw, Page bw) {
		draw.postTaskToUiThread(new Runnable() {
			@Override
			public void run() {
				draw.createPageImpl(getData().getBackgroundType(), getData().getPosition(), getData().getPageID());
			}
		});

	}
	// 实现接口 - 反选
	@Override
	public ICmd inverse() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ICmd copy() {
		return null;
	}

}
