package com.speaktool.ui.welcome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.speaktool.R;
import com.speaktool.base.BaseFragment;
import com.speaktool.ui.adapters.SplashPageAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

/**
 * @author maple
 * @time 2018/11/23
 */
public class SplashFragment extends BaseFragment {
    @BindView(R.id.guide_viewpager) ViewPager guide_viewpager;
    @BindView(R.id.ll_point) LinearLayout ll_point;
    @BindViews({R.id.iv_dot1, R.id.iv_dot2, R.id.iv_dot3})
    List<ImageView> mDots;

    SplashActivity mActivity;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_splash;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mActivity = (SplashActivity) getActivity();
        ButterKnife.bind(this, view);
        // 初始化页面
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View guideView1 = inflater.inflate(R.layout.activity_splash_view1, null);
        View guideView2 = inflater.inflate(R.layout.activity_splash_view2, null);
        View guideView3 = inflater.inflate(R.layout.activity_splash_view3, null);
        // 按钮点击
        guideView3.findViewById(R.id.tv_start).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mActivity.enterHome();
            }
        });
        // 添加页面
        ArrayList<View> guideViews = new ArrayList<View>();
        guideViews.add(guideView1);
        guideViews.add(guideView2);
        guideViews.add(guideView3);

        guide_viewpager.setAdapter(new SplashPageAdapter(guideViews));
        guide_viewpager.addOnPageChangeListener(pageChangeListener);
    }

    /**
     * 页面变化监听
     */
    OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            for (int i = 0; i < mDots.size(); i++) {
                if (position == i) {
                    mDots.get(i).setImageResource(R.drawable.guide_dot_pressed);
                } else {
                    mDots.get(i).setImageResource(R.drawable.guide_dot_normal);
                }
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };


}
