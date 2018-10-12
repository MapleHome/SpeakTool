package com.speaktool.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapScaleUtil {

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, long reqMemorySize) {
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(res, resId, options);
            options.inSampleSize = calculateInSampleSize(options, reqMemorySize);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeResource(res, resId, options);
        } catch (Error err) {// mem error.
            err.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isGif(String key) {
        return key.toLowerCase().endsWith(".gif");
    }

    /**
     * assume one pix use 4 bytes.Bitmap.Config.ARGB_8888.
     */
    private final static int calculateInSampleSize(BitmapFactory.Options options, long reqMemorySize) {
        final int onePixBytes = 4;
        int reqPixs = (int) (reqMemorySize / onePixBytes);
        final int height = options.outHeight;
        final int width = options.outWidth;
        int orgPixs = height * width;
        int inSampleSize = 1;
        while (orgPixs / Math.pow(inSampleSize, 2) > reqPixs) {
            inSampleSize *= 2;
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromPath(String localpath, long reqMemorySize) {
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(localpath, options);
            options.inSampleSize = calculateInSampleSize(options, reqMemorySize);
            options.inJustDecodeBounds = false;
            Bitmap bp = null;
            bp = BitmapFactory.decodeFile(localpath, options);
            return bp;
        } catch (Error err) {
            err.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
