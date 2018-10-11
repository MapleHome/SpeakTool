package com.speaktool.impl.dataloader;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import com.speaktool.Const;
import com.speaktool.busevents.LocalPhotoDirIconLoadedEvent;
import com.speaktool.utils.BitmapScaleUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;


public class LocalPhotoDirLoader extends ListItemAsyncDataLoader<String, byte[]> {

    @Override
    protected int getSizeOfValueBytes(String key, byte[] value) {
        return value.length;
    }

    @Override
    protected byte[] getDataLogic(String key, Object... args) {
        try {
            Bitmap bmp = BitmapScaleUtil.decodeSampledBitmapFromPath(key, Const.MAX_MEMORY_BMP_CAN_ALLOCATE);
            if (bmp == null)
                return null;

            ByteArrayOutputStream outstream = new ByteArrayOutputStream(bmp.getRowBytes() * bmp.getHeight());
            bmp.compress(CompressFormat.JPEG, 100, outstream);
            outstream.flush();

            byte[] bpScaled = outstream.toByteArray();
            outstream.close();
            if (bpScaled == null)
                return null;

            return bpScaled;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void postFinishEvent(String key, byte[] value, Object... args) {
        Bitmap bpScaled = BitmapFactory.decodeByteArray(value, 0, value.length);
        // 通过EventBus订阅者发送消息
        EventBus.getDefault().post(new LocalPhotoDirIconLoadedEvent(key, bpScaled));

    }

    @Override
    protected void postErrorEvent(String key, byte[] value, Object... args) {
    }
}
