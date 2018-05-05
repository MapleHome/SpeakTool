package com.speaktool.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.speaktool.Const;
import com.speaktool.R;
import com.speaktool.api.Draw.PlayMode;
import com.speaktool.bean.SearchCategoryBean;
import com.speaktool.busevents.CourseThumbnailLoadedEvent;
import com.speaktool.busevents.RefreshCourseListEvent;
import com.speaktool.tasks.TaskLoadRecordCategories;
import com.speaktool.tasks.TaskLoadRecordCategories.RecordTypeLoadListener;
import com.speaktool.tasks.ThreadPoolWrapper;
import com.speaktool.ui.base.BasePopupWindow.WeiZhi;
import com.speaktool.ui.fragment.HomePage;
import com.speaktool.ui.layouts.ItemViewLocalRecord;
import com.speaktool.ui.layouts.SearchView;
import com.speaktool.ui.popupwindow.CategoryPoW;
import com.speaktool.ui.popupwindow.CategoryPoW.SearchCategoryChangedListener;
import com.speaktool.utils.FileIOUtils;
import com.speaktool.utils.T;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * 主界面
 *
 * @author shaoshuai
 */
public class MainActivity extends FragmentActivity implements OnClickListener {
    @BindView(R.id.tvMakeVideo) TextView tvMakeVideo;// 新建按钮
    @BindView(R.id.iv_back) ImageView iv_back;// 返回
    @BindView(R.id.searchView) SearchView searchView;// 整个搜索框
    @BindView(R.id.ivSetting) ImageView ivSetting;// 设置按钮

    private HomePage mHomePage;
    private Context mContext;
    public SearchCategoryBean mCurSearchType;
    public String mCurSearchKeyWords = null;

    private ThreadPoolWrapper singleExecutor = ThreadPoolWrapper.newThreadPool(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = this;

        initView();
        initData();
        initListener();

    }

    private void initView() {
        // 注册EventBus订阅者
        EventBus.getDefault().register(this);

        // 填充视图
        mHomePage = new HomePage();
        loadView(mHomePage);
    }

    private void initData() {
        // 搜索框
        setSearchView(new SearchCategoryBean(0, "全部分类", SearchCategoryBean.CID_ALL), null);
    }

    /**
     * 更新搜索框
     */
    public void setSearchView(SearchCategoryBean mSearchType, String mSearchKeyWords) {
        if (TextUtils.isEmpty(mSearchKeyWords)) {// 显示全部
            tvMakeVideo.setVisibility(View.VISIBLE);
            iv_back.setVisibility(View.INVISIBLE);
        } else {// 显示搜索的部分
            iv_back.setVisibility(View.VISIBLE);
            tvMakeVideo.setVisibility(View.INVISIBLE);
        }

        searchView.setCategory(mSearchType);// 设置搜索框
        searchView.setSearchKey(mSearchKeyWords);

        mCurSearchType = mSearchType;
        mCurSearchKeyWords = mSearchKeyWords;
    }

    private void initListener() {
        tvMakeVideo.setOnClickListener(this);// 新建
        iv_back.setOnClickListener(this);// 返回
        ivSetting.setOnClickListener(this);// 设置
        //
        searchView.setSearchClickListener(this);
        searchView.setDropdownClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvMakeVideo:// 新建
                make();
                break;
            case R.id.iv_back:// 返回
                setSearchView(new SearchCategoryBean(0, "全部分类", SearchCategoryBean.CID_ALL), null);
                mHomePage.refreshIndexPage();
                break;
            case R.id.layDropdownHandle:// 分类下拉框
                showPopDropdown(v);
                break;
            case R.id.ivSearch:// 搜索
                search();
                break;
            case R.id.ivSetting:// 设置
                test();
//                toUserMGPage(UserFMActivity.INIT_USER_INFO);
                break;
        }
    }

    private void test() {
        try {
            File file = new File(Const.RECORD_DIR, "test.txt");
            FileIOUtils.writeFile(file, "text88888");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onEventMainThread(CourseThumbnailLoadedEvent event) {
        ItemViewLocalRecord item = mHomePage.findViewWithTag(event.getKey());
        if (item != null)
            item.setThumbnail(event.getIcon());

    }

    public void onEventMainThread(RefreshCourseListEvent event) {
        mHomePage.refreshIndexPage();
    }

    // =========================================================================

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                T.showShort(mContext, "再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);// 解除注册EventBus订阅者
        singleExecutor.shutdownNow();
        super.onDestroy();
    }

    /**
     * 横竖屏配置改变
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mHomePage = new HomePage();
        replacePage(mHomePage);

    }

    /**
     * 替换视图
     */
    public void replacePage(Fragment fg) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.id_content, fg);
        ft.commitAllowingStateLoss();
    }

    /**
     * 搜索
     */
    private void search() {
        SearchCategoryBean type = searchView.getCategory();
        String keywords = searchView.getSearchKeywords();
        if (TextUtils.isEmpty(keywords)) {
            T.showShort(mContext, "搜索关键字不能为空！");
            return;
        }
        // 和上次完全相同
        if (mCurSearchType.equals(type) && keywords.equals(mCurSearchKeyWords)) {
            T.showShort(mContext, "请不要频繁点击！");
            return;
        }

        mHomePage.searchRecords(type, keywords, true);
    }

    private CategoryPoW popupWindow;

    private void showPopDropdown(View anchor) {
        popupWindow = new CategoryPoW(mContext, anchor, anchor, new SearchCategoryChangedListener() {
            @Override
            public void onSearchCategoryChanged(SearchCategoryBean categoryNew) {
                SearchCategoryBean befCategory = searchView.getCategory();
                if (!befCategory.equals(categoryNew)) {
                    setSearchView(categoryNew, "");
                    // 通过EventBus订阅者发送消息
                    EventBus.getDefault().post(new RefreshCourseListEvent());
                }
            }
        });
        popupWindow.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                popupWindow = null;
            }
        });
        singleExecutor.execute(new TaskLoadRecordCategories(new RecordTypeLoadListener() {

            @Override
            public void onRecordTypeLoaded(List<SearchCategoryBean> result) {
                if (popupWindow != null) {
                    popupWindow.refreshCategoryList(result);
                }
            }
        }, true));
        popupWindow.showPopupWindow(WeiZhi.Bottom);
    }

    /**
     * 加载填充视图
     */
    private void loadView(Fragment fg) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.id_content, fg).commit();
    }

    /**
     * 新建一个讲讲画板
     */
    private void make() {
        Intent it = new Intent(this, DrawActivity.class);
        it.putExtra(DrawActivity.EXTRA_PLAY_MODE, PlayMode.MAKE);
        startActivity(it);
    }

    /**
     * 去用户设置页面
     */
    private void toUserMGPage(int viewIndex) {
        Intent intent = new Intent(mContext, UserFMActivity.class);
        intent.putExtra(UserFMActivity.IN_LOAGING_PAGE_INDEX, viewIndex);// 默认加载界面
        startActivity(intent);// 开启目标Activity
    }
}
