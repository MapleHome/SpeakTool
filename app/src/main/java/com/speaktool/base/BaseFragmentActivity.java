package com.speaktool.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.speaktool.R;

/**
 * @author maple
 * @time 2018/10/11
 */
public abstract class BaseFragmentActivity extends FragmentActivity {
    public LinearLayout ll_root;
    public FrameLayout fl_content;

    public Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_base_fragment);
        mContext = getBaseContext();

        findView();
    }

    private void findView() {
        ll_root = (LinearLayout) findViewById(R.id.ll_root);
        fl_content = (FrameLayout) findViewById(R.id.fl_content);
    }

    public void setBaseContentView(int layoutID) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(layoutID, ll_root);
        fl_content.setVisibility(View.GONE);
    }

    // ------------------ fragment ------------------

    public void addView(Fragment fgView) {
        addView(fgView, R.id.fl_content);
    }

    public void addView(Fragment fgView, int containerViewId) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(containerViewId, fgView).commit();
    }

    public void replaceView(Fragment fgView) {
        replaceView(fgView, R.id.fl_content);
    }

    public void replaceView(Fragment fgView, int containerViewId) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(containerViewId, fgView);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void onBack() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    public boolean onNext() {
        int nextPage = getSupportFragmentManager().getBackStackEntryCount() + 1;
//        if (fragmentList != null && fragmentList.size() > nextPage) {
//            replaceView(fragmentList.get(nextPage));
//            return true;
//        }
        return false;
    }


}