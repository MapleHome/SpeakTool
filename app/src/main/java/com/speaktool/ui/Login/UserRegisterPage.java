package com.speaktool.ui.Login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.speaktool.R;
import com.speaktool.ui.Setting.UserFMActivity;
import com.speaktool.base.BaseFragment;
import com.speaktool.bean.UserBean;
import com.speaktool.busevents.RefreshCourseListEvent;
import com.speaktool.view.dialogs.LoadingDialog;
import com.speaktool.utils.T;
import com.speaktool.utils.UserInfoValidateUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 用户注册界面
 *
 * @author shaoshuai
 */
public class UserRegisterPage extends BaseFragment implements OnClickListener {
    @BindView(R.id.etAccount) EditText etAccount;// 帐号
    @BindView(R.id.etPwd) EditText etPwd;// 密码
    @BindView(R.id.etNick) EditText etNick;// 昵称
    @BindView(R.id.etEmail) EditText etEmail;// 邮箱
    @BindView(R.id.etIntroduce) EditText etIntroduce;// 简介
    @BindView(R.id.btnFinish) Button btnFinish;// 完成

    public static final String FRAGMENT_NAME = "注册";
    private UserFMActivity mActivity;
    private LoadingDialog mLoadingDialog;

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_user_register, null);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mActivity = (UserFMActivity) getActivity();
        mActivity.setTitle(FRAGMENT_NAME);
        //
        mLoadingDialog = new LoadingDialog(mActivity);
        btnFinish.setOnClickListener(this);// 完成
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFinish:// 完成
                doFinishClick();
                break;
            default:
                break;
        }
    }

    /**
     * 注册界面-完成
     */
    private void doFinishClick() {
        String account = etAccount.getText().toString().trim();// 帐号
        String pwd = etPwd.getText().toString().trim();// 密码
        String nick = etNick.getText().toString().trim();// 昵称
        String email = etEmail.getText().toString().trim();// 邮箱
        String introduce = etIntroduce.getText().toString().trim();// 简介

        if (!UserInfoValidateUtil.checkAccount(account)) {
            T.showShort(mContext, "帐号格式不正确！");
            return;
        }
        if (!UserInfoValidateUtil.checkPassword(pwd)) {
            T.showShort(mContext, "密码格式不正确！");
            return;
        }
        if (!UserInfoValidateUtil.checkEmail(email)) {
            T.showShort(mContext, "邮箱格式不正确！");
            return;
        }
        //
        final UserBean mUser = new UserBean();
        mUser.setType(UserBean.USER_TYPE_SPEAKTOOL);
        mUser.setWidgetUserId(null);
        mUser.setAccount(account);// 帐号
        mUser.setPassword(pwd);// 密码
        mUser.setEmail(email);// 邮箱
        mUser.setNickName(nick);// 昵称
        mUser.setIntroduce(introduce);// 简介
        //
        mLoadingDialog.show("请耐心等待...");

        mLoadingDialog.dismiss();
        EventBus.getDefault().post(new RefreshCourseListEvent());
        fm.popBackStack();// 退出当前界面
    }

}