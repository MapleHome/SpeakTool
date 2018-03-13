package com.speaktool.ui.popupwindow;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.speaktool.R;
import com.speaktool.ui.adapters.AdapterColors;
import com.speaktool.ui.base.BasePopupWindow;
import com.speaktool.bean.PaintInfoBean;
import com.speaktool.impl.shapes.EditWidget;
import com.speaktool.tasks.TaskLoadPaintColors;
import com.speaktool.tasks.TaskLoadPaintColors.Callback;

/**
 * 选择字体颜色
 * 
 * @author shaoshuai
 * 
 */
public class PickFontColorsPoW extends BasePopupWindow implements OnItemClickListener, Callback {
	private GridView gridViewColors;
	private AdapterColors adapter;
	
	private EditWidget mEditWidget;

	@Override
	public View getContentView() {
		return LayoutInflater.from(mContext).inflate(R.layout.pow_pickfontcolor, null);
	}

	public PickFontColorsPoW(Context context, View token, View anchor, EditWidget edit) {
		this(context, token, anchor, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, edit);
	}

	public PickFontColorsPoW(Context context, View token, View anchor, int w, int h, EditWidget edit) {
		super(context, token, anchor, w, h);
		mEditWidget = edit;

		gridViewColors = (GridView) mRootView.findViewById(R.id.gridViewColors);

		gridViewColors.setOnItemClickListener(this);
		adapter = new AdapterColors(mContext, null);
		gridViewColors.setAdapter(adapter);

		new Thread(new TaskLoadPaintColors(this)).start();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		PaintInfoBean bean = (PaintInfoBean) parent.getAdapter().getItem(position);
		mEditWidget.changeColor(bean.getColor());
		dismiss();
	}

	@Override
	public void onLoaded(List<PaintInfoBean> colors) {
		adapter.refresh(colors);
	}

}
