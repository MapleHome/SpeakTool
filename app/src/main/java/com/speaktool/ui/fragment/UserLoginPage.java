package com.speaktool.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.speaktool.R;
import com.speaktool.api.LoginCallback;
import com.speaktool.bean.UserBean;
import com.speaktool.ui.activity.UserFMActivity;
import com.speaktool.ui.base.BaseFragment;
import com.speaktool.utils.T;
import com.speaktool.utils.UserInfoValidateUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 用户登陆界面
 *
 * @author shaoshuai
 */
public class UserLoginPage extends BaseFragment implements OnClickListener {
    @BindView(R.id.etAccount) EditText etAccount;// 账户
    @BindView(R.id.etPwd) EditText etPwd;// 密码

    @BindView(R.id.btnRegister) Button btnRegister;// 注册
    @BindView(R.id.btnLogin) Button btnLogin;// 登陆
    // 第三方
    @BindView(R.id.layTencentLogin) LinearLayout layTencentLogin;// 腾讯微博
    @BindView(R.id.laySinaLogin) LinearLayout laySinaLogin;// 新浪微博
    @BindView(R.id.layQQLogin) LinearLayout layQQLogin;// QQ
    @BindView(R.id.layMore) LinearLayout layMore;// 更多

    public static final String FRAGMENT_NAME = "登录";
    private UserFMActivity mActivity;

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_user_login, null);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mActivity = (UserFMActivity) getActivity();
        mActivity.setTitle(FRAGMENT_NAME);
    }

    @Override
    public void initListener() {
        btnRegister.setOnClickListener(this);// 注册
        btnLogin.setOnClickListener(this);// 登陆

        layTencentLogin.setOnClickListener(this);// 腾讯微博
        laySinaLogin.setOnClickListener(this);// 新浪微博
        layQQLogin.setOnClickListener(this);// QQ
        layMore.setOnClickListener(this);// 更多
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister:// 注册
                mActivity.replacePage(new UserRegisterPage());// 跳转到注册
                break;
            case R.id.btnLogin:// 登陆
                doLoginClick();
                break;
            case R.id.layTencentLogin:// 腾讯微博

                break;
            case R.id.laySinaLogin:// 新浪微博

                break;
            case R.id.layQQLogin:// QQ

                break;
            case R.id.layMore:// 更多

                break;
            default:
                break;
        }
    }

    /**
     * 登陆界面-登陆
     */
    private void doLoginClick() {
        String account = etAccount.getText().toString().trim();// 账户
        String pwd = etPwd.getText().toString().trim();// 密码

        if (!UserInfoValidateUtil.checkAccount(account)) {
            T.showShort(mContext, "帐号格式不正确！");
            return;
        }
        if (!UserInfoValidateUtil.checkPassword(pwd)) {
            T.showShort(mContext, "密码格式不正确！");
            return;
        }

        final UserBean userBean = new UserBean();
        userBean.setAccount(account);
        userBean.setPassword(pwd);


        fm.popBackStack();// 退出当前页面
    }

    private LoginCallback mLoginCallback;

    public void setLoginCallback(LoginCallback callback) {
        mLoginCallback = callback;
    }


}