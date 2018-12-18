package com.speaktool.impl.cmd.transform;

import com.speaktool.impl.api.BaseDraw;
import com.speaktool.api.Page;
import com.speaktool.impl.bean.ImageCommonData;
import com.speaktool.impl.cmd.CmdBase;
import com.speaktool.impl.cmd.ICmd;
import com.speaktool.view.layouts.OuterImage;

public class CmdChangeImageNoSeq extends CmdBase<ImageCommonData> {
    private transient ImageCommonData oldData;

    public void setOldData(ImageCommonData oldData) {
        this.oldData = oldData;
    }

    public CmdChangeImageNoSeq() {
        super();
        setType(TYPE_TRANSFORM_SHAPE);
    }

    @Override
    public void run(final BaseDraw draw, Page bw) {
        final ImageCommonData data = getData();

        draw.postTaskToUiThread(new Runnable() {

            @Override
            public void run() {
                OuterImage img = (OuterImage) draw.getCurrentBoard().shape(data.getShapeID());
                OuterImage.inflateDataToAttrs(data, img);
            }
        });

    }

    @Override
    public ICmd inverse() {
        CmdChangeImageNoSeq undo = new CmdChangeImageNoSeq();
        undo.setData(oldData);
        return undo;
    }

    @Override
    public ICmd copy() {
//        CmdChangeImageNoSeq copy = new CmdChangeImageNoSeq();
//        copy.setTime(getTime());
//        copy.setType(getType());
//        copy.setData(getData());
//        copy.setOldData(oldData);
        return this;
    }

}
