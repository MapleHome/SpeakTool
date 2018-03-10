package com.speektool.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.speektool.Const;
import com.speektool.R;
import com.speektool.activity.UserFMActivity;
import com.speektool.activity.WebActivity;
import com.speektool.api.LoginCallback;
import com.speektool.base.BaseFragment;
import com.speektool.bean.UserBean;
import com.speektool.busevents.RefreshCourseListEvent;
import com.speektool.dao.UserDatabase;
import com.speektool.tasks.TaskGetNetImage;
import com.speektool.tasks.TaskGetNetImage.NetImageLoadListener;
import com.speektool.tasks.TaskUserLogout;
import com.speektool.tasks.TaskUserLogout.UserLogoutCallback;
import com.speektool.ui.dialogs.LoadingDialog;
import com.speektool.utils.BitmapScaleUtil;
import com.speektool.utils.T;

import de.greenrobot.event.EventBus;

/**
 * 用户信息界面
 * 
 * @author shaoshuai
 * 
 */
public class UserInfoPage extends BaseFragment implements OnClickListener {
	@ViewInject(R.id.ib_userPortrait)
	private ImageButton ib_userPortrait;// 用户头像
	@ViewInject(R.id.user_name)
	private TextView user_name;// 用户名

	@ViewInject(R.id.ll_my_note)
	private LinearLayout ll_my_note;// 我的笔记
	@ViewInject(R.id.ll_my_record)
	private LinearLayout ll_my_record;// 我的视频

	@ViewInject(R.id.ll_guanwang)
	private LinearLayout ll_guanwang;// 官网
	@ViewInject(R.id.ll_tieba)
	private LinearLayout ll_tieba;// 贴吧
	@ViewInject(R.id.ll_feedback)
	private LinearLayout ll_feedback;// 意见反馈
	@ViewInject(R.id.ll_about)
	private LinearLayout ll_about;// 关于

	@ViewInject(R.id.bt_logout)
	private Button bt_logout;// 注销

	public static final String FRAGMENT_NAME = "设置";
	private UserFMActivity mActivity;
	private boolean isLogin = false;// 是否登陆
	private LoadingDialog mLoadingDialog;

	@Override
	public View initView(LayoutInflater inflater) {
		view = inflater.inflate(R.layout.fragment_user_info, null);
		ViewUtils.inject(this, view);

		return view;
	}

	@Override
	public void initData(Bundle savedInstanceState) {
		mActivity = (UserFMActivity) getActivity();
		mActivity.tv_title.setText(FRAGMENT_NAME);

		mLoadingDialog = new LoadingDialog(mActivity);
		if (UserDatabase.getUserLoginState(mContext) == UserBean.STATE_IN) {
			isLogin = true;
			UserBean session = UserDatabase.getUserLocalSession(mContext);
			setPortrait(session.getPortraitPath());// 设置头像
			user_name.setText(session.getNickName());// 用户名
			bt_logout.setVisibility(View.VISIBLE);// 注销按钮
			// session.getIntroduce()// 简介
		} else {
			isLogin = false;
			ib_userPortrait.setImageResource(R.drawable.user_portrait);// 默认头像
			user_name.setText("登陆");// 用户名
			bt_logout.setVisibility(View.GONE);// 隐藏注销按钮
		}
	}

	@Override
	public void initListener() {
		ib_userPortrait.setOnClickListener(this);// 头像
		user_name.setOnClickListener(this);// 用户名

		ll_my_note.setOnClickListener(this);// 我的笔记
		ll_my_record.setOnClickListener(this);// 我的视频

		ll_guanwang.setOnClickListener(this);// 官网
		ll_tieba.setOnClickListener(this);// 贴吧
		ll_feedback.setOnClickListener(this);// 意见反馈
		ll_about.setOnClickListener(this);// 关于

		bt_logout.setOnClickListener(this);// 注销
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_userPortrait:// 头像
		case R.id.user_name:// 用户名
			if (isLogin) {// 已登录
				mActivity.replacePage(new UserInfoChangePage());
			} else {// 未登录
				toLogin();
			}
			break;
		case R.id.ll_my_note:// 我的笔记
			Log.e("设置Fg", "我的笔记");
			break;
		case R.id.ll_my_record:// 我的视频
			Log.e("设置Fg", "我的视频");
			break;
		case R.id.ll_guanwang:// 官网
			toWebPage("讲讲官网", Const.SPEEKTOOL_SERVER__URL);// 前往 讲讲官网
			break;
		case R.id.ll_tieba:// 贴吧
			toWebPage("讲讲贴吧", Const.SPEEKTOOL_BBS_URL);// 前往 讲讲贴吧
			break;
		case R.id.ll_feedback:// 意见反馈
			mActivity.replacePage(new UcFeedbackPage());
			break;
		case R.id.ll_about:// 关于
			mActivity.replacePage(new UcAboutPage());
			break;
		case R.id.bt_logout:// 注销
			logout();
			break;
		default:
			break;
		}
	}

	/** 注销 */
	private void logout() {
		mLoadingDialog.show("正在退出...");
		final UserBean session = UserDatabase.getUserLocalSession(mContext);
		new Thread(new TaskUserLogout(new UserLogoutCallback() {

			@Override
			public void onSuccess() {// 成功退出
				mLoadingDialog.dismiss();
				initData(getArguments());
				EventBus.getDefault().post(new RefreshCourseListEvent());
				fm.popBackStack();// 退出当前页面
			}

			@Override
			public void onResponseFail() {
				mLoadingDialog.dismiss();
				T.showShort(mContext, "注销失败！");
			}

			@Override
			public void onConnectFail() {
				mLoadingDialog.dismiss();
				T.showShort(mContext, "服务器链接失败！请检查网络");
			}
		}, session)).start();
	}

	private String portraitPath;// 头像路径
	private static LruCache<String, Bitmap> portraitCache = new LruCache<String, Bitmap>(1024 * 500) {
		@Override
		protected int sizeOf(String key, Bitmap value) {
			return value.getHeight() * value.getRowBytes();
		}
	};

	/** 设置头像路径 */
	public void setPortrait(final String imagepath) {
		if (TextUtils.isEmpty(imagepath))
			return;
		portraitPath = imagepath;
		Bitmap cache = portraitCache.get(imagepath);
		if (cache != null) {
			ib_userPortrait.setImageBitmap(cache);
			return;
		}
		if (portraitPath.startsWith("http://")) {
			new Thread(new TaskGetNetImage(new NetImageLoadListener() {
				@Override
				public void onNetImageLoaded(Bitmap result) {
					if (result != null) {
						ib_userPortrait.setImageBitmap(result);
						portraitCache.put(imagepath, result);
					}
				}
			}, portraitPath)).start();

		} else {
			Bitmap bp = BitmapScaleUtil.decodeSampledBitmapFromPath(portraitPath, Const.MAX_MEMORY_BMP_CAN_ALLOCATE);
			ib_userPortrait.setImageBitmap(bp);
			portraitCache.put(imagepath, bp);

		}
	}

	/**
	 * 去登陆界面
	 */
	private void toLogin() {
		UserLoginPage login = new UserLoginPage();
		login.setLoginCallback(new LoginCallback() {
			@Override
			public void onLoginFinish(int resultCode) {
				if (resultCode == LoginCallback.SUCCESS) {
					initData(getArguments());
				} else {
					T.showShort(mContext, "登陆失败");
				}
			}
		});
		mActivity.replacePage(login);
	}

	/** 去新闻页面 */
	private void toWebPage(String title, String url) {
		Intent intent = new Intent(mContext, WebActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(WebActivity.EXTRA_TITLE, title);// 功能Item
		intent.putExtra(WebActivity.EXTRA_URL, url);// 功能Item
		mContext.startActivity(intent);// 开启目标Activity
	}

}