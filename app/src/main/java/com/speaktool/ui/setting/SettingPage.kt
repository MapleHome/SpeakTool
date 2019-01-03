package com.speaktool.ui.setting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import com.google.gson.Gson
import com.speaktool.Const
import com.speaktool.R
import com.speaktool.base.BaseFragment
import com.speaktool.bean.UserBean
import com.speaktool.ui.login.UserInfoChangePage
import com.speaktool.ui.login.UserLoginPage
import com.speaktool.utils.SPUtils
import com.speaktool.utils.UserSPUtils
import kotlinx.android.synthetic.main.fragment_user_info.*

/**
 * 用户信息界面
 *
 * @author maple
 * @time 2019/1/2
 */
class SettingPage : BaseFragment(), OnClickListener {
    private lateinit var mActivity: SettingActivity
    private lateinit var userBean: UserBean

    override fun getLayoutRes(): Int {
        return R.layout.fragment_user_info
    }

    override fun initData(savedInstanceState: Bundle?) {
        mActivity = activity as SettingActivity
        mActivity.setTitle("设置")

        userBean = UserSPUtils().user
        if (userBean.activity) {
            user_name.text = userBean.nickName// 用户名
            bt_logout.visibility = View.VISIBLE// 注销按钮
        } else {
            ib_userPortrait.setImageResource(R.drawable.user_portrait)// 默认头像
            user_name.text = "登陆"// 用户名
            bt_logout.visibility = View.GONE// 隐藏注销按钮
        }

        ib_userPortrait.setOnClickListener(this)// 头像
        user_name.setOnClickListener(this)// 用户名

        ll_my_note.setOnClickListener(this)// 我的笔记
        ll_my_record.setOnClickListener(this)// 我的视频

        ll_guanwang.setOnClickListener(this)// 官网
        ll_tieba.setOnClickListener(this)// 贴吧
        ll_feedback.setOnClickListener(this)// 意见反馈
        ll_about.setOnClickListener(this)// 关于

        bt_logout.setOnClickListener(this)// 注销
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ib_userPortrait, R.id.user_name -> {// 头像,用户名
                if (userBean.activity) {
                    mActivity.replaceView(UserInfoChangePage())
                } else {
                    mActivity.replaceView(UserLoginPage())
                }
            }
            R.id.ll_my_note -> Log.e("设置Fg", "我的笔记")
            R.id.ll_my_record -> Log.e("设置Fg", "我的视频")
            R.id.ll_guanwang -> toWebPage("讲讲官网", Const.SPEAK_SERVER_URL)
            R.id.ll_tieba -> toWebPage("讲讲贴吧", Const.SPEAK_BBS_URL)
            R.id.ll_feedback -> mActivity.replaceView(FeedbackPage())
            R.id.ll_about -> mActivity.replaceView(AboutPage())
            R.id.bt_logout -> logout()// 注销
            else -> {
            }
        }
    }

    /**
     * 注销
     */
    private fun logout() {
        UserSPUtils().setUserActivity(false)

        initData(arguments)
    }

    /**
     * 去新闻页面
     */
    private fun toWebPage(title: String, url: String) {
        val intent = Intent(mContext, WebActivity::class.java)
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(WebActivity.EXTRA_TITLE, title)
        intent.putExtra(WebActivity.EXTRA_URL, url)
        mContext.startActivity(intent)
    }

}