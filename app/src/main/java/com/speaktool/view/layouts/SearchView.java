package com.speaktool.view.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.speaktool.R;
import com.speaktool.bean.SearchCategoryBean;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 搜索视图
 *
 * @author shaoshuai
 */
public class SearchView extends FrameLayout {
    @BindView(R.id.layDropdownHandle) View layDropdownHandle;// 类型选择- 根视图
    @BindView(R.id.tvCheckedType) TextView tvCheckedType;// 课程类型

    @BindView(R.id.etSearch) EditText etSearch;// 搜索输入框
    @BindView(R.id.ivSearch) ImageView ivSearch;// 搜索按钮

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
        View view = View.inflate(getContext(), R.layout.search_layout, this);
        ButterKnife.bind(this, view);
    }


    public void setDropdownClickListener(OnClickListener lsn) {
        layDropdownHandle.setOnClickListener(lsn);
    }

    public void setSearchClickListener(OnClickListener lsn) {
        ivSearch.setOnClickListener(lsn);
    }

    /**
     * 获取搜索关键字
     */
    public String getSearchKeywords() {
        return etSearch.getText().toString();
    }

    /**
     * 设置搜索关键字
     */
    public void setSearchKey(String key) {
        etSearch.setText(key);
    }

    /**
     * 获取课程记录 类型
     */
    public SearchCategoryBean getCategory() {
        return mSearchCategory;
    }

    /**
     * 设置分类
     */
    public void setCategory(SearchCategoryBean category) {
        tvCheckedType.setText(category.getCategoryName());
        mSearchCategory = category;
    }
}
