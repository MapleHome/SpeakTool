package com.speaktool.base;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment基类
 *
 * @author maple
 */
public abstract class BaseFragment extends Fragment {
    public View view;
    public Context mContext;
    public FragmentManager fm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();
        this.fm = getFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = initView(inflater);
        view.setClickable(true);// 防止点击穿透，底层的fragment响应上层点击触摸事件
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        initData(savedInstanceState);
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * onCreateView方法中构建UI(将xml转换成view对象)
     */
    public abstract View initView(LayoutInflater inflater);

    /**
     * onActivityCreated方法中请求网络。返回数据填充UI
     */
    public abstract void initData(Bundle savedInstanceState);


}
