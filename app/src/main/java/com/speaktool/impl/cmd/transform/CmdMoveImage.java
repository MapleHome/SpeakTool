package com.speaktool.impl.cmd.transform;

import android.os.SystemClock;

import com.speaktool.api.Draw;
import com.speaktool.api.Page;
import com.speaktool.bean.ChangeImageData;
import com.speaktool.bean.ImageCommonData;
import com.speaktool.bean.MoveData;
import com.speaktool.impl.cmd.ICmd;
import com.speaktool.view.layouts.OuterImage;

import java.util.List;

/**
 * 移动图片
 *
 * @author Maple Shao
 */
public class CmdMoveImage extends CmdTransformSeqBase<ChangeImageData<MoveData>> {
    public CmdMoveImage() {
        super();

    }

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
        final ChangeImageData<MoveData> data = getData();
        List<MoveData> seq = data.getSequence();

        long preT = 0;

        for (int i = 0; i < seq.size(); i++) {
            final MoveData mv = seq.get(i);
            SystemClock.sleep(mv.getT() - preT);
            preT = mv.getT();

            draw.postTaskToUiThread(new Runnable() {

                @Override
                public void run() {
                    OuterImage edit = (OuterImage) draw.getCurrentBoard().shape(data.getShapeID());
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
        ChangeImageData<MoveData> data = getData();
        ImageCommonData copydata = data.copy();

        copy.setData(copydata);
        copy.setOldData(olddata);
        return copy;

    }

}
