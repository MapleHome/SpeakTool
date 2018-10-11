package com.speaktool.ui.Setting;

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

import com.speaktool.Const;
import com.speaktool.R;
import com.speaktool.bean.UserBean;
import com.speaktool.busevents.RefreshCourseListEvent;
import com.speaktool.dao.UserDatabase;
import com.speaktool.tasks.TaskGetNetImage;
import com.speaktool.tasks.TaskGetNetImage.NetImageLoadListener;
import com.speaktool.ui.Login.UserLoginPage;
import com.speaktool.ui.base.BaseFragment;
import com.speaktool.view.dialogs.LoadingDialog;
import com.speaktool.utils.BitmapScaleUtil;

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
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_user_info, null);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mActivity = (UserFMActivity) getActivity();
        mActivity.setTitle("设置");

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
                mActivity.replacePage(new FeedbackPage());
                break;
            case R.id.ll_about:// 关于
                mActivity.replacePage(new AboutPage());
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
        fm.popBackStack();// 退出当前页面

    }

    private String portraitPath;// 头像路径
    private static LruCache<String, Bitmap> portraitCache = new LruCache<String, Bitmap>(1024 * 500) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getHeight() * value.getRowBytes();
        }
    };

    /**
     * 设置头像路径
     */
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
        mActivity.replacePage(new UserLoginPage());
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