package com.speaktool.view.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.speaktool.R;
import com.speaktool.api.Draw;
import com.speaktool.api.Page;
import com.speaktool.bean.RecordUploadBean;
import com.speaktool.utils.DeviceUtils;
import com.speaktool.utils.RecordFileUtils;
import com.speaktool.utils.T;
import com.speaktool.view.layouts.FillSaveRecordInfoEditPage;

import java.io.File;

/**
 * 填写保存信息
 *
 * @author shaoshuai
 */
public class FillSaveInfoDialog extends Dialog implements OnClickListener {
    private FillSaveRecordInfoEditPage firstPage;
    private Context mActivityContext;
    private Draw mDraw;

    public FillSaveInfoDialog(Context context, Draw draw) {
        this(context, R.style.dialogThemeFullScreen, draw);
    }

    public FillSaveInfoDialog(Context context, int theme, Draw draw) {
        super(context, theme);
        setCanceledOnTouchOutside(false);
        mActivityContext = context;
        mDraw = draw;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_fill_saveinfo);
        firstPage = findViewById(R.id.firstPage);

        resetLayout();
        // 预置的分类
        String[] nativeTypes = mActivityContext.getResources().getStringArray(R.array.native_recordTypes);
        firstPage.setType(nativeTypes[0]);// 默认类型
        //
        firstPage.setCancelClickListener(this);
        firstPage.setOkClickListener(this);
        firstPage.setEditTypeTouchListener(this);
    }

    private void resetLayout() {
        // 调整dialog背景大小
        int width = DeviceUtils.getScreenWidth(mActivityContext);
        int height = DeviceUtils.getScreenHeight(mActivityContext);
        if (DeviceUtils.isPad(mActivityContext)) {// 平板
            if (DeviceUtils.isHengPing(mActivityContext)) {// 横屏
                width = (int) (width * 0.5);
                height = (int) (height * 0.5);
            } else {// 竖屏
                width = (int) (width * 0.7);
                height = (int) (height * 0.5);
            }
        } else {// 手机
            if (DeviceUtils.isHengPing(mActivityContext)) {// 横屏
                width = (int) (width * 0.6);
                height = (int) (height * 0.85);
            } else {// 竖屏
                width = (int) (width * 0.85);
                height = (int) (height * 0.5);
            }
        }

        ViewGroup.LayoutParams lp1 = firstPage.getLayoutParams();
        lp1.height = height;
        lp1.width = width;
        firstPage.setLayoutParams(lp1);
    }

    @Override
    public void onBackPressed() {
        this.dismiss();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivCancel:// 信息页——关闭
                this.dismiss();
                break;
            case R.id.ivOk:// 信息页——完成
                onSaveOk();
                break;

        }
    }

    private void onSaveOk() {
        String title = firstPage.getTitle();
        if (TextUtils.isEmpty(title)) {
            T.showShort(mActivityContext, "标题不能为空");
            return;
        }
        this.dismiss();
        String tab = firstPage.getTab();
        String type = firstPage.getType();
        String intro = firstPage.getIntroduce();
        Bitmap thumb = getVideoThumbnail();
        String thumbNailName = RecordFileUtils.copyBitmapToRecordDir(thumb, mDraw.getRecordDir());

        boolean isPublicPublish = firstPage.isPublicPublish();
        RecordUploadBean info = new RecordUploadBean();
        info.setTitle(title);
        info.setTab(tab);
        info.setType(type);
        info.setIntroduce(intro);

        info.setThumbNailName(thumbNailName);
        String thumbNailPath = String.format("%s%s%s", mDraw.getRecordDir(), File.separator, thumbNailName);
        info.setThumbNailPath(thumbNailPath);
        info.setPublicPublish(isPublicPublish);

        mDraw.saveRecord(info);
    }

    /**
     * 获取视频缩略图
     */
    private Bitmap getVideoThumbnail() {
        Page firstpage = mDraw.getPageAtPosition(0);
        mDraw.setActivePageImpl(firstpage.getPageID());
        View v = firstpage.view();
        v.setDrawingCacheEnabled(true);// 启用绘图缓存
        v.buildDrawingCache();
        return v.getDrawingCache();
    }

}
