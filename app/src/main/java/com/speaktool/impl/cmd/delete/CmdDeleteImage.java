package com.speaktool.impl.cmd.delete;

import com.speaktool.api.Draw;
import com.speaktool.api.Page;
import com.speaktool.bean.DeleteShapeData;
import com.speaktool.bean.ImageCommonData;
import com.speaktool.impl.cmd.CmdBase;
import com.speaktool.impl.cmd.ICmd;
import com.speaktool.impl.cmd.create.CmdCreateImage;

public class CmdDeleteImage extends CmdBase<DeleteShapeData> {
    private transient ImageCommonData olddata;

    public ImageCommonData getOlddata() {
        return olddata;
    }

    public void setOlddata(ImageCommonData olddata) {
        this.olddata = olddata;
    }

    public CmdDeleteImage() {
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
        CmdCreateImage cmd = new CmdCreateImage();
        cmd.setData(olddata);
        return cmd;
    }

    @Override
    public ICmd copy() {
        CmdDeleteImage copy = new CmdDeleteImage();
        copy.setData(getData());
        copy.setOlddata(getOlddata());
        copy.setTime(getTime());
        return copy;
    }

}
