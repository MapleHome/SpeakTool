package com.speaktool.impl.dataloader;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.google.common.base.Preconditions;
import com.speaktool.Const;
import com.speaktool.bean.PicDataHolder;
import com.speaktool.busevents.NetPictureThumbnailLoadedEvent;
import com.speaktool.utils.BitmapScaleUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.greenrobot.event.EventBus;
import com.speaktool.ui.custom.gif.GifDrawable;

/**
 * 网络图片 异步加载
 *
 * @author shaoshuai
 */
public class NetPicturesIconAsyncLoader extends
        ListItemAsyncDataLoader<String, PicDataHolder> {

    public NetPicturesIconAsyncLoader() {
        super();
    }

    @Override
    protected int getSizeOfValueBytes(String key, PicDataHolder value) {
        if (isGif(key)) {
            return value.gif.length;
        } else {
            return value.bpScaled.length;
        }
    }

    private static boolean isGif(String key) {
        return BitmapScaleUtil.isGif(key);

    }

    @Override
    protected PicDataHolder getDataLogic(String key, Object... args) {
        Preconditions.checkNotNull(key);
        try {

            if (isGif(key)) {

                PicDataHolder data = new PicDataHolder();
                data.gif =
//                        UniversalHttp.downloadFile(key, "http://img4.duitang.com");
                        null;
                if (data.gif == null)
                    return null;
                return data;
            } else {

                PicDataHolder data = new PicDataHolder();
                byte[] bytesBig =
//                        UniversalHttp.downloadFile(key, "http://img4.duitang.com");// mem p.
                        null;
                if (bytesBig == null)
                    return null;
                Bitmap bmp = BitmapScaleUtil.decodeSampledBitmapFromByteArray(
                        bytesBig, Const.MAX_MEMORY_BMP_CAN_ALLOCATE);
                if (bmp == null)
                    return null;
                bytesBig = null;
                ByteArrayOutputStream outstream = new ByteArrayOutputStream(
                        bmp.getRowBytes() * bmp.getHeight());
                bmp.compress(CompressFormat.JPEG, 100, outstream);
                outstream.flush();

                data.bpScaled = outstream.toByteArray();
                outstream.close();
                if (data.bpScaled == null)
                    return null;
                return data;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void postFinishEvent(String key, PicDataHolder value, Object... args) {

        Drawable d = null;
        if (BitmapScaleUtil.isGif(key)) {
            try {
                d = new GifDrawable(value.gif);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Bitmap bpScaled = BitmapFactory.decodeByteArray(value.bpScaled, 0, value.bpScaled.length);
            d = new BitmapDrawable(bpScaled);
        }
        if (d != null)
            EventBus.getDefault().post(new NetPictureThumbnailLoadedEvent(key, d, false));
        else
            postErrorEvent(key, value);
    }

    @Override
    protected void postErrorEvent(String key, PicDataHolder value, Object... args) {
        EventBus.getDefault().post(new NetPictureThumbnailLoadedEvent(key, null, true));
    }

}
