package com.speektool.ui.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.speektool.R;

/**
 * 填写保存记录信息
 * 
 * @author shaoshuai
 * 
 */
public class FillSaveRecordInfoEditPage extends FrameLayout {
	@ViewInject(R.id.ivCancel)
	private ImageView ivCancel;// 关闭
	@ViewInject(R.id.ivOk)
	private ImageView ivOk;// 完成

	@ViewInject(R.id.etTitle)
	private EditText etTitle;// 标题
	@ViewInject(R.id.etTab)
	private EditText etTab;// 标签
	@ViewInject(R.id.tv_type)
	private TextView tv_type;// 分类
	@ViewInject(R.id.etIntroduce)
	private EditText etIntroduce;// 简介

	@ViewInject(R.id.ivCheckbox)
	private CheckBoxImageView ivCheckbox;// 是否公开发布

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
		ViewUtils.inject(this, view);

	}

	/** 设置关闭按钮点击监听 */
	public void setCancelClickListener(OnClickListener lsn) {
		ivCancel.setOnClickListener(lsn);
	}

	/** 设置完成按钮点击监听 */
	public void setOkClickListener(OnClickListener lsn) {
		ivOk.setOnClickListener(lsn);
	}

	/** 设置分类点击监听 */
	public void setEditTypeTouchListener(OnClickListener lsn) {
		tv_type.setOnClickListener(lsn);
	}

	/** 设置类型 */
	public void setType(String type) {
		tv_type.setText(type);
	}

	/** 获取标题 */
	public String getTitle() {
		return etTitle.getText().toString();
	}

	/** 获取标签 */
	public String getTab() {
		return etTab.getText().toString();
	}

	/** 获取类型 */
	public String getType() {
		return tv_type.getText().toString();
	}

	/** 获取简介 */
	public String getIntroduce() {
		return etIntroduce.getText().toString();
	}

	/** 是否公开发布 */
	public boolean isPublicPublish() {
		return ivCheckbox.isChecked();
	}
}
