package com.speaktool.ui.Login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.speaktool.R;
import com.speaktool.bean.UserBean;
import com.speaktool.ui.Setting.UserFMActivity;
import com.speaktool.base.BaseFragment;
import com.speaktool.utils.T;
import com.speaktool.utils.UserInfoValidateUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 用户登陆界面
 *
 * @author shaoshuai
 */
public class UserLoginPage extends BaseFragment {
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

    @OnClick(R.id.btnRegister)
    public void toRegisterPage() {
        // 跳转到注册
        mActivity.replacePage(new UserRegisterPage());
    }

    /**
     * 登陆界面-登陆
     */
    @OnClick(R.id.btnLogin)
    public void doLoginClick() {
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


}