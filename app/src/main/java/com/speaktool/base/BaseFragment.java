package com.speaktool.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

/**
 * Fragment基类
 *
 * @author maple
 * @time 2018/12/22
 */
public abstract class BaseFragment extends Fragment {
    public Context mContext;
    public View view;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(getLayoutRes(), container, false);
        view.setClickable(true);// 防止点击穿透，底层的fragment响应上层点击触摸事件
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData(savedInstanceState);
    }

    public boolean onKeyBackPressed() {
        // 是否消耗掉back事件
        return false;
    }

    public abstract int getLayoutRes();

    public abstract void initData(Bundle savedInstanceState);

}
