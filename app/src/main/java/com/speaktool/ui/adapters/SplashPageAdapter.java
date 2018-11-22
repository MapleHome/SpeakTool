package com.speaktool.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class SplashPageAdapter extends PagerAdapter {

    private List<View> Views;

    public SplashPageAdapter(List<View> views) {
        this.Views = views;
    }

    /**
     * 获取总界面数
     */
    @Override
    public int getCount() {
        return Views.size();
    }

    /**
     * 判断pager的一个view是否和instantiateItem方法返回的object有关联，并决定是否由对象生成界面
     */
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    /**
     * PagerAdapter适配器选择哪个对象
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        container.addView(Views.get(position));
        return Views.get(position);
    }

    /**
     * 从ViewGroup中移出当前View
     */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }


}
