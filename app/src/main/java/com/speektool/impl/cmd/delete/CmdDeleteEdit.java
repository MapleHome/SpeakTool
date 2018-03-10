package com.speektool.impl.cmd.delete;

import com.speektool.api.Draw;
import com.speektool.api.Page;
import com.speektool.bean.DeleteShapeData;
import com.speektool.bean.EditCommonData;
import com.speektool.impl.cmd.CmdBase;
import com.speektool.impl.cmd.ICmd;
import com.speektool.impl.cmd.create.CmdCreateEdit;

public class CmdDeleteEdit extends CmdBase<DeleteShapeData> {
	private transient EditCommonData olddata;

	public EditCommonData getOlddata() {
		return olddata;
	}

	public void setOlddata(EditCommonData olddata) {
		this.olddata = olddata;
	}

	public CmdDeleteEdit() {
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
		CmdCreateEdit cmd = new CmdCreateEdit();
		cmd.setData(olddata);
		return cmd;
	}

	@Override
	public ICmd copy() {
		CmdDeleteEdit copy = new CmdDeleteEdit();
		copy.setTime(getTime());
		copy.setData(getData());
		copy.setOlddata(getOlddata());
		return copy;
	}

}
