package com.ishare_lib.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.ishare_lib.R;
import com.ishare_lib.utils.DeviceUtils;

/**
 * 警告样式会话框 （标题+消息内容+消极按钮+积极按钮）
 * 
 * @author shaoshuai
 * 
 */
public class AlertDialog {
	private LinearLayout lLayout_bg;// 跟布局
	private TextView txt_title;// 标题
	private TextView txt_msg;// 消息

	private Button btn_neg;// 左按钮
	private ImageView img_line;// 中间线
	private Button btn_pos;// 右按钮

	private Context mContext;
	private Dialog dialog;
	/** 显示标题 */
	private boolean showTitle = false;
	/** 显示消息内容 */
	private boolean showMsg = false;
	/** 显示消极按钮 */
	private boolean showNegBtn = false;
	/** 显示积极按钮 */
	private boolean showPosBtn = false;

	public AlertDialog(Context context) {
		this.mContext = context;

	}

	/** 生成器 */
	public AlertDialog builder() {
		// 获取Dialog布局
		View view = LayoutInflater.from(mContext).inflate(R.layout.view_dialog_alertdialog, null);

		// 获取自定义Dialog布局中的控件
		lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
		txt_title = (TextView) view.findViewById(R.id.txt_title);
		txt_msg = (TextView) view.findViewById(R.id.txt_msg);
		btn_neg = (Button) view.findViewById(R.id.btn_neg);
		btn_pos = (Button) view.findViewById(R.id.btn_pos);
		img_line = (ImageView) view.findViewById(R.id.img_line);

		txt_title.setVisibility(View.GONE);
		txt_msg.setVisibility(View.GONE);
		btn_neg.setVisibility(View.GONE);
		btn_pos.setVisibility(View.GONE);
		img_line.setVisibility(View.GONE);

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
	public AlertDialog setTitle(String title) {
		showTitle = true;
		if ("".equals(title)) {
			txt_title.setText("标题");
		} else {
			txt_title.setText(title);
		}
		return this;
	}

	/** 设置消息内容 */
	public AlertDialog setMsg(String msg) {
		showMsg = true;
		if ("".equals(msg)) {
			txt_msg.setText("内容");
		} else {
			txt_msg.setText(msg);
		}
		return this;
	}

	/** 可销毁的（点击dialog以外区域销毁） 默认true */
	public AlertDialog setCancelable(boolean cancel) {
		dialog.setCancelable(cancel);
		return this;
	}

	/** 设置积极按钮 */
	public AlertDialog setPositiveButton(String text, final OnClickListener listener) {
		showPosBtn = true;
		if ("".equals(text)) {
			btn_pos.setText("确定");
		} else {
			btn_pos.setText(text);
		}
		btn_pos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onClick(v);
				dialog.dismiss();
			}
		});
		return this;
	}

	/** 设置消极按钮 */
	public AlertDialog setNegativeButton(String text, final OnClickListener listener) {
		showNegBtn = true;
		if ("".equals(text)) {
			btn_neg.setText("取消");
		} else {
			btn_neg.setText(text);
		}
		btn_neg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onClick(v);
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
		// 左右按钮都为空，显示单个确认按钮
		if (!showPosBtn && !showNegBtn) {
			btn_pos.setText("确定");
			btn_pos.setVisibility(View.VISIBLE);
			btn_pos.setBackgroundResource(R.drawable.selector_dialog_btm_fillet_border_bg);// 底部圆角
			btn_pos.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
		}
		// 显示左右按钮
		if (showPosBtn && showNegBtn) {
			btn_pos.setVisibility(View.VISIBLE);
			btn_pos.setBackgroundResource(R.drawable.selector_dialog_btmright_fillet_bg);// 右下圆角
			btn_neg.setVisibility(View.VISIBLE);
			btn_neg.setBackgroundResource(R.drawable.selector_dialog_btmleft_fillet_bg);// 左下圆角
			img_line.setVisibility(View.VISIBLE);
		}
		// 只显示积极按钮
		if (showPosBtn && !showNegBtn) {
			btn_pos.setVisibility(View.VISIBLE);
			btn_pos.setBackgroundResource(R.drawable.selector_dialog_btm_fillet_border_bg);// 底部圆角
		}
		// 只显示消极按钮
		if (!showPosBtn && showNegBtn) {
			btn_neg.setVisibility(View.VISIBLE);
			btn_neg.setBackgroundResource(R.drawable.selector_dialog_btm_fillet_border_bg);// 底部圆角
		}
	}

	public void show() {
		setLayout();
		dialog.show();
	}
}
