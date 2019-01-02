package com.speaktool.ui.setting;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.speaktool.R;
import com.speaktool.base.BaseFragmentActivity;
import com.speaktool.ui.login.UserLoginPage;
import com.speaktool.ui.login.UserRegisterPage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 基本视图容器-管理器
 * 打造一个只需要传递，需要加载那个页面索引标志的加载器
 *
 * @author maple
 * @time 2019/1/2
 */
public class UserFMActivity extends BaseFragmentActivity {
    @BindView(R.id.tv_back) TextView tv_back;// 返回
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
        setContentView(R.layout.activity_base_top_bar_fragment);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        Intent it = getIntent();
        loadViewIndex = it.getIntExtra(LOAD_PAGE_INDEX, 0);
        switch (loadViewIndex) {
            case INIT_USER_REGISTER:// 注册
                addView(new UserRegisterPage());
                break;
            case INIT_USER_LOGIN:// 登陆
                addView(new UserLoginPage());
                break;
            case INIT_USER_INFO:// 用户信息
                addView(new SettingPage());
                break;
            case INIT_USER_INFO_CHANGE:// 用户信息修改
                addView(new UserInfoChangePage());
                break;
            case INIT_APP_FEEDBACK:// 意见反馈
                addView(new FeedbackPage());
                break;
            case INIT_APP_ABOUT:// 其他
                addView(new AboutPage());
                break;
            default:
                break;
        }
    }

    @OnClick(R.id.tv_back)
    public void onBack() {
        onBackPressed();
    }

    public void setTitle(String title) {
        tv_title.setText(title);
    }

}
