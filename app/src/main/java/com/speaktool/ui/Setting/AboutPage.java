package com.speaktool.ui.Setting;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.speaktool.R;
import com.speaktool.base.BaseFragment;
import com.speaktool.utils.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * @author maple
 * @time 2019/1/2
 */
public class AboutPage extends BaseFragment implements OnClickListener {
    @BindView(R.id.tv_version) TextView tv_version;// APP版本
    @BindView(R.id.ll_check_update) RelativeLayout ll_check_update;// 检查更新
    @BindView(R.id.ll_service_tel) RelativeLayout ll_service_tel;// 服务热线

    private UserFMActivity mActivity;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_uc_about;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mActivity = (UserFMActivity) getActivity();
        ButterKnife.bind(this, view);

        mActivity.setTitle("关于");
        // 版本号
        PackageInfo packageInfo = AppUtils.getPackageInfo(mContext);
        if (packageInfo != null)
            tv_version.setText("For Android V " + packageInfo.versionName + " - " + packageInfo.versionCode);
        // initListener
        ll_check_update.setOnClickListener(this);// 检查更新
        ll_service_tel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_check_update:// 检查更新

                break;
            case R.id.ll_service_tel:// 服务热线
                callPhone("010-62117887");
                break;
            default:
                break;
        }
    }

    private void callPhone(String num) {
        Intent intent = new Intent(Intent.ACTION_DIAL);// 跳转拨号界面，显示号码
        // Intent intent = new Intent(Intent.ACTION_CALL);//对用户没有提示直接拨打电话
        Uri data = Uri.parse("tel:" + num);
        intent.setData(data);
        startActivity(intent);
    }
}
