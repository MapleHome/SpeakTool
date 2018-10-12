package com.speaktool.view.layouts;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.speaktool.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 更换背景 Item
 *
 * @author shaoshuai
 */
public class ItemViewChooseBackgroundPop extends FrameLayout {
    @BindView(R.id.ivIcon) ImageView ivIcon;// 图片
    @BindView(R.id.tvNote) TextView tvNote;// 说明
    @BindView(R.id.ivChooseState) ImageView ivChooseState;// 是否选中

    private String note = "";
    private int iconResid;
    private boolean isCheck = false;

    public ItemViewChooseBackgroundPop(Context context) {
        super(context);
        init();
    }

    public ItemViewChooseBackgroundPop(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.itemSelectBg);
        note = a.getString(R.styleable.itemSelectBg_itemSelectBg_note);
        iconResid = a.getResourceId(R.styleable.itemSelectBg_itemSelectBg_icon, -1);
        isCheck = a.getBoolean(R.styleable.itemSelectBg_itemSelectBg_isCheck, false);
        a.recycle();
        init();
    }

    public ItemViewChooseBackgroundPop(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.itemSelectBg);
        note = a.getString(R.styleable.itemSelectBg_itemSelectBg_note);
        iconResid = a.getResourceId(R.styleable.itemSelectBg_itemSelectBg_icon, -1);
        isCheck = a.getBoolean(R.styleable.itemSelectBg_itemSelectBg_isCheck, false);
        a.recycle();
        init();
    }

    private void init() {
        View view = View.inflate(getContext(), R.layout.item_choosebackground_pop, this);
        ButterKnife.bind(this, view);

        if (iconResid != -1)
            ivIcon.setImageResource(iconResid);
        if (!TextUtils.isEmpty(note))
            tvNote.setText(note);
        setCheckState(isCheck);
    }

    public void setCheckState(boolean isChecked) {
        if (isChecked)
            ivChooseState.setVisibility(View.VISIBLE);
        else
            ivChooseState.setVisibility(View.INVISIBLE);
    }
}
