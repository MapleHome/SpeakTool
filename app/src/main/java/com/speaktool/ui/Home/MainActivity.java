package com.speaktool.ui.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.speaktool.Const;
import com.speaktool.R;
import com.speaktool.busevents.CourseThumbnailLoadedEvent;
import com.speaktool.busevents.RefreshCourseListEvent;
import com.speaktool.ui.Draw.DrawActivity;
import com.speaktool.ui.Setting.UserFMActivity;
import com.speaktool.utils.FileIOUtils;
import com.speaktool.utils.T;
import com.speaktool.view.layouts.ItemViewLocalRecord;
import com.speaktool.view.layouts.SearchView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 主界面
 *
 * @author shaoshuai
 */
public class MainActivity extends FragmentActivity {
    @BindView(R.id.tvMakeVideo) TextView tvMakeVideo;// 新建按钮
    @BindView(R.id.searchView) SearchView searchView;// 整个搜索框
    @BindView(R.id.ivSetting) ImageView ivSetting;// 设置按钮

    private HomePage mHomePage;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mContext = this;

        initView();
    }

    private void initView() {
        mHomePage = new HomePage();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.id_content, mHomePage).commit();
    }

    @OnClick(R.id.tvMakeVideo)
    public void make() {
        Intent it = new Intent(this, DrawActivity.class);
        startActivity(it);
    }

    @OnClick(R.id.ivSetting)
    public void toUserMGPage() {
        Intent intent = new Intent(this, UserFMActivity.class);
        intent.putExtra(UserFMActivity.IN_LOAGING_PAGE_INDEX, UserFMActivity.INIT_USER_INFO);
        startActivity(intent);
    }


    private void test() {
        try {
            File file = new File(Const.RECORD_DIR, "test.txt");
            FileIOUtils.writeFile(file, "text88888");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onEventMainThread(RefreshCourseListEvent event) {
        mHomePage.refreshIndexPage();
    }

    // =========================================================================

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

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

}
