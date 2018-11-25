package com.speaktool.ui.adapters;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

/**
 * @author maple
 * @time 2018/11/23
 */
public class SplashPageAdapter extends PagerAdapter {
    private List<View> Views;

    public SplashPageAdapter(List<View> views) {
        this.Views = views;
    }

    @Override
    public int getCount() {
        return Views.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        container.addView(Views.get(position));
        return Views.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}
