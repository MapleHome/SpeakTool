package com.speaktool.impl.cmd.transform;

import android.graphics.Color;
import android.os.SystemClock;

import com.speaktool.api.Draw;
import com.speaktool.api.Page;
import com.speaktool.bean.ChangeEditData;
import com.speaktool.bean.EditCommonData;
import com.speaktool.bean.MoveData;
import com.speaktool.impl.cmd.ICmd;
import com.speaktool.view.layouts.WordEdit;

import java.util.List;

/**
 * 移动编辑框
 *
 * @author Maple Shao
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
                WordEdit edit = (WordEdit) draw.getCurrentBoard().shape(data.getShapeID());
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
                    WordEdit edit = (WordEdit) draw.getCurrentBoard().shape(data.getShapeID());
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
