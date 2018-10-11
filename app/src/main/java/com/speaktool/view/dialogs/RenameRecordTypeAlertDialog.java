package com.speaktool.view.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.speaktool.R;
import com.speaktool.bean.SearchCategoryBean;
import com.speaktool.utils.T;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RenameRecordTypeAlertDialog extends Dialog implements View.OnClickListener {
    @BindView(R.id.btnCancel) Button btnCancel;
    @BindView(R.id.btnOk) Button btnOk;
    @BindView(R.id.etRenameRecordType) EditText etRenameRecordType;

    private SearchCategoryBean mRecordType;
    private Context mActivityContext;

    public RenameRecordTypeAlertDialog(Context context, SearchCategoryBean type) {
        this(context, R.style.dialogTheme, type);
    }

    public RenameRecordTypeAlertDialog(Context context, int theme, SearchCategoryBean type) {
        super(context, theme);
        setCanceledOnTouchOutside(false);
        mActivityContext = context;
        mRecordType = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_recordtype_rename_alert);
        ButterKnife.bind(this);

        btnCancel.setOnClickListener(this);
        btnOk.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        this.dismiss();
    }

    @Override
    public void onClick(View v) {
        if (v == btnOk) {
            String renametype = etRenameRecordType.getText().toString();
            if (TextUtils.isEmpty(renametype)) {
                T.showShort(mActivityContext, "输入分类不能为空!");
                return;
            }
            mRecordType.setCategoryName(renametype);
            this.dismiss();
        } else if (v == btnCancel) {
            this.dismiss();
        }
    }
}
