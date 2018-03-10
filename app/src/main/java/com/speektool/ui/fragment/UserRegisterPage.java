package com.speektool.ui.fragment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ishare_lib.ui.dialog.AlertDialog;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.speektool.R;
import com.speektool.activity.UserFMActivity;
import com.speektool.base.BaseFragment;
import com.speektool.bean.UserBean;
import com.speektool.busevents.RefreshCourseListEvent;
import com.speektool.tasks.MyThreadFactory;
import com.speektool.tasks.TaskUserRegister;
import com.speektool.tasks.TaskUserRegister.UserRegisterCallback;
import com.speektool.ui.dialogs.LoadingDialog;
import com.speektool.utils.UserInfoValidateUtil;

import de.greenrobot.event.EventBus;

/**
 * 用户注册界面
 * 
 * @author shaoshuai
 * 
 */
public class UserRegisterPage extends BaseFragment implements OnClickListener {
	@ViewInject(R.id.etAccount)
	private EditText etAccount;// 帐号
	@ViewInject(R.id.etPwd)
	private EditText etPwd;// 密码
	@ViewInject(R.id.etNick)
	private EditText etNick;// 昵称
	@ViewInject(R.id.etEmail)
	private EditText etEmail;// 邮箱
	@ViewInject(R.id.etIntroduce)
	private EditText etIntroduce;// 简介

	@ViewInject(R.id.btnFinish)
	private Button btnFinish;// 完成

	public static final String FRAGMENT_NAME = "注册";
	private UserFMActivity mActivity;
	private LoadingDialog mLoadingDialog;
	private ExecutorService singleExecutor = Executors.newSingleThreadExecutor(new MyThreadFactory(
			"loadThirdpartysThread"));

	@Override
	public View initView(LayoutInflater inflater) {
		view = inflater.inflate(R.layout.fragment_user_register, null);
		ViewUtils.inject(this, view);

		return view;
	}

	@Override
	public void initData(Bundle savedInstanceState) {
		mActivity = (UserFMActivity) getActivity();
		mActivity.tv_title.setText(FRAGMENT_NAME);
		//
		mLoadingDialog = new LoadingDialog(mActivity);
	}

	@Override
	public void initListener() {
		btnFinish.setOnClickListener(this);// 完成
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnFinish:// 完成
			doFinishClick();
			break;

		default:
			break;
		}
	}

	/** 注册界面-完成 */
	private void doFinishClick() {
		String account = etAccount.getText().toString().trim();// 帐号
		String pwd = etPwd.getText().toString().trim();// 密码
		String nick = etNick.getText().toString().trim();// 昵称
		String email = etEmail.getText().toString().trim();// 邮箱
		String introduce = etIntroduce.getText().toString().trim();// 简介

		if (!UserInfoValidateUtil.checkAccount(account)) {
			Toast.makeText(mContext, "帐号格式不正确！", 0).show();
			return;
		}
		if (!UserInfoValidateUtil.checkPassword(pwd)) {
			Toast.makeText(mContext, "密码格式不正确！", 0).show();
			return;
		}
		if (!UserInfoValidateUtil.checkEmail(email)) {
			Toast.makeText(mContext, "邮箱格式不正确！", 0).show();
			return;
		}
		//
		final UserBean mUser = new UserBean();
		mUser.setType(UserBean.USER_TYPE_SPEAKTOOL);
		mUser.setWidgetUserId(null);
		mUser.setAccount(account);// 帐号
		mUser.setPassword(pwd);// 密码
		mUser.setEmail(email);// 邮箱
		mUser.setNickName(nick);// 昵称
		mUser.setIntroduce(introduce);// 简介
		//
		mLoadingDialog.show("请耐心等待...");
		singleExecutor.execute(new TaskUserRegister(mUserRegisterCallback, mUser));
		//
	}

	private UserRegisterCallback mUserRegisterCallback = new UserRegisterCallback() {

		@Override
		public void onUserAlreadyExist() {
			mLoadingDialog.dismiss();
			new AlertDialog(mActivity).builder().setTitle("提示").setMsg("用户已经存在！").show();
		}

		@Override
		public void onRegisterSuccess() {
			mLoadingDialog.dismiss();
			EventBus.getDefault().post(new RefreshCourseListEvent());
			fm.popBackStack();// 退出当前界面
			// close(LoginCallback.SUCCESS);
		}

		@Override
		public void onRegisterFail() {
			mLoadingDialog.dismiss();
			new AlertDialog(mActivity).builder().setTitle("提示").setMsg("注册失败！").show();

			// OneButtonAlertDialog dia = new OneButtonAlertDialog(mContext,
			// "注册失败！");
			// dia.show();
		}

		@Override
		public void onConnectFail() {
			mLoadingDialog.dismiss();
			Toast.makeText(mContext, "服务器链接失败！请检查网络", 0).show();
		}
	};

}