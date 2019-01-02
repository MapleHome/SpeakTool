package com.speaktool.ui.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.speaktool.Const;
import com.speaktool.R;
import com.speaktool.base.BaseFragment;
import com.speaktool.bean.UserBean;
import com.speaktool.busevents.RefreshCourseListEvent;
import com.speaktool.ui.Login.UserLoginPage;
import com.speaktool.view.dialogs.LoadingDialog;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 用户信息界面
 *
 * @author shaoshuai
 */
public class SettingPage extends BaseFragment implements OnClickListener {
    @BindView(R.id.ib_userPortrait) ImageButton ib_userPortrait;// 用户头像
    @BindView(R.id.user_name) TextView user_name;// 用户名

    @BindView(R.id.ll_my_note) LinearLayout ll_my_note;// 我的笔记
    @BindView(R.id.ll_my_record) LinearLayout ll_my_record;// 我的视频

    @BindView(R.id.ll_guanwang) LinearLayout ll_guanwang;// 官网
    @BindView(R.id.ll_tieba) LinearLayout ll_tieba;// 贴吧
    @BindView(R.id.ll_feedback) LinearLayout ll_feedback;// 意见反馈
    @BindView(R.id.ll_about) LinearLayout ll_about;// 关于

    @BindView(R.id.bt_logout) Button bt_logout;// 注销

    private UserFMActivity mActivity;
    private boolean isLogin = false;// 是否登陆
    private LoadingDialog mLoadingDialog;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_user_info;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mActivity = (UserFMActivity) getActivity();
        ButterKnife.bind(this, view);

        mActivity.setTitle("设置");

        mLoadingDialog = new LoadingDialog(mActivity);
        if (false) {
            isLogin = true;
            UserBean session = new UserBean();
            session.setId("7b2d5a11803b4363977bf8923dbd36a6");
            session.setNickName("小可爱");
            String portraitUrl = Const.SPEEKTOOL_SERVER__URL + "userPhoto/7b2d5a11803b4363977bf8923dbd36a6.jpg";
            session.setPortraitPath(portraitUrl);
            session.setIntroduce("更改赫兹日龙");
            session.setEmail("939078792@qq.com");

//            UserBean session = UserDatabase.getUserLocalSession(mContext);
//            setPortrait(session.getPortraitPath());// 设置头像
            user_name.setText(session.getNickName());// 用户名
            bt_logout.setVisibility(View.VISIBLE);// 注销按钮
            // session.getIntroduce()// 简介
        } else {
            isLogin = false;
            ib_userPortrait.setImageResource(R.drawable.user_portrait);// 默认头像
            user_name.setText("登陆");// 用户名
            bt_logout.setVisibility(View.GONE);// 隐藏注销按钮
        }

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
                    mActivity.replaceView(new UserInfoChangePage());
                } else {// 未登录
                    mActivity.replaceView(new UserLoginPage());
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
                mActivity.replaceView(new FeedbackPage());
                break;
            case R.id.ll_about:// 关于
                mActivity.replaceView(new AboutPage());
                break;
            case R.id.bt_logout:// 注销
                logout();
                break;
            default:
                break;
        }
    }

    /**
     * 注销
     */
    private void logout() {
        mLoadingDialog.show("正在退出...");
        mLoadingDialog.dismiss();
        initData(getArguments());
        EventBus.getDefault().post(new RefreshCourseListEvent());

        mActivity.onBackPressed();// 退出当前页面
    }

    /**
     * 去新闻页面
     */
    private void toWebPage(String title, String url) {
        Intent intent = new Intent(mContext, WebActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(WebActivity.EXTRA_TITLE, title);// 功能Item
        intent.putExtra(WebActivity.EXTRA_URL, url);// 功能Item
        mContext.startActivity(intent);// 开启目标Activity
    }

}