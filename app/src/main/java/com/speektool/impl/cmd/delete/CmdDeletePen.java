package com.speektool.impl.cmd.delete;

import com.speektool.api.Draw;
import com.speektool.api.Page;
import com.speektool.bean.DeleteShapeData;
import com.speektool.impl.cmd.CmdBase;
import com.speektool.impl.cmd.ICmd;

/**
 * 删除笔记
 * 
 * @author shaoshuai
 * 
 */
public class CmdDeletePen extends CmdBase<DeleteShapeData> {

	public CmdDeletePen() {
		super();
		setType(TYPE_DELETE_SHAPE);
	}

	@Override
	public void run(final Draw draw, Page bw) {
		final DeleteShapeData data = getData();
		draw.postTaskToUiThread(new Runnable() {
			@Override
			public void run() {
				draw.getCurrentBoard().deleteShape(data.getShapeID());
			}
		});

	}

	@Override
	public ICmd inverse() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ICmd copy() {
		CmdDeletePen cd = new CmdDeletePen();
		cd.setData(getData());
		cd.setTime(getTime());
		return cd;
	}

}
