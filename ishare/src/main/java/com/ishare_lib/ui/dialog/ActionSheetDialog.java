package com.ishare_lib.ui.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ishare_lib.R;
import com.ishare_lib.utils.DeviceUtils;

/**
 * 动作页签样式会话框 （消息内容+动作页签+取消按钮）
 * 
 * @author shaoshuai
 * 
 */
public class ActionSheetDialog {
	private TextView txt_title;// 标题
	private ScrollView sLayout_content;
	private LinearLayout lLayout_content;
	private TextView txt_cancel;// 取消

	private Context mContext;
	private Dialog dialog;
	private List<SheetItem> sheetItemList;
	private boolean showTitle = false;

	public ActionSheetDialog(Context context) {
		this.mContext = context;
	}

	/** 生成器 */
	public ActionSheetDialog builder() {
		// 获取Dialog布局
		View view = LayoutInflater.from(mContext).inflate(R.layout.view_dialog_actionsheet, null);

		// 设置Dialog最小宽度为屏幕宽度
		view.setMinimumWidth(DeviceUtils.getScreenWidth(mContext));
		// 获取自定义Dialog布局中的控件
		sLayout_content = (ScrollView) view.findViewById(R.id.sLayout_content);
		lLayout_content = (LinearLayout) view.findViewById(R.id.lLayout_content);
		txt_title = (TextView) view.findViewById(R.id.txt_title);
		txt_cancel = (TextView) view.findViewById(R.id.txt_cancel);

		txt_title.setVisibility(View.GONE);
		txt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		// 定义Dialog布局和参数
		dialog = new Dialog(mContext, R.style.ActionSheetDialogStyle);
		dialog.setContentView(view);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.x = 0;
		lp.y = 0;
		dialogWindow.setAttributes(lp);

		return this;
	}

	/** 设置标题 */
	public ActionSheetDialog setTitle(String title) {
		showTitle = true;
		txt_title.setVisibility(View.VISIBLE);
		txt_title.setText(title);
		return this;
	}

	/** 可销毁的（点击dialog以外区域销毁） 默认true */
	public ActionSheetDialog setCancelable(boolean cancel) {
		dialog.setCancelable(cancel);
		return this;
	}

	public ActionSheetDialog setCanceledOnTouchOutside(boolean cancel) {
		dialog.setCanceledOnTouchOutside(cancel);
		return this;
	}

	/**
	 * 添加动作页签
	 * 
	 * @param strItem
	 *            条目名称
	 * @param color
	 *            条目字体颜色，设置null则默认蓝色
	 * @param listener
	 *            动作监听
	 * @return
	 */
	public ActionSheetDialog addSheetItem(String strItem, SheetItemColor color, OnSheetItemClickListener listener) {
		if (sheetItemList == null) {
			sheetItemList = new ArrayList<SheetItem>();
		}
		sheetItemList.add(new SheetItem(strItem, color, listener));
		return this;
	}

	/** 设置条目布局 */
	private void setSheetItems() {
		if (sheetItemList == null || sheetItemList.size() <= 0) {
			return;
		}
		int size = sheetItemList.size();
		// TODO 高度控制，非最佳解决办法
		// 添加条目过多的时候控制高度
		if (size >= 7) {
			LinearLayout.LayoutParams params = (LayoutParams) sLayout_content.getLayoutParams();
			params.height = DeviceUtils.getScreenHeight(mContext) / 2;
			sLayout_content.setLayoutParams(params);
		}
		// 循环添加条目
		for (int i = 1; i <= size; i++) {
			final int index = i;
			SheetItem sheetItem = sheetItemList.get(i - 1);
			String strItem = sheetItem.name;
			SheetItemColor color = sheetItem.color;
			final OnSheetItemClickListener listener = (OnSheetItemClickListener) sheetItem.itemClickListener;

			TextView textView = new TextView(mContext);
			textView.setText(strItem);
			textView.setTextSize(18);
			textView.setGravity(Gravity.CENTER);
			// 背景图片

			if (size == 1) {
				if (showTitle) {
					textView.setBackgroundResource(R.drawable.selector_dialog_btm_fillet_border_bg);// 底部圆角
				} else {
					textView.setBackgroundResource(R.drawable.selector_dialog_fillet_border_bg);// 全圆角
				}
			} else {
				if (showTitle) {
					if (i >= 1 && i < size) {
						textView.setBackgroundResource(R.drawable.selector_dialog_border_bg);// 矩形
					} else {
						textView.setBackgroundResource(R.drawable.selector_dialog_btm_fillet_border_bg);// 底部圆角
					}
				} else {
					if (i == 1) {
						textView.setBackgroundResource(R.drawable.selector_dialog_top_fillet_border_bg);// 顶部圆角
					} else if (i < size) {
						textView.setBackgroundResource(R.drawable.selector_dialog_border_bg);// 矩形
					} else {
						textView.setBackgroundResource(R.drawable.selector_dialog_btm_fillet_border_bg);// 底部圆角
					}
				}
			}

			// 字体颜色
			if (color == null) {
				textView.setTextColor(Color.parseColor(SheetItemColor.Blue.getName()));
			} else {
				textView.setTextColor(Color.parseColor(color.getName()));
			}

			// 高度
			float scale = mContext.getResources().getDisplayMetrics().density;
			int height = (int) (45 * scale + 0.5f);
			textView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, height));

			// 点击事件
			textView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					listener.onClick(index);
					dialog.dismiss();
				}
			});

			lLayout_content.addView(textView);
		}
	}

	public void show() {
		setSheetItems();
		dialog.show();
	}

	public interface OnSheetItemClickListener {
		void onClick(int which);
	}

	/**
	 * 动作页签条目
	 * 
	 * @author shaoshuai
	 * 
	 */
	public class SheetItem {
		String name;// 页签名称
		OnSheetItemClickListener itemClickListener;// 动作监听
		SheetItemColor color;// 颜色

		public SheetItem(String name, SheetItemColor color, OnSheetItemClickListener itemClickListener) {
			this.name = name;
			this.color = color;
			this.itemClickListener = itemClickListener;
		}
	}

	/**
	 * 动作页签条目颜色
	 * 
	 * @author shaoshuai
	 * 
	 */
	public enum SheetItemColor {
		Blue("#037BFF"), Red("#FD4A2E");

		private String name;

		private SheetItemColor(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
