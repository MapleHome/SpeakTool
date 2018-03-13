package com.speaktool.ui.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.speaktool.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 新增分类
 *
 * @author shaoshuai
 */
public class FillSaveRecordInfoAddKindPage extends FrameLayout {
    @BindView(R.id.ivBackThird) ImageView ivBack;// 返回
    @BindView(R.id.ivAddNewType) ImageView ivAddNewType;// 完成
    @BindView(R.id.etNewtype) EditText etNewtype;// 新分类名称

    public FillSaveRecordInfoAddKindPage(Context context) {
        super(context);
        init();
    }

    public FillSaveRecordInfoAddKindPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FillSaveRecordInfoAddKindPage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        View view = View.inflate(getContext(), R.layout.dialog_fill_saveinfo_thirdpage, this);
        ButterKnife.bind(this, view);
    }

    public void setBackClickListener(OnClickListener lsn) {
        ivBack.setOnClickListener(lsn);
    }

    public void setAddNewTypeClickListener(OnClickListener lsn) {
        ivAddNewType.setOnClickListener(lsn);
    }

    public String getInputNewType() {
        return etNewtype.getText().toString();
    }

}
