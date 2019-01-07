package com.speaktool.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * Fragment基类
 *
 * @author maple
 * @time 2018/12/22
 */
abstract class BaseFragment : Fragment() {
    internal lateinit var view: View
    lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = inflater.inflate(getLayoutRes(), container, false)
        view.isClickable = true // 防止点击穿透，底层的fragment响应上层点击触摸事件
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData(savedInstanceState)
    }

    open fun onKeyBackPressed(): Boolean {
        // 是否消耗掉back事件
        return false
    }

    abstract fun getLayoutRes(): Int

    abstract fun initData(savedInstanceState: Bundle?)

}
