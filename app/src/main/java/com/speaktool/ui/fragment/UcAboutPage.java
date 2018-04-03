package com.speaktool.ui.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.speaktool.R;
import com.speaktool.ui.activity.UserFMActivity;
import com.speaktool.ui.base.BaseFragment;
import com.speaktool.tasks.ThreadPoolWrapper;
import com.speaktool.utils.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 关于
 *
 * @author shao
 */
public class UcAboutPage extends BaseFragment implements OnClickListener {
    @BindView(R.id.tv_version) TextView tv_version;// APP版本
    @BindView(R.id.ll_check_update) LinearLayout ll_check_update;// 检查更新
    @BindView(R.id.ll_service_tel) LinearLayout ll_service_tel;// 服务热线

    public static final String FRAGMENT_NAME = "关于";
    private UserFMActivity mActivity;
    //	private AppUpdateManager mAppUpdateManager;
//    private ThreadPoolWrapper singleExecutor = ThreadPoolWrapper.newThreadPool(1);

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_uc_about, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mActivity = (UserFMActivity) getActivity();
        mActivity.setTitle(FRAGMENT_NAME);
        // 版本号
        PackageInfo packageInfo = AppUtils.getPackageInfo(mContext);
        tv_version.setText("For Android V " + packageInfo.versionName + " - " + packageInfo.versionCode);
        // app更新
//		mAppUpdateManager = new AppUpdateManager(mContext, singleExecutor, true);
    }

    @Override
    public void initListener() {
        ll_check_update.setOnClickListener(this);// 检查更新
        ll_service_tel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_check_update:// 检查更新
//			mAppUpdateManager.checkAppUpdate();
                break;
            case R.id.ll_service_tel:// 服务热线
                callPhone("010-62117887");
                break;
            default:
                break;
        }
    }

//    @Override
//    public void onDestroyView() {
//        singleExecutor.shutdownNow();
//		mAppUpdateManager = null;
//        super.onDestroyView();
//    }

    /**
     * 拨打电话
     */
    private void callPhone(String num) {
        Intent intent = new Intent(Intent.ACTION_DIAL);// 跳转拨号界面，显示号码
        // Intent intent = new Intent(Intent.ACTION_CALL);//对用户没有提示直接拨打电话
        Uri data = Uri.parse("tel:" + num);
        intent.setData(data);
        startActivity(intent);
    }
}
