package com.speaktool.impl.cmd.transform;

import android.os.SystemClock;

import com.speaktool.impl.api.BaseDraw;
import com.speaktool.api.Page;
import com.speaktool.impl.bean.ChangeImageData;
import com.speaktool.impl.bean.ImageCommonData;
import com.speaktool.impl.bean.ScaleData;
import com.speaktool.impl.cmd.ICmd;
import com.speaktool.view.layouts.OuterImage;

import java.util.List;

/**
 * 缩放图片
 *
 * @author Maple Shao
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
    public void run(final BaseDraw draw, Page bw) {
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
                    OuterImage edit = (OuterImage) draw.getCurrentBoard().shape(data.getShapeID());
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
        copy.setOldData(olddata);
        return copy;

    }

}
