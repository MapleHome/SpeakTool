package com.speaktool.view.popupwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.speaktool.R;
import com.speaktool.bean.SearchCategoryBean;
import com.speaktool.ui.adapters.AdapterSearchRecordTypes;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 分类列表下拉框
 *
 * @author shaoshuai
 */
public class CategoryPoW extends BasePopupWindow implements OnItemClickListener {
    @BindView(R.id.lvTypes) ListView lvTypes;// 类型列表

    private AdapterSearchRecordTypes mAdapterSearchSpinnerTypes;
    private SearchCategoryChangedListener mSearchCategoryChangedListener;

    @Override
    public View getContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.pow_searchbox_dropdown, null);
        ButterKnife.bind(this, view);
        return view;
    }

    public CategoryPoW(Context context, View token, View anchor, SearchCategoryChangedListener lsn) {
        super(context, token, anchor);
        mSearchCategoryChangedListener = lsn;

        mAdapterSearchSpinnerTypes = new AdapterSearchRecordTypes(context, null);
        lvTypes.setAdapter(mAdapterSearchSpinnerTypes);
        // init.
        lvTypes.setOnItemClickListener(this);
    }

    public interface SearchCategoryChangedListener {
        void onSearchCategoryChanged(SearchCategoryBean categoryNew);
    }

    public void refreshCategoryList(List<SearchCategoryBean> datas) {
        mAdapterSearchSpinnerTypes.refresh(datas);
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
