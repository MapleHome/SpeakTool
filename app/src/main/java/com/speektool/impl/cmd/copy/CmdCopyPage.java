package com.speektool.impl.cmd.copy;

import com.speektool.api.Draw;
import com.speektool.api.Page;
import com.speektool.bean.CopyPageData;
import com.speektool.impl.cmd.CmdBase;
import com.speektool.impl.cmd.ICmd;

/**
 * 界面清除-复制命令
 * 
 * @author shaoshuai
 * 
 */
public class CmdCopyPage extends CmdBase<CopyPageData> {

	public CmdCopyPage() {
		super();
		setType(TYPE_COPY_PAGE);
	}

	@Override
	public void run(final Draw draw, Page board) {
		draw.postTaskToUiThread(new Runnable() {
			@Override
			public void run() {
				draw.copyPageImpl(getData().getSrcPageId(), getData()
						.getDestPageId(), getData().getOption());
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
