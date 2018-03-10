package com.speektool.impl.cmd.transform;

import com.google.common.base.Preconditions;
import com.speektool.api.Draw;
import com.speektool.api.Page;
import com.speektool.bean.ImageCommonData;
import com.speektool.impl.cmd.CmdBase;
import com.speektool.impl.cmd.ICmd;
import com.speektool.ui.layouts.OuterImage;

public class CmdChangeImageNoSeq extends CmdBase<ImageCommonData> {

	private transient ImageCommonData olddata;

	public ImageCommonData getOlddata() {
		return olddata;
	}

	public void setOlddata(ImageCommonData olddata) {
		this.olddata = olddata;
	}

	public CmdChangeImageNoSeq() {
		super();
		setType(TYPE_TRANSFORM_SHAPE);
	}

	@Override
	public void run(final Draw draw, Page bw) {
		final ImageCommonData data = getData();
		Preconditions.checkNotNull(data);

		draw.postTaskToUiThread(new Runnable() {

			@Override
			public void run() {
				final OuterImage img = (OuterImage) draw.getCurrentBoard()
						.shape(data.getShapeID());

				OuterImage.inflateDataToAttrs(data, img);
			}
		});

	}

	@Override
	public ICmd inverse() {
		CmdChangeImageNoSeq undo = new CmdChangeImageNoSeq();
		undo.setData(olddata);
		return undo;
	}

	@Override
	public ICmd copy() {
		CmdChangeImageNoSeq copy = new CmdChangeImageNoSeq();
		copy.setTime(getTime());
		copy.setType(getType());
		copy.setData(getData());
		copy.setOlddata(olddata);
		return copy;
	}

}
