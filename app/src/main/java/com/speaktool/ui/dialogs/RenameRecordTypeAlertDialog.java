package com.speaktool.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.common.base.Preconditions;
import com.speaktool.R;
import com.speaktool.bean.SearchCategoryBean;
import com.speaktool.dao.RecordCategoriesDatabase;

public class RenameRecordTypeAlertDialog extends Dialog implements View.OnClickListener {

	private Button btnCancel;
	private Button btnOk;
	private SearchCategoryBean mRecordType;
	private EditText etRenameRecordType;
	private Context mActivityContext;

	public RenameRecordTypeAlertDialog(Context context, SearchCategoryBean type) {
		this(context, R.style.dialogTheme, type);
	}

	public RenameRecordTypeAlertDialog(Context context, int theme, SearchCategoryBean type) {
		super(context, theme);
		Preconditions.checkArgument(context instanceof Activity, "context must be Activity in Dialog.");
		mActivityContext = context;
		mRecordType = type;
		init();
	}

	private void init() {
		this.setCanceledOnTouchOutside(false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.dialog_recordtype_rename_alert);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);
		btnOk = (Button) findViewById(R.id.btnOk);
		btnOk.setOnClickListener(this);
		etRenameRecordType = (EditText) findViewById(R.id.etRenameRecordType);
		super.onCreate(savedInstanceState);
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
				OneButtonAlertDialog dia = new OneButtonAlertDialog(mActivityContext, "输入分类不能为空!");
				dia.show();
				return;
			}
			mRecordType.setCategoryName(renametype);
			RecordCategoriesDatabase.updateCategory(mRecordType, getContext());
			this.dismiss();
		} else if (v == btnCancel) {
			this.dismiss();
		}
	}
}
