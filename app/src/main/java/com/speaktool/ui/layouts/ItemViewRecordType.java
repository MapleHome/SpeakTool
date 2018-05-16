package com.speaktool.ui.layouts;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.speaktool.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 记录类型
 *
 * @author Maple Shao
 */
public class ItemViewRecordType extends FrameLayout {
    @BindView(R.id.tvTypeName) TextView tvTypeName;// 类型名称

    public ItemViewRecordType(Context context) {
        super(context);
        init();
    }

    private void init() {
        View view = View.inflate(getContext(), R.layout.item_record_type, this);
        ButterKnife.bind(this, view);
    }


    public void setTypeName(String typeName) {
        tvTypeName.setText(typeName);
    }
}
