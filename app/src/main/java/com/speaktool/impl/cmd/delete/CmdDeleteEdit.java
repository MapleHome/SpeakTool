package com.speaktool.impl.cmd.delete;

import com.speaktool.api.Draw;
import com.speaktool.api.Page;
import com.speaktool.bean.DeleteShapeData;
import com.speaktool.bean.EditCommonData;
import com.speaktool.impl.cmd.CmdBase;
import com.speaktool.impl.cmd.ICmd;
import com.speaktool.impl.cmd.create.CmdCreateEdit;

public class CmdDeleteEdit extends CmdBase<DeleteShapeData> {
    private transient EditCommonData oldData;

    public void setOldData(EditCommonData olddata) {
        this.oldData = olddata;
    }

    public CmdDeleteEdit() {
        super();
        setType(TYPE_DELETE_SHAPE);
    }

    @Override
    public void run(final Draw draw, Page bw) {
        final DeleteShapeData data = getData();
        draw.postTaskToUiThread(new Runnable() {
            @Override
            public void run() {
                draw.getCurrentBoard().deleteShape(data.getShapeID());

            }
        });
    }

    @Override
    public ICmd inverse() {
        CmdCreateEdit cmd = new CmdCreateEdit();
        cmd.setData(oldData);
        return cmd;
    }

    @Override
    public ICmd copy() {
//        CmdDeleteEdit copy = new CmdDeleteEdit();
//        copy.setTime(getTime());
//        copy.setData(getData());
//        copy.setOldData(getOlddata());
        return this;
    }

}
