package com.speaktool.ui.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.speaktool.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 填写保存记录信息
 *
 * @author shaoshuai
 */
public class FillSaveRecordInfoEditPage extends FrameLayout {
    @BindView(R.id.ivCancel) ImageView ivCancel;// 关闭
    @BindView(R.id.ivOk) ImageView ivOk;// 完成

    @BindView(R.id.etTitle) EditText etTitle;// 标题
    @BindView(R.id.etTab) EditText etTab;// 标签
    @BindView(R.id.tv_type) TextView tv_type;// 分类
    @BindView(R.id.etIntroduce) EditText etIntroduce;// 简介

    @BindView(R.id.ivCheckbox) CheckBoxImageView ivCheckbox;// 是否公开发布

    public FillSaveRecordInfoEditPage(Context context) {
        super(context);
        init();
    }

    public FillSaveRecordInfoEditPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FillSaveRecordInfoEditPage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        View view = View.inflate(getContext(), R.layout.dialog_fill_saveinfo_firstpage, this);
        ButterKnife.bind(this, view);

    }

    /**
     * 设置关闭按钮点击监听
     */
    public void setCancelClickListener(OnClickListener lsn) {
        ivCancel.setOnClickListener(lsn);
    }

    /**
     * 设置完成按钮点击监听
     */
    public void setOkClickListener(OnClickListener lsn) {
        ivOk.setOnClickListener(lsn);
    }

    /**
     * 设置分类点击监听
     */
    public void setEditTypeTouchListener(OnClickListener lsn) {
        tv_type.setOnClickListener(lsn);
    }

    /**
     * 设置类型
     */
    public void setType(String type) {
        tv_type.setText(type);
    }

    /**
     * 获取标题
     */
    public String getTitle() {
        return etTitle.getText().toString();
    }

    /**
     * 获取标签
     */
    public String getTab() {
        return etTab.getText().toString();
    }

    /**
     * 获取类型
     */
    public String getType() {
        return tv_type.getText().toString();
    }

    /**
     * 获取简介
     */
    public String getIntroduce() {
        return etIntroduce.getText().toString();
    }

    /**
     * 是否公开发布
     */
    public boolean isPublicPublish() {
        return ivCheckbox.isChecked();
    }
}
