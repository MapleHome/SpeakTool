package com.speektool.impl.cmd.transform;

import java.util.List;

import android.graphics.Color;
import android.os.SystemClock;

import com.speektool.api.Draw;
import com.speektool.api.Page;
import com.speektool.bean.ChangeEditData;
import com.speektool.bean.EditCommonData;
import com.speektool.bean.MoveData;
import com.speektool.impl.cmd.ICmd;
import com.speektool.ui.layouts.WordEdit;

/**
 * 移动编辑框
 * 
 * @author Maple Shao
 * 
 */
public class CmdMoveEdit extends CmdTransformSeqBase<ChangeEditData<MoveData>> {
	public CmdMoveEdit() {
		super();
	}

	private long endTime;

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	private transient EditCommonData olddata;

	public EditCommonData getOlddata() {
		return olddata;
	}

	public void setOlddata(EditCommonData olddata) {
		this.olddata = olddata;
	}

	// ----------------------------------------------------------------------

	@Override
	public void run(final Draw draw, Page bw) {
		final ChangeEditData<MoveData> data = getData();
		List<MoveData> seq = data.getSequence();

		draw.postTaskToUiThread(new Runnable() {

			@Override
			public void run() {
				final WordEdit edit = (WordEdit) draw.getCurrentBoard().shape(data.getShapeID());
				edit.setAlpha(data.getAlpha());
				edit.setRotation(data.getRotation());
				edit.setText(data.getText());
				edit.setTextColor(Color.parseColor(data.getColor()));
			}
		});

		long preT = 0;

		for (int i = 0; i < seq.size(); i++) {
			final MoveData mv = seq.get(i);
			SystemClock.sleep(mv.getT() - preT);
			preT = mv.getT();
			draw.postTaskToUiThread(new Runnable() {

				@Override
				public void run() {
					final WordEdit edit = (WordEdit) draw.getCurrentBoard().shape(data.getShapeID());
					edit.setPosition(mv.getX(), mv.getY(), true);
				}
			});

		}
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
		copy.setType(getType());
		copy.setTime(getTime());
		//
		ChangeEditData<MoveData> data = getData();
		EditCommonData copydata = data.copy();

		copy.setData(copydata);
		copy.setOlddata(olddata);
		return copy;

	}

}
