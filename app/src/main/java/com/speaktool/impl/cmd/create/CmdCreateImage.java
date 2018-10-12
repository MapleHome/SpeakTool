package com.speaktool.impl.cmd.create;

import com.speaktool.api.Draw;
import com.speaktool.api.Page;
import com.speaktool.bean.DeleteShapeData;
import com.speaktool.bean.ImageCommonData;
import com.speaktool.impl.cmd.ICmd;
import com.speaktool.impl.cmd.delete.CmdDeleteImage;
import com.speaktool.view.layouts.OuterImage;

/**
 * 创建图片
 * 
 * @author shaoshuai
 * 
 */
public class CmdCreateImage extends CmdCreateShape<ImageCommonData> {

	public CmdCreateImage() {
		super();
	}

	@Override
	public void run(final Draw draw, Page bw) {

		final ImageCommonData data = getData();

		draw.postTaskToUiThread(new Runnable() {
			@Override
			public void run() {
				Page page = draw.getCurrentBoard();
				OuterImage img = new OuterImage(draw.context(), draw,
						data.getShapeID());
				OuterImage.inflateDataToAttrs(data, img);
				page.draw(img);
				page.saveShape(img);
			}
		});
	}

	@Override
	public ICmd<DeleteShapeData> inverse() {
		CmdDeleteImage inverseCmd = new CmdDeleteImage();

		DeleteShapeData data = new DeleteShapeData();
		data.setShapeID(getData().getShapeID());
		inverseCmd.setData(data);
		return inverseCmd;
	}

	@Override
	public ICmd<ImageCommonData> copy() {
		CmdCreateImage copy = new CmdCreateImage();
		copy.setTime(getTime());
		copy.setData(getData());

		return copy;
	}

}
