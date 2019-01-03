package com.speaktool.ui.login

import android.os.Bundle
import com.speaktool.R
import com.speaktool.base.BaseFragment
import com.speaktool.ui.setting.SettingActivity
import com.speaktool.utils.UserSPUtils
import kotlinx.android.synthetic.main.fragment_user_login.*

/**
 * 用户登陆界面
 *
 * @author maple
 * @time 2019/1/2
 */
class UserLoginPage : BaseFragment() {
    private lateinit var mActivity: SettingActivity

    override fun getLayoutRes(): Int {
        return R.layout.fragment_user_login
    }

    override fun initData(savedInstanceState: Bundle?) {
        mActivity = activity as SettingActivity
        mActivity.setTitle("登录")

        btnRegister.setOnClickListener {
            mActivity.replaceView(UserRegisterPage()) // 跳转到注册
        }
        btnLogin.setOnClickListener {
            doLogin()
        }
    }

    /**
     * 登陆界面-登陆
     */
    private fun doLogin() {
        val account = etAccount.text.toString().trim()// 账户
        val pwd = etPwd.text.toString().trim()// 密码

//        if (!UserInfoValidateUtil.checkAccount(account)) {
//            T.showShort(mContext, "帐号格式不正确！")
//            return
//        }
//        if (!UserInfoValidateUtil.checkPassword(pwd)) {
//            T.showShort(mContext, "密码格式不正确！")
//            return
//        }

        UserSPUtils().setUserActivity(true)

        mActivity.onBackPressed()
    }

}