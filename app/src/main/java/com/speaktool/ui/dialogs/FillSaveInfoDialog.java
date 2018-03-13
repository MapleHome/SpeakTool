package com.speaktool.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ViewFlipper;

import com.google.common.base.Preconditions;
import com.speaktool.R;
import com.speaktool.SpeakToolApp;
import com.speaktool.ui.adapters.AdapterRecordTypes;
import com.speaktool.api.Draw;
import com.speaktool.api.Page;
import com.speaktool.bean.RecordUploadBean;
import com.speaktool.bean.SearchCategoryBean;
import com.speaktool.busevents.RecordTypeChangedEvent;
import com.speaktool.dao.RecordCategoriesDatabase;
import com.speaktool.tasks.MyThreadFactory;
import com.speaktool.tasks.TaskLoadRecordCategories;
import com.speaktool.tasks.TaskLoadRecordCategories.RecordTypeLoadListener;
import com.speaktool.ui.custom.swipemenu.SwipeMenu;
import com.speaktool.ui.custom.swipemenu.SwipeMenuCreator;
import com.speaktool.ui.custom.swipemenu.SwipeMenuItem;
import com.speaktool.ui.custom.swipemenu.SwipeMenuListView;
import com.speaktool.ui.layouts.FillSaveRecordInfoAddKindPage;
import com.speaktool.ui.layouts.FillSaveRecordInfoEditPage;
import com.speaktool.ui.layouts.FillSaveRecordInfoKindListPage;
import com.speaktool.utils.DensityUtils;
import com.speaktool.utils.DeviceUtils;
import com.speaktool.utils.RecordFileUtils;
import com.speaktool.utils.T;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.greenrobot.event.EventBus;

/**
 * 填写保存信息
 *
 * @author shaoshuai
 */
public class FillSaveInfoDialog extends Dialog implements OnClickListener, RecordTypeLoadListener, OnDismissListener,
        OnItemClickListener {
    private ViewFlipper viewFlipper;
    /**
     * 填写记录信息页面
     */
    private FillSaveRecordInfoEditPage firstPage;
    /**
     * 选择分类列表页面
     */
    private FillSaveRecordInfoKindListPage secondPage;
    /**
     * 新增分类页面
     */
    private FillSaveRecordInfoAddKindPage thirdPage;

    private Context mActivityContext;
    private AdapterRecordTypes mAdapterRecordTypes;
    private Draw mDraw;

    private static final int FIRST_PAGE = 0;
    private static final int SECOND_PAGE = 1;
    private static final int THIRD_PAGE = 2;
    private static final int SWIPE_MENU_ITEM_WIDTH = DensityUtils.dp2px(SpeakToolApp.app(), 90);
    private ExecutorService singleExecutor = Executors.newSingleThreadExecutor(new MyThreadFactory(
            "getRecordTypesThread"));

    public FillSaveInfoDialog(Context context, Draw draw) {
        this(context, R.style.dialogThemeFullScreen, draw);
    }

    public FillSaveInfoDialog(Context context, int theme, Draw draw) {
        super(context, theme);
        Preconditions.checkArgument(context instanceof Activity, "context must be Activity in Dialog.");
        mActivityContext = context;
        mDraw = draw;
        init();
    }

    private void init() {
        this.setCanceledOnTouchOutside(false);
        this.setOnDismissListener(this);
        initAnim(mActivityContext);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_fill_saveinfo);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        firstPage = (FillSaveRecordInfoEditPage) findViewById(R.id.firstPage);
        secondPage = (FillSaveRecordInfoKindListPage) findViewById(R.id.secondPage);
        thirdPage = (FillSaveRecordInfoAddKindPage) findViewById(R.id.thirdPage);

        resetLayout();
        // 预置的分类
        String[] nativeTypes = mActivityContext.getResources().getStringArray(R.array.native_recordTypes);
        firstPage.setType(nativeTypes[0]);// 默认类型
        //
        firstPage.setCancelClickListener(this);
        firstPage.setOkClickListener(this);
        firstPage.setEditTypeTouchListener(this);
        //
        secondPage.setBackClickListener(this);
        secondPage.setNewTypeClickListener(this);
        secondPage.setListItemClickListener(this);
        //
        thirdPage.setBackClickListener(this);
        thirdPage.setAddNewTypeClickListener(this);

        mAdapterRecordTypes = new AdapterRecordTypes(mActivityContext, null);
        secondPage.setAdapter(mAdapterRecordTypes);
        // set menu item.
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create item
                SwipeMenuItem modifyItem = new SwipeMenuItem(mActivityContext);
                modifyItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                modifyItem.setWidth(SWIPE_MENU_ITEM_WIDTH);
                modifyItem.setTitle("修改");
                modifyItem.setTitleSize(mActivityContext.getResources().getDimensionPixelSize(
                        R.dimen.dialog_swipe_menu_item_textSize));
                modifyItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(modifyItem);
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(mActivityContext);
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                deleteItem.setWidth(SWIPE_MENU_ITEM_WIDTH);//
                deleteItem.setIcon(R.drawable.draw_garbage_normal);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        secondPage.getListView().setMenuCreator(creator);
        // step 2. listener item click event
        secondPage.getListView().setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0: {
                        SearchCategoryBean type = (SearchCategoryBean) mAdapterRecordTypes.getItem(position);
                        RenameRecordTypeAlertDialog dia = new RenameRecordTypeAlertDialog(mActivityContext, type);
                        dia.show();
                    }
                    break;
                    case 1: {
                        SearchCategoryBean type = (SearchCategoryBean) mAdapterRecordTypes.getItem(position);
                        RecordCategoriesDatabase.deleteCategory(type, getContext());
                    }
                    break;
                }
            }
        });
        // drive.
        singleExecutor.execute(new TaskLoadRecordCategories(this, false));
        super.onCreate(savedInstanceState);
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

        ViewGroup.LayoutParams lp1 = viewFlipper.getLayoutParams();
        lp1.height = height;
        lp1.width = width;
        viewFlipper.setLayoutParams(lp1);
    }

    @Override
    public void onBackPressed() {
        this.dismiss();
    }

    int befpageId = FIRST_PAGE;

    private void displayPage(int id) {
        if (id > befpageId)
            setIntoAnimation();
        else if (id < befpageId)
            setBackAnimation();
        viewFlipper.setDisplayedChild(id);
        befpageId = id;
    }

    private Animation intoAnim_in;
    private Animation intoAnim_out;
    private Animation backAnim_in;
    private Animation backAnim_out;

    private void initAnim(Context context) {
        intoAnim_in = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        intoAnim_out = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right);
        backAnim_in = AnimationUtils.loadAnimation(context, R.anim.viewfliper_slide_in_right);
        backAnim_out = AnimationUtils.loadAnimation(context, R.anim.viewfliper_slide_out_left);
    }

    private void setIntoAnimation() {
        viewFlipper.setInAnimation(backAnim_in);
        viewFlipper.setOutAnimation(backAnim_out);
    }

    private void setBackAnimation() {
        viewFlipper.setInAnimation(intoAnim_in);
        viewFlipper.setOutAnimation(intoAnim_out);
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
            case R.id.tv_type:// 信息页——分类
                displayPage(SECOND_PAGE);
                break;
            case R.id.ivBackSecond:// 分类页——返回
                displayPage(FIRST_PAGE);
                break;
            case R.id.ivNewType:// 分类页——创建新分类
                displayPage(THIRD_PAGE);
                break;
            case R.id.ivBackThird:// 创建分类-返回
                displayPage(SECOND_PAGE);
                break;
            case R.id.ivAddNewType:// 创建分类-创建
                String inputtype = thirdPage.getInputNewType();
                if (TextUtils.isEmpty(inputtype)) {
                    T.showShort(mActivityContext, "输入分类不能为空！");
                    break;
                }
                // add newtype to local.
                SearchCategoryBean type = new SearchCategoryBean();
                type.setCategoryName(inputtype);
                //
                boolean isexist = RecordCategoriesDatabase.isCategoryExist(type, mActivityContext);
                if (isexist) {
                    T.showShort(mActivityContext, "目录已经存在！");
                    break;
                }
                RecordCategoriesDatabase.addCategory(type, mActivityContext);
                displayPage(SECOND_PAGE);
                break;
        }
    }

    /**
     * same thread
     */
    public void onEvent(RecordTypeChangedEvent event) {
        singleExecutor.execute(new TaskLoadRecordCategories(this, false));
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

    @Override
    public void onRecordTypeLoaded(List<SearchCategoryBean> result) {
        mAdapterRecordTypes.refresh(result);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        singleExecutor.shutdownNow();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SearchCategoryBean type = (SearchCategoryBean) parent.getAdapter().getItem(position);
        firstPage.setType(type.getCategoryName());
        displayPage(FIRST_PAGE);
    }

}
