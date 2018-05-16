package com.speaktool.ui.popupwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.speaktool.R;
import com.speaktool.bean.SearchCategoryBean;
import com.speaktool.ui.adapters.AdapterSearchRecordTypes;
import com.speaktool.ui.base.BasePopupWindow;

import java.util.List;

/**
 * 分类列表下拉框
 *
 * @author shaoshuai
 */
public class CategoryPoW extends BasePopupWindow implements OnItemClickListener {

    private ListView lvTypes;// 类型列表
    private AdapterSearchRecordTypes mAdapterSearchSpinnerTypes;

    @Override
    public View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.pow_searchbox_dropdown, null);
    }

    public CategoryPoW(Context context, View token, View anchor, SearchCategoryChangedListener lsn) {
        this(context, token, anchor, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, lsn);
    }

    public CategoryPoW(Context context, View token, View anchor, int w, int h, SearchCategoryChangedListener lsn) {
        super(context, token, anchor, w, h);
        mSearchCategoryChangedListener = lsn;

        lvTypes = (ListView) mRootView.findViewById(R.id.lvTypes);

        mAdapterSearchSpinnerTypes = new AdapterSearchRecordTypes(context, null);
        lvTypes.setAdapter(mAdapterSearchSpinnerTypes);
        // init.
        lvTypes.setOnItemClickListener(this);
    }

    public void refreshCategoryList(final List<SearchCategoryBean> datas) {
        mAdapterSearchSpinnerTypes.refresh(datas);
    }

    private SearchCategoryChangedListener mSearchCategoryChangedListener;

    public static interface SearchCategoryChangedListener {
        void onSearchCategoryChanged(SearchCategoryBean categoryNew);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SearchCategoryBean category = (SearchCategoryBean) parent.getAdapter().getItem(position);

        if (mSearchCategoryChangedListener != null) {
            mSearchCategoryChangedListener.onSearchCategoryChanged(category);
        }
        this.dismiss();
    }
}
