package com.speaktool.impl.dataloader;

import android.content.Context;
import android.graphics.Bitmap;

import com.speaktool.Const;
import com.speaktool.bean.ThirdParty;
import com.speaktool.busevents.ThirdpartyLoadEvent;
import com.speaktool.service.UseFactory;
import com.speaktool.service.AsyncDataLoaderFactory;
import com.speaktool.utils.BitmapScaleUtil;

import de.greenrobot.event.EventBus;

/**
 * 第三方条目 加载
 *
 * @author shaoshuai
 */
@UseFactory(AsyncDataLoaderFactory.class)
public class ThirdpartyItemLoader extends ListItemAsyncDataLoader<Object, Bitmap> {

    private Context mContext;

    public ThirdpartyItemLoader(Context mContext) {
        super();
        this.mContext = mContext.getApplicationContext();
    }

    @Override
    protected int getSizeOfValueBytes(Object key, Bitmap value) {
        return value.getRowBytes() * value.getHeight();
    }

    @Override
    protected Bitmap getDataLogic(Object key, Object... args) {
        int iconType = (Integer) args[0];
        if (iconType == ThirdParty.ICON_TYPE_RES) {
            int resid = (Integer) key;
            Bitmap ret = BitmapScaleUtil.decodeSampledBitmapFromResource(
                    mContext.getResources(), resid,
                    Const.MAX_MEMORY_BMP_CAN_ALLOCATE);
            return ret;
        } else if (iconType == ThirdParty.ICON_TYPE_NET) {
            String iconurl = (String) key;
            Bitmap ret = BitmapScaleUtil.decodeSampledBitmapFromUrl(iconurl,
                    Const.MAX_MEMORY_BMP_CAN_ALLOCATE, "");
            return ret;
        }

        return null;
    }

    @Override
    protected void postFinishEvent(Object key, Bitmap value, Object... args) {
        EventBus.getDefault().post(new ThirdpartyLoadEvent(key, value));

    }

    @Override
    protected void postErrorEvent(Object key, Bitmap value, Object... args) {
        // dont need error icon just use default icon.

    }

}
