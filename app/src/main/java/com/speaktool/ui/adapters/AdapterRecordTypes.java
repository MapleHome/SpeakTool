package com.speaktool.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.speaktool.bean.SearchCategoryBean;
import com.speaktool.ui.base.AbsAdapter;
import com.speaktool.view.layouts.ItemViewRecordType;

import java.util.List;

/**
 * 课程记录类型
 *
 * @author shaoshuai
 */
public class AdapterRecordTypes extends AbsAdapter<SearchCategoryBean> {

    public AdapterRecordTypes(Context ctx, List<SearchCategoryBean> datas) {
        super(ctx, datas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new ItemViewRecordType(mContext);
        }
        ItemViewRecordType itemview = (ItemViewRecordType) convertView;
        String typename = ((SearchCategoryBean) getItem(position)).getCategoryName();
        if (typename == null)
            return itemview;
        itemview.setTypeName(typename);

        return itemview;
    }

}
