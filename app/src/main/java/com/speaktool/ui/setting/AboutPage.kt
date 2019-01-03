package com.speaktool.ui.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import com.speaktool.R
import com.speaktool.base.BaseFragment
import com.speaktool.utils.AppUtils
import kotlinx.android.synthetic.main.fragment_uc_about.*

/**
 * about page
 *
 * @author maple
 * @time 2019/1/2
 */
class AboutPage : BaseFragment(), OnClickListener {
    private lateinit var mActivity: SettingActivity

    override fun getLayoutRes(): Int {
        return R.layout.fragment_uc_about
    }

    override fun initData(savedInstanceState: Bundle?) {
        mActivity = activity as SettingActivity
        mActivity.setTitle("关于")
        // 版本号
        val packageInfo = AppUtils.getPackageInfo(mContext)
        if (packageInfo != null) {
            tv_version.text = "For Android V ${packageInfo.versionName} - ${packageInfo.versionCode}"
        }
        // initListener
        ll_check_update.setOnClickListener(this)
        ll_service_tel.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ll_check_update -> {// 检查更新

            }
            R.id.ll_service_tel -> {// 服务热线
                callPhone("010-62117887")
            }
            else -> {
            }
        }
    }

    private fun callPhone(num: String) {
        val intent = Intent(Intent.ACTION_DIAL)// 跳转拨号界面，显示号码
        // Intent intent = new Intent(Intent.ACTION_CALL);//对用户没有提示直接拨打电话
        val data = Uri.parse("tel:$num")
        intent.data = data
        startActivity(intent)
    }
}
