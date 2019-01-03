package com.speaktool.ui.welcome;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.speaktool.Const;
import com.speaktool.R;
import com.speaktool.base.BaseFragmentActivity;
import com.speaktool.ui.home.MainActivity;
import com.speaktool.utils.SPUtils;
import com.speaktool.utils.T;
import com.speaktool.utils.permission.PermissionFragment;
import com.speaktool.utils.permission.PermissionListener;

/**
 * App启动界面
 *
 * @author maple
 * @time 2018/11/25
 */
public class SplashActivity extends BaseFragmentActivity {
    public static final int MSG_ENTER_HOME = 1;// 进入主界面

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == MSG_ENTER_HOME) {
                enterHome();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long startTime = System.currentTimeMillis();

        checkPermission();

        boolean isFirst = SPUtils.getBool(Const.First_ComeIn, true);
        if (isFirst) {
            setContentView(R.layout.activity_base_fragment);
            addView(new SplashFragment());
        } else {
            // null ContentView, 快速加载Styles.Theme中windowBackground.
            long sleepTime = Const.SplashMinTime - (System.currentTimeMillis() - startTime);
            handler.sendEmptyMessageDelayed(MSG_ENTER_HOME, sleepTime);
        }
    }

    private void checkPermission() {
        String[] permissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                Manifest.permission.RECORD_AUDIO
        };
        PermissionFragment.getPermissionFragment(this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {

                    }

                    @Override
                    public void onPermissionDenied(String[] deniedPermissions) {
                        T.showShort(SplashActivity.this, "请打开内存读写权限");
                    }

                    @Override
                    public void onPermissionDeniedDotAgain(String[] deniedPermissions) {

                    }
                }).checkPermissions(permissions, null);
    }

    public void enterHome() {
        SPUtils.putBool(Const.First_ComeIn, false);// 不再是第一次了
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
