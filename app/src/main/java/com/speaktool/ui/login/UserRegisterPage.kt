package com.speaktool.ui.login

import android.os.Bundle
import com.speaktool.R
import com.speaktool.base.BaseFragment
import com.speaktool.ui.setting.SettingActivity
import com.speaktool.utils.T
import com.speaktool.utils.UserInfoValidateUtil
import com.speaktool.utils.UserSPUtils
import kotlinx.android.synthetic.main.fragment_user_register.*

/**
 * 用户注册界面
 *
 * @author maple
 * @time 2019/1/3
 */
class UserRegisterPage : BaseFragment() {
    private lateinit var mActivity: SettingActivity

    override fun getLayoutRes(): Int {
        return R.layout.fragment_user_register
    }

    override fun initData(savedInstanceState: Bundle?) {
        mActivity = activity as SettingActivity
        mActivity.setTitle("注册")
        // 完成
        btnFinish.setOnClickListener {
            doFinishClick()
        }
    }

    /**
     * 注册界面-完成
     */
    private fun doFinishClick() {
        val account = etAccount.text.toString().trim()// 帐号
        val pwd = etPwd.text.toString().trim()// 密码
        val nick = etNick.text.toString().trim()// 昵称
        val email = etEmail.text.toString().trim()// 邮箱
        val introduce = etIntroduce.text.toString().trim()// 简介

        if (!UserInfoValidateUtil.checkAccount(account)) {
            T.showShort(mContext, "帐号格式不正确！")
            return
        }
        if (!UserInfoValidateUtil.checkPassword(pwd)) {
            T.showShort(mContext, "密码格式不正确！")
            return
        }
        if (!UserInfoValidateUtil.checkEmail(email)) {
            T.showShort(mContext, "邮箱格式不正确！")
            return
        }

        val mUser = UserSPUtils().user
        mUser.account = account// 帐号
        mUser.password = pwd// 密码
        mUser.nickName = nick// 昵称
        mUser.email = email// 邮箱
        mUser.introduce = introduce// 简介
        mUser.activity = true
        UserSPUtils().user = mUser

        mActivity.onBackPressed()// 退出当前页面
    }

}