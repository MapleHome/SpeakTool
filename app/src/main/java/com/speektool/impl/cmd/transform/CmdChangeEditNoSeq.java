package com.speektool.impl.cmd.transform;

import com.google.common.base.Preconditions;
import com.speektool.api.Draw;
import com.speektool.api.Page;
import com.speektool.bean.EditCommonData;
import com.speektool.impl.cmd.CmdBase;
import com.speektool.impl.cmd.ICmd;
import com.speektool.ui.layouts.WordEdit;

public class CmdChangeEditNoSeq extends CmdBase<EditCommonData> {

	private transient EditCommonData olddata;

	public EditCommonData getOlddata() {
		return olddata;
	}

	public void setOlddata(EditCommonData olddata) {
		this.olddata = olddata;
	}

	public CmdChangeEditNoSeq() {
		super();
		setType(TYPE_TRANSFORM_SHAPE);
	}

	@Override
	public void run(final Draw draw, Page bw) {
		final EditCommonData data = getData();
		Preconditions.checkNotNull(data);

		draw.postTaskToUiThread(new Runnable() {

			@Override
			public void run() {
				final WordEdit edit = (WordEdit) draw.getCurrentBoard().shape(
						data.getShapeID());

				WordEdit.inflateDataToAttrs(data, edit);
			}
		});

	}

	@Override
	public ICmd inverse() {
		CmdChangeEditNoSeq undo = new CmdChangeEditNoSeq();
		undo.setData(olddata);
		return undo;
	}

	@Override
	public ICmd copy() {
		CmdChangeEditNoSeq copy = new CmdChangeEditNoSeq();
		copy.setTime(getTime());
		copy.setType(getType());
		copy.setData(getData());
		copy.setOlddata(olddata);
		return copy;
	}

}
