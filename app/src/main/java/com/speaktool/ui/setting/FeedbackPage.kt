package com.speaktool.ui.setting

import android.os.Bundle
import android.util.Log
import com.speaktool.R
import com.speaktool.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_uc_feedback.*

/**
 * 意见反馈
 * @author maple
 * @time 2019/1/2
 */
class FeedbackPage : BaseFragment() {
    private lateinit var mActivity: UserFMActivity

    override
    fun getLayoutRes(): Int {
        return R.layout.fragment_uc_feedback
    }

    override
    fun initData(savedInstanceState: Bundle?) {
        mActivity = activity as UserFMActivity
        mActivity.setTitle("意见反馈")

        // 提交反馈
        bt_fb_submit.setOnClickListener {
            val content = et_fb_content.text.toString().trim()
            val contact = et_fb_contact.text.toString().trim()

            Log.e("feed back", "\n content: $content \n contact: $contact")
        }

    }

}
