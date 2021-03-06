package com.speaktool.impl.cmd.delete;

import com.speaktool.impl.api.BaseDraw;
import com.speaktool.impl.api.Page;
import com.speaktool.impl.bean.DeleteShapeData;
import com.speaktool.impl.cmd.CmdBase;
import com.speaktool.impl.cmd.ICmd;

/**
 * 删除笔记
 *
 * @author shaoshuai
 */
public class CmdDeletePen extends CmdBase<DeleteShapeData> {

    public CmdDeletePen() {
        super();
        setType(TYPE_DELETE_SHAPE);
    }

    @Override
    public void run(final BaseDraw draw, Page bw) {
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
        throw new UnsupportedOperationException();
    }

    @Override
    public ICmd copy() {
//        CmdDeletePen cd = new CmdDeletePen();
//        cd.setData(getData());
//        cd.setTime(getTime());
        return this;
    }

}
