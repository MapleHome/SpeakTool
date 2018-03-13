package com.speaktool.impl.cmd.create;

import com.speaktool.api.Draw;
import com.speaktool.api.Page;
import com.speaktool.bean.DeleteShapeData;
import com.speaktool.bean.EditCommonData;
import com.speaktool.impl.cmd.ICmd;
import com.speaktool.impl.cmd.delete.CmdDeleteEdit;
import com.speaktool.ui.layouts.WordEdit;

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
