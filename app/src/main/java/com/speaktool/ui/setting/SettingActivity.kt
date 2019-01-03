package com.speaktool.ui.setting

import android.os.Bundle
import com.speaktool.R
import com.speaktool.base.BaseFragmentActivity
import kotlinx.android.synthetic.main.activity_base_top_bar.*


/**
 * 基本视图容器-管理器
 * 打造一个只需要传递，需要加载那个页面索引标志的加载器
 *
 * @author maple
 * @time 2019/1/2
 */
class SettingActivity : BaseFragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_top_bar_fragment)

        initView()
    }

    private fun initView() {
        addView(SettingPage())
        tv_back.setOnClickListener {
            onBackPressed()
        }
    }

    fun setTitle(title: String) {
        tv_title.text = title
    }

}
