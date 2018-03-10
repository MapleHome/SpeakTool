package com.speektool.impl.cmd.transform;

import java.util.List;

import android.os.SystemClock;

import com.speektool.api.Draw;
import com.speektool.api.Page;
import com.speektool.bean.ChangeImageData;
import com.speektool.bean.ImageCommonData;
import com.speektool.bean.ScaleData;
import com.speektool.impl.cmd.ICmd;
import com.speektool.ui.layouts.OuterImage;

/**
 * 缩放图片
 * 
 * @author Maple Shao
 * 
 */
public class CmdScaleImage extends CmdTransformSeqBase<ChangeImageData<ScaleData>> {
	public CmdScaleImage() {
		super();
	}

	//
	private long endTime;

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	private transient ImageCommonData olddata;

	public ImageCommonData getOlddata() {
		return olddata;
	}

	public void setOlddata(ImageCommonData olddata) {
		this.olddata = olddata;
	}

	// ----------------------------------------------------------------------

	@Override
	public void run(final Draw draw, Page bw) {
		final ChangeImageData<ScaleData> data = getData();
		List<ScaleData> seq = data.getSequence();

		long preT = 0;
		for (int i = 0; i < seq.size(); i++) {
			final ScaleData mv = seq.get(i);
			SystemClock.sleep(mv.getT() - preT);
			preT = mv.getT();

			draw.postTaskToUiThread(new Runnable() {

				@Override
				public void run() {
					final OuterImage edit = (OuterImage) draw.getCurrentBoard().shape(data.getShapeID());
					float scl = mv.getS();
					edit.setScaleX(scl);
					edit.setScaleY(scl);

					edit.setRotation(mv.getR());
					edit.setPosition(mv.getX(), mv.getY(), true);
				}
			});

		}
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
		copy.setType(getType());
		copy.setTime(getTime());
		//
		ChangeImageData<ScaleData> data = getData();
		ImageCommonData copydata = data.copy();

		copy.setData(copydata);
		copy.setOlddata(olddata);
		return copy;

	}

}
