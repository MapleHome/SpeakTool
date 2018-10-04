package com.speaktool.impl.cmd.transform;

import com.google.common.base.Preconditions;
import com.speaktool.api.Draw;
import com.speaktool.api.Page;
import com.speaktool.bean.ImageCommonData;
import com.speaktool.impl.cmd.CmdBase;
import com.speaktool.impl.cmd.ICmd;
import com.speaktool.view.layouts.OuterImage;

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
