package com.ishare_lib.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.ishare_lib.R;
import com.ishare_lib.utils.DeviceUtils;

/**
 * 警告样式会话框 （标题+输入内容+消极按钮+积极按钮）
 * 
 * @author shaoshuai
 * 
 */
public class AlertEditTextDialog {
	private LinearLayout lLayout_bg;// 跟布局
	private TextView txt_title;// 标题
	private TextView txt_msg;// 消息
	private EditText et_text;// 输入内容
	private Button btn_left;// 左按钮
	private Button btn_right;// 右按钮

	private Context mContext;
	private Dialog dialog;
	/** 显示标题 */
	private boolean showTitle = false;
	/** 显示消息内容 */
	private boolean showMsg = false;

	public AlertEditTextDialog(Context context) {
		this.mContext = context;
	}

	/** 生成器 */
	public AlertEditTextDialog builder() {
		// 获取Dialog布局
		View view = LayoutInflater.from(mContext).inflate(R.layout.view_dialog_editdialog, null);

		// 获取自定义Dialog布局中的控件
		lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
		txt_title = (TextView) view.findViewById(R.id.txt_title);
		txt_msg = (TextView) view.findViewById(R.id.txt_msg);
		et_text = (EditText) view.findViewById(R.id.et_text);
		btn_left = (Button) view.findViewById(R.id.btn_left);
		btn_right = (Button) view.findViewById(R.id.btn_right);

		txt_title.setVisibility(View.GONE);
		txt_msg.setVisibility(View.GONE);
		et_text.setHint("");
		// 定义Dialog布局和参数
		dialog = new Dialog(mContext, R.style.AlertDialogStyle);
		dialog.setContentView(view);
		// 调整dialog背景大小
		int width = (int) (DeviceUtils.getScreenWidth(mContext) * 0.85);
		if (DeviceUtils.isHengPing(mContext)) {// 横屏
			width = (int) (DeviceUtils.getScreenWidth(mContext) * 0.5);
		}
		lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT));

		return this;
	}

	/** 设置标题 */
	public AlertEditTextDialog setTitle(String title) {
		showTitle = true;
		if ("".equals(title)) {
			txt_title.setText("提示");
		} else {
			txt_title.setText(title);
		}
		return this;
	}

	/** 设置消息内容 */
	public AlertEditTextDialog setMsg(String msg) {
		showMsg = true;
		if ("".equals(msg)) {
			txt_msg.setText("消息内容");
		} else {
			txt_msg.setText(msg);
		}
		return this;
	}

	/** 设置消息内容 */
	public AlertEditTextDialog setHint(String hint) {
		showMsg = true;
		if ("".equals(hint)) {
			et_text.setHint("请输入内容：");
		} else {
			et_text.setHint(hint);
		}
		return this;
	}

	/** 可销毁的（点击dialog以外区域销毁） 默认true */
	public AlertEditTextDialog setCancelable(boolean cancel) {
		dialog.setCancelable(cancel);
		return this;
	}

	/** 输入文本回调接口 */
	public static interface EditInputTextCallback {
		/** 获取输入文本 */
		void getInputText(String text);
	}

	/** 设置积极按钮 */
	public AlertEditTextDialog setPositiveButton(final EditInputTextCallback listener) {
		btn_right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = et_text.getText().toString().trim();
				listener.getInputText(text);

				dialog.dismiss();
			}
		});
		return this;
	}

	/** 设置布局 */
	private void setLayout() {
		// 标题、内容都为空，显示默认标题
		if (!showTitle && !showMsg) {
			txt_title.setText("提示");
			txt_title.setVisibility(View.VISIBLE);
		}
		// 有标题-显示标题
		if (showTitle) {
			txt_title.setVisibility(View.VISIBLE);
		}
		// 有内容-显示内容
		if (showMsg) {
			txt_msg.setVisibility(View.VISIBLE);
		}
		// 显示左右按钮
		btn_right.setVisibility(View.VISIBLE);
		btn_right.setBackgroundResource(R.drawable.selector_dialog_btmright_fillet_bg);// 右下圆角
		btn_left.setVisibility(View.VISIBLE);
		btn_left.setBackgroundResource(R.drawable.selector_dialog_btmleft_fillet_bg);// 左下圆角
		//

		btn_left.setText("取消");
		btn_left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		btn_right.setText("确定");
		
	}

	public void show() {
		setLayout();
		dialog.show();
	}
}
