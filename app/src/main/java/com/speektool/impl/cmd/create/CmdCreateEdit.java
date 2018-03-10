package com.speektool.impl.cmd.create;

import com.speektool.api.Draw;
import com.speektool.api.Page;
import com.speektool.bean.DeleteShapeData;
import com.speektool.bean.EditCommonData;
import com.speektool.impl.cmd.ICmd;
import com.speektool.impl.cmd.delete.CmdDeleteEdit;
import com.speektool.ui.layouts.WordEdit;

/**
 * 创建编辑框
 * 
 * @author shaoshuai
 * 
 */
public class CmdCreateEdit extends CmdCreateShape<EditCommonData> {

	public CmdCreateEdit() {
		super();
	}

	@Override
	public void run(final Draw draw, Page bw) {
		final EditCommonData data = getData();
		draw.postTaskToUiThread(new Runnable() {
			@Override
			public void run() {
				Page page = draw.getCurrentBoard();
				WordEdit edit = new WordEdit(draw.context(), draw, data
						.getShapeID());

				WordEdit.inflateDataToAttrs(data, edit);

				page.draw(edit);
				page.saveShape(edit);
			}
		});

	}

	@Override
	public ICmd<DeleteShapeData> inverse() {
		DeleteShapeData inversedata = new DeleteShapeData();
		EditCommonData createdata = getData();
		
		CmdDeleteEdit inverseCmd = new CmdDeleteEdit();
		inversedata.setShapeID(createdata.getShapeID());
		inverseCmd.setData(inversedata);
		return inverseCmd;
	}

	@Override
	public ICmd<EditCommonData> copy() {
		CmdCreateEdit copy = new CmdCreateEdit();
		copy.setTime(getTime());
		copy.setData(getData());

		return copy;
	}

}
