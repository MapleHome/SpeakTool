package com.speaktool.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.google.gson.Gson;
import com.speaktool.Const;
import com.speaktool.R;
import com.speaktool.base.BaseFragmentActivity;
import com.speaktool.busevents.RefreshCourseListEvent;
import com.speaktool.ui.draw.DrawActivity;
import com.speaktool.ui.player.ReleaseBean;
import com.speaktool.ui.setting.UserFMActivity;
import com.speaktool.utils.FileUtils;
import com.speaktool.utils.T;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 主界面
 *
 * @author shaoshuai
 */
public class MainActivity extends BaseFragmentActivity {
    private HomePage mHomePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        mHomePage = new HomePage();
        addView(mHomePage, R.id.id_content);
    }

    @OnClick(R.id.tvMakeVideo)
    void make() {
        Intent it = new Intent(this, DrawActivity.class);
        startActivity(it);
    }

    @OnClick(R.id.ivSetting)
    void toSettingGPage() {
        Intent intent = new Intent(this, UserFMActivity.class);
        intent.putExtra(UserFMActivity.LOAD_PAGE_INDEX, UserFMActivity.INIT_USER_INFO);
        startActivity(intent);
    }

    private void test() {
        String str = "/167bb3e8e3a/release.txt";
        File releaseFile = new File(Const.RECORD_DIR, str);
        try {
            String res = FileUtils.readFile(releaseFile);
            ReleaseBean releaseBean = new Gson().fromJson(res, ReleaseBean.class);
            Log.e("res ", " :   " + res + "  " + releaseBean.version);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        try {
//            File file = new File(Const.RECORD_DIR, "test.txt");
//            FileUtils.writeFile(file, "text88888");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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
