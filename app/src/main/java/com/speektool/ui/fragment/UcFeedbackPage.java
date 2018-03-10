package com.speektool.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.speektool.R;
import com.speektool.activity.UserFMActivity;
import com.speektool.base.BaseFragment;

/**
 * 意见反馈
 * 
 * @author shao
 * 
 */
public class UcFeedbackPage extends BaseFragment {
	@ViewInject(R.id.et_fb_content)
	private EditText et_fb_content;// 反馈内容
	@ViewInject(R.id.et_fb_contact)
	private EditText et_fb_contact;// 联系方式
	@ViewInject(R.id.bt_fb_submit)
	private Button bt_fb_submit;// 提交

	public static final String FRAGMENT_NAME = "意见反馈";
	private UserFMActivity mActivity;

	@Override
	public View initView(LayoutInflater inflater) {
		view = inflater.inflate(R.layout.fragment_uc_feedback, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void initData(Bundle savedInstanceState) {
		mActivity = (UserFMActivity) getActivity();
		mActivity.setTitle(FRAGMENT_NAME);
	}

	@Override
	public void initListener() {
		// 提交反馈
		bt_fb_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
	}

}
