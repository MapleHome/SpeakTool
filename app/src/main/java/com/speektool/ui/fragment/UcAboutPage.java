package com.speektool.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.speektool.R;
import com.speektool.activity.UserFMActivity;
import com.speektool.base.BaseFragment;
import com.speektool.manager.AppManager;
import com.speektool.manager.AppUpdateManager;
import com.speektool.tasks.ThreadPoolWrapper;

/**
 * 关于
 * 
 * @author shao
 * 
 */
public class UcAboutPage extends BaseFragment implements OnClickListener {
	@ViewInject(R.id.tv_version)
	private TextView tv_version;// APP版本

	@ViewInject(R.id.ll_check_update)
	private LinearLayout ll_check_update;// 检查更新
	@ViewInject(R.id.ll_service_tel)
	private LinearLayout ll_service_tel;// 服务热线

	public static final String FRAGMENT_NAME = "关于";
	private UserFMActivity mActivity;
	private AppUpdateManager mAppUpdateManager;
	private ThreadPoolWrapper singleExecutor = ThreadPoolWrapper.newThreadPool(1);

	@Override
	public View initView(LayoutInflater inflater) {
		view = inflater.inflate(R.layout.fragment_uc_about, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void initData(Bundle savedInstanceState) {
		mActivity = (UserFMActivity) getActivity();
		mActivity.tv_title.setText(FRAGMENT_NAME);
		// 版本号
		String versionName = AppManager.getCurrentAppVersionName(mContext);
		tv_version.setText("For Android V " + versionName);
		// app更新
		mAppUpdateManager = new AppUpdateManager(mContext, singleExecutor, true);
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
			mAppUpdateManager.checkAppUpdate();
			break;
		case R.id.ll_service_tel:// 服务热线
			callPhone("010-62117887");
			break;
		default:
			break;
		}
	}

	@Override
	public void onDestroyView() {
		singleExecutor.shutdownNow();
		mAppUpdateManager = null;
		super.onDestroyView();
	}

	/** 拨打电话 */
	private void callPhone(String num) {
		Intent intent = new Intent(Intent.ACTION_DIAL);// 跳转拨号界面，显示号码
		// Intent intent = new Intent(Intent.ACTION_CALL);//对用户没有提示直接拨打电话
		Uri data = Uri.parse("tel:" + num);
		intent.setData(data);
		startActivity(intent);
	}
}
