package com.speaktool.ui.fragment;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.speaktool.R;
import com.speaktool.ui.activity.UserFMActivity;
import com.speaktool.api.LoginCallback;
import com.speaktool.api.ThirdPartyRunState;
import com.speaktool.ui.base.BaseFragment;
import com.speaktool.bean.ThirdParty;
import com.speaktool.bean.UserBean;
import com.speaktool.busevents.RefreshCourseListEvent;
import com.speaktool.impl.platforms.PartnerPlat;
import com.speaktool.impl.platforms.QQPlat;
import com.speaktool.impl.platforms.SinaPlat;
import com.speaktool.impl.platforms.TencentWeiboPlat;
import com.speaktool.tasks.MyThreadFactory;
import com.speaktool.tasks.TaskGetThirdpartys;
import com.speaktool.tasks.TaskGetThirdpartys.TaskGetThirdpartysCallback;
import com.speaktool.tasks.TaskUserLogin;
import com.speaktool.tasks.TaskUserLogin.UserLoginCallback;
import com.speaktool.ui.dialogs.LoadingDialog;
import com.speaktool.ui.dialogs.ThirdpartyListDialog;
import com.speaktool.utils.T;
import com.speaktool.utils.UserInfoValidateUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * 用户登陆界面
 *
 * @author shaoshuai
 */
public class UserLoginPage extends BaseFragment implements OnClickListener, ThirdPartyRunState {
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
    private LoadingDialog mLoadingDialog;
    private ExecutorService singleExecutor = Executors.newSingleThreadExecutor(new MyThreadFactory(
            "loadThirdpartysThread"));

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
        // 初始化
        mLoadingDialog = new LoadingDialog(mActivity);
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
                tencentLogin();
                break;
            case R.id.laySinaLogin:// 新浪微博
                sinaLogin();
                break;
            case R.id.layQQLogin:// QQ
                qqLogin();
                break;
            case R.id.layMore:// 更多
                more();
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
        mLoadingDialog.show();
        singleExecutor.execute(new TaskUserLogin(new UserLoginCallback() {
            @Override
            public void onLoginSuccess() {
                mLoadingDialog.dismiss();
                T.showShort(mContext, "登陆成功！");
                EventBus.getDefault().post(new RefreshCourseListEvent());
                close(LoginCallback.SUCCESS);
            }

            @Override
            public void onLoginFail() {
                mLoadingDialog.dismiss();
                T.showShort(mContext, "登录失败！");
            }

            @Override
            public void onConnectFail() {
                mLoadingDialog.dismiss();
                T.showShort(mContext, "服务器链接失败！");
            }
        }, userBean));
    }

    private LoginCallback mLoginCallback;

    public void setLoginCallback(LoginCallback callback) {
        mLoginCallback = callback;
    }

    /**
     * 登陆界面-关闭
     */
    private void close(int resultCode) {
        if (mLoginCallback != null)
            mLoginCallback.onLoginFinish(resultCode);

        if (resultCode == LoginCallback.SUCCESS) {
            fm.popBackStack();// 退出当前页面
        } else {
            T.showShort(mContext, "登陆失败");
        }
    }

    /**
     * 登陆界面-腾讯微博
     */
    private void tencentLogin() {
        TencentWeiboPlat lTencentWeiboPlat = new TencentWeiboPlat(mActivity, this);
        lTencentWeiboPlat.login(new LoginCallback() {
            @Override
            public void onLoginFinish(int resultCode) {
                close(resultCode);
            }
        });
    }

    /**
     * 登陆界面-新浪
     */
    private void sinaLogin() {
        SinaPlat mSinaPlat = new SinaPlat(mActivity, this);
        mSinaPlat.login(new LoginCallback() {
            @Override
            public void onLoginFinish(int resultCode) {
                close(resultCode);
            }
        });
    }

    /**
     * 登陆界面-QQ
     */
    private void qqLogin() {
        QQPlat mQQPlat = new QQPlat(mActivity, this);
        mQQPlat.login(new LoginCallback() {
            @Override
            public void onLoginFinish(int resultCode) {
                close(resultCode);
            }
        });
    }

    private ThirdpartyListDialog mThirdpartyListDialog;

    /**
     * 登陆界面-更多方式
     */
    private void more() {
        mThirdpartyListDialog = new ThirdpartyListDialog(mActivity);
        mThirdpartyListDialog.show();
        mThirdpartyListDialog.setListItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mThirdpartyListDialog.dismiss();
                ThirdParty party = (ThirdParty) parent.getAdapter().getItem(position);
                if (party == null) {
                    T.showShort(mContext, "登录失败！");
                    return;
                }
                PartnerPlat lPartnerPlat = new PartnerPlat(mContext, UserLoginPage.this, party);
                lPartnerPlat.login(new LoginCallback() {
                    @Override
                    public void onLoginFinish(int resultCode) {
                        close(resultCode);
                    }
                });
            }
        });
        singleExecutor.execute(new TaskGetThirdpartys(new TaskGetThirdpartysCallback() {
            @Override
            public void onThirdpartyLoaded(List<ThirdParty> result) {
                if (mThirdpartyListDialog.isShowing()) {
                    mThirdpartyListDialog.refreshListData(result);
                }
            }

            @Override
            public void onConnectFail() {
                T.showShort(mContext, "服务器链接失败！请检查网络");
            }

            @Override
            public void onResponseFail() {
                T.showShort(mContext, "服务器响应错误！");
            }

        }, TaskGetThirdpartys.PartyType.LOGIN, null));
    }

    @Override
    public void onStartRun() {
        mLoadingDialog.show();
    }

    @Override
    public void onFinishRun() {
        mLoadingDialog.dismiss();
    }
}