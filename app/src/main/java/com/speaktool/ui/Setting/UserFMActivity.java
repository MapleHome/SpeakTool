package com.speaktool.ui.Setting;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.speaktool.R;
import com.speaktool.ui.Login.UserLoginPage;
import com.speaktool.ui.Login.UserRegisterPage;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 基本视图容器-管理器
 * <p>
 * 打造一个只需要传递，需要加载那个页面索引标志的加载器
 *
 * @author shaoshuai
 */
public class UserFMActivity extends FragmentActivity {
    @BindView(R.id.iv_back) ImageView iv_back;// 返回
    @BindView(R.id.tv_title) TextView tv_title;// 标题

    public static final int INIT_USER_REGISTER = 1;// 用户注册
    public static final int INIT_USER_LOGIN = 2;// 用户登陆
    public static final int INIT_USER_INFO = 3;// 用户信息
    public static final int INIT_USER_INFO_CHANGE = 4;// 用户信息修改
    public static final int INIT_APP_FEEDBACK = 5;// 意见反馈
    public static final int INIT_APP_ABOUT = 6;// 其他

    public static final String LOAD_PAGE_INDEX = "intent_load_page_index";// 需要加载的页面
    public int loadViewIndex = 0;// 默认填充的页面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fm);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        Intent it = getIntent();
        loadViewIndex = it.getIntExtra(LOAD_PAGE_INDEX, 0);
        switch (loadViewIndex) {
            case INIT_USER_REGISTER:// 注册
                loadView(new UserRegisterPage());
                break;
            case INIT_USER_LOGIN:// 登陆
                loadView(new UserLoginPage());
                break;
            case INIT_USER_INFO:// 用户信息
                loadView(new SettingPage());
                break;
            case INIT_USER_INFO_CHANGE:// 用户信息修改
                loadView(new UserInfoChangePage());
                break;
            case INIT_APP_FEEDBACK:// 意见反馈
                loadView(new FeedbackPage());
                break;
            case INIT_APP_ABOUT:// 其他
                loadView(new AboutPage());
                break;
            default:
                break;
        }
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
     * 替换视图
     */
    public void replacePage(Fragment fg) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.id_content, fg);
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * 返回处理
     */
    public void onBack(View view) {
        FragmentManager fm = getSupportFragmentManager();
        int num = fm.getBackStackEntryCount();
        Log.e("", "++ Fragment回退栈数量：" + num);
        if (num > 0) {
            fm.popBackStack();
        } else {
            finish();
        }
    }

    public void setTitle(String title){
        tv_title.setText(title);
    }

}
