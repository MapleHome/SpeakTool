package com.speektool.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ishare_lib.ui.dialog.AlertDialog;
import com.speektool.Const;
import com.speektool.R;
import com.speektool.adapters.SplashPageAdapter;
import com.speektool.utils.MiuiManager;
import com.speektool.utils.SPUtils;
import com.speektool.utils.T;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import roboguice.activity.RoboActivity;

/**
 * 欢迎界面
 *
 * @author shaoshuai
 */
public class SplashActivity extends RoboActivity {
    @BindView(R.id.rl_root) RelativeLayout rl_root;// 根布局
    @BindView(R.id.guide_viewpager) ViewPager guide_viewpager;// ViwePager
    @BindView(R.id.ll_point) LinearLayout ll_point;// 相应的点区域

    protected static final int MSG_ENTER_HOME = 20;// 进入主界面
    protected static final int MSG_IO_ERROR = 30;// IO异常

    private ArrayList<View> guideViews;
    private SplashPageAdapter guideViewPagerAdapter;
    private ImageView[] guide_dot_iv;
    private Context mContext;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ENTER_HOME:// 进入下一个界面
                    enterHome();
                    break;
                case MSG_IO_ERROR:// io 异常
                    T.showShort(mContext, "错误号" + MSG_IO_ERROR);
                    enterHome();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        mContext = getApplicationContext();

        long startTime = System.currentTimeMillis();
        boolean isFirst = SPUtils.getBool(Const.First_ComeIn, true);
        initData();
        if (isFirst) {
            initViews();
        } else {
            ll_point.setVisibility(View.INVISIBLE);
            guide_viewpager.setVisibility(View.INVISIBLE);
            rl_root.setBackgroundResource(R.drawable.guide2);

            Message msg = Message.obtain();
            msg.what = MSG_ENTER_HOME;
            long sleepTime = Const.SplashMinTime - (System.currentTimeMillis() - startTime);
            Log.e("", "失眠时间：" + sleepTime);
            handler.sendMessageDelayed(msg, sleepTime);
        }
    }

    private void initViews() {
        // 初始化点
        guide_dot_iv = new ImageView[3];
        guide_dot_iv[0] = findViewById(R.id.guide_dot1_iv);
        guide_dot_iv[1] = findViewById(R.id.guide_dot2_iv);
        guide_dot_iv[2] = findViewById(R.id.guide_dot3_iv);
        // 初始化页面
        View guideView1 = LayoutInflater.from(this).inflate(R.layout.activity_splash_view1, null);
        View guideView2 = LayoutInflater.from(this).inflate(R.layout.activity_splash_view2, null);
        View guideView3 = LayoutInflater.from(this).inflate(R.layout.activity_splash_view3, null);
        // 按钮点击
        guideView3.findViewById(R.id.guide_start_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                enterHome();
            }
        });
        // 添加页面
        guideViews = new ArrayList<View>();
        guideViews.add(guideView1);
        guideViews.add(guideView2);
        guideViews.add(guideView3);

        guideViewPagerAdapter = new SplashPageAdapter(guideViews);
        guide_viewpager.setAdapter(guideViewPagerAdapter);
        guide_viewpager.setOnPageChangeListener(pageChangeListener);
    }

    private void initData() {
        checkOs();// 检查系统
    }

    /**
     * 页面变化监听
     */
    OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            for (int i = 0; i < guide_dot_iv.length; i++) {
                if (position == i) {
                    guide_dot_iv[position].setImageResource(R.drawable.guide_dot_pressed);
                } else {
                    guide_dot_iv[i].setImageResource(R.drawable.guide_dot_normal);
                }
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };
    // 是否检查系统
    private final static String OS_IS_CHECKED = "OS_IS_CHECKED";

    /**
     * 检查系统
     */
    private void checkOs() {
        Log.e("main", "当前系统型号为:: " + Build.MODEL);// Lenovo S810t
        if (isXiaomiOS()) {
            if (SPUtils.getBool(OS_IS_CHECKED, false))
                return;

            new AlertDialog(this)
                    .builder()
                    .setTitle("提示")
                    .setMsg("正常使用本应用需要悬浮窗权限，请到设置中心开启！")
                    .setPositiveButton("去开启", new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MiuiManager.openMiuiPermissionActivity(mContext);
                            SPUtils.putBool(OS_IS_CHECKED, true);
                        }
                    })
                    .setNegativeButton("暂不", new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
        }
    }

    public boolean isXiaomiOS() {
        String display = Build.DISPLAY;// 显示器- JLS36C
        String manufacturer = Build.MANUFACTURER;// 制造商- LENOVO

        if ((display != null) && (display.toLowerCase().indexOf("miui") >= 0))
            return true;
        if ((manufacturer != null) && (manufacturer.toLowerCase().indexOf("xiaomi") >= 0))
            return true;
        return false;
    }

    /**
     * 进入主界面
     */
    protected void enterHome() {
        finish();
        SPUtils.putBool(Const.First_ComeIn, false);// 不再是第一次了
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
