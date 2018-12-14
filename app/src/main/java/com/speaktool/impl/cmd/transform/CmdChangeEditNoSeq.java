package com.speaktool.impl.cmd.transform;

import com.speaktool.api.Draw;
import com.speaktool.api.Page;
import com.speaktool.bean.EditCommonData;
import com.speaktool.impl.cmd.CmdBase;
import com.speaktool.impl.cmd.ICmd;
import com.speaktool.view.layouts.WordEdit;

public class CmdChangeEditNoSeq extends CmdBase<EditCommonData> {
    private transient EditCommonData oldData;

    public void setOldData(EditCommonData oldData) {
        this.oldData = oldData;
    }

    public CmdChangeEditNoSeq() {
        super();
        setType(TYPE_TRANSFORM_SHAPE);
    }

    @Override
    public void run(final Draw draw, Page bw) {
        final EditCommonData data = getData();
        draw.postTaskToUiThread(new Runnable() {

            @Override
            public void run() {
                WordEdit edit = (WordEdit) draw.getCurrentBoard().shape(data.getShapeID());
                WordEdit.inflateDataToAttrs(data, edit);
            }
        });

    }

    @Override
    public ICmd inverse() {
        CmdChangeEditNoSeq undo = new CmdChangeEditNoSeq();
        undo.setData(oldData);
        return undo;
    }

    @Override
    public ICmd copy() {
//        CmdChangeEditNoSeq copy = new CmdChangeEditNoSeq();
//        copy.setTime(getTime());
//        copy.setType(getType());
//        copy.setData(getData());
//        copy.setOldData(oldData);
        return this;
    }

}
