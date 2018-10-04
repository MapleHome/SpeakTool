package com.speaktool.view.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.speaktool.R;
import com.speaktool.view.swipemenu.SwipeMenuListView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 记录信息——选择已有分类列表页
 *
 * @author shaoshuai
 */
public class FillSaveRecordInfoKindListPage extends FrameLayout {
    @BindView(R.id.ivBackSecond) ImageView ivBack;// 返回
    @BindView(R.id.ivNewType) ImageView ivNewType;// 添加新分类
    @BindView(R.id.listViewTypes) SwipeMenuListView listViewTypes;// 已有分类列表

    public FillSaveRecordInfoKindListPage(Context context) {
        super(context);
        init();
    }

    public FillSaveRecordInfoKindListPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FillSaveRecordInfoKindListPage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        View view = View.inflate(getContext(), R.layout.dialog_fill_saveinfo_secondpage, this);
        ButterKnife.bind(this, view);

    }

    /**
     * 设置返回按钮点击监听
     */
    public void setBackClickListener(OnClickListener lsn) {
        ivBack.setOnClickListener(lsn);
    }

    /**
     * 设置添加新分类按钮点击监听
     */
    public void setNewTypeClickListener(OnClickListener lsn) {
        ivNewType.setOnClickListener(lsn);
    }

    /**
     * 已有分类列表点击监听
     */
    public void setListItemClickListener(OnItemClickListener lsn) {
        listViewTypes.setOnItemClickListener(lsn);
    }

    public void setAdapter(BaseAdapter adp) {
        listViewTypes.setAdapter(adp);
    }

    public SwipeMenuListView getListView() {
        return listViewTypes;
    }

}
