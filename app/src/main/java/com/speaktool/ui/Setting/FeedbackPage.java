package com.speaktool.ui.Setting;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.speaktool.R;
import com.speaktool.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 意见反馈
 *
 * @author shao
 */
public class FeedbackPage extends BaseFragment {
    @BindView(R.id.et_fb_content) EditText et_fb_content;// 反馈内容
    @BindView(R.id.et_fb_contact) EditText et_fb_contact;// 联系方式
    @BindView(R.id.bt_fb_submit) Button bt_fb_submit;// 提交

    private UserFMActivity mActivity;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_uc_feedback;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mActivity = (UserFMActivity) getActivity();
        ButterKnife.bind(this, view);
        mActivity.setTitle("意见反馈");

        // 提交反馈
        bt_fb_submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            }
        });
    }

}
