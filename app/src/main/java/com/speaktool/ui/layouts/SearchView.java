package com.speaktool.ui.layouts;

import roboguice.inject.InjectView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.speaktool.R;
import com.speaktool.bean.SearchCategoryBean;
import com.speaktool.injectmodules.IInject;
import com.speaktool.injectmodules.Layout;
import com.speaktool.injectmodules.ViewInjectUtils;

/**
 * 搜索视图
 * 
 * @author shaoshuai
 * 
 */
@Layout(R.layout.search_layout)
public class SearchView extends FrameLayout implements IInject {
	@InjectView(R.id.layDropdownHandle)
	private View layDropdownHandle;// 类型选择- 根视图
	@InjectView(R.id.tvCheckedType)
	private TextView tvCheckedType;// 课程类型

	@InjectView(R.id.etSearch)
	private EditText etSearch;// 搜索输入框
	@InjectView(R.id.ivSearch)
	private ImageView ivSearch;// 搜索按钮

	private SearchCategoryBean mSearchCategory;

	public SearchView(Context context) {
		super(context);
		init();
	}

	public SearchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SearchView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		startInject();
		afterInject();
	}

	@Override
	public void startInject() {
		ViewInjectUtils.injectViews(this);
	}

	@Override
	public void afterInject() {
	}

	public void setDropdownClickListener(OnClickListener lsn) {
		layDropdownHandle.setOnClickListener(lsn);
	}

	public void setSearchClickListener(OnClickListener lsn) {
		ivSearch.setOnClickListener(lsn);
	}

	/** 获取搜索关键字 */
	public String getSearchKeywords() {
		return etSearch.getText().toString();
	}

	/** 设置搜索关键字 */
	public void setSearchKey(String key) {
		etSearch.setText(key);
	}

	/** 获取课程记录 类型 */
	public SearchCategoryBean getCategory() {
		return mSearchCategory;
	}

	/** 设置分类 */
	public void setCategory(SearchCategoryBean category) {
		tvCheckedType.setText(category.getCategoryName());
		mSearchCategory = category;
	}
}
