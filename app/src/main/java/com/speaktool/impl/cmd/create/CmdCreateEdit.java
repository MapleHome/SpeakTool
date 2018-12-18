package com.speaktool.impl.cmd.create;

import com.speaktool.impl.api.BaseDraw;
import com.speaktool.api.Page;
import com.speaktool.impl.bean.DeleteShapeData;
import com.speaktool.impl.bean.EditCommonData;
import com.speaktool.impl.cmd.ICmd;
import com.speaktool.impl.cmd.delete.CmdDeleteEdit;
import com.speaktool.view.layouts.WordEdit;

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
	public void run(final BaseDraw draw, Page bw) {
		final EditCommonData data = getData();
		draw.postTaskToUiThread(new Runnable() {
			@Override
			public void run() {
				Page page = draw.getCurrentBoard();
				WordEdit edit = new WordEdit(draw.context(), draw, data.getShapeID());
				WordEdit.inflateDataToAttrs(data, edit);

				page.draw(edit);
				page.saveShape(edit);
			}
		});

	}

	@Override
	public ICmd<DeleteShapeData> inverse() {
		CmdDeleteEdit inverseCmd = new CmdDeleteEdit();
		inverseCmd.setData(new DeleteShapeData(getData().getShapeID()));
		return inverseCmd;
	}

	@Override
	public ICmd<EditCommonData> copy() {
//		CmdCreateEdit copy = new CmdCreateEdit();
//		copy.setTime(getTime());
//		copy.setData(getData());

		return this;
	}

}
