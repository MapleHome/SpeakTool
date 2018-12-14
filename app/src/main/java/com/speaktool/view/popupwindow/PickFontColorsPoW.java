package com.speaktool.view.popupwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.speaktool.R;
import com.speaktool.bean.PaintInfoBean;
import com.speaktool.impl.shapes.EditWidget;
import com.speaktool.tasks.TaskLoadPaintColors;
import com.speaktool.tasks.TaskLoadPaintColors.Callback;
import com.speaktool.ui.adapters.AdapterColors;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 选择字体颜色
 *
 * @author shaoshuai
 */
public class PickFontColorsPoW extends BasePopupWindow implements OnItemClickListener {
    @BindView(R.id.gridViewColors) GridView gridViewColors;

    private AdapterColors adapter;
    private EditWidget mEditWidget;

    @Override
    public View getContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.pow_pickfontcolor, null);
        ButterKnife.bind(this, view);
        return view;
    }

    public PickFontColorsPoW(Context context, View token, View anchor, EditWidget edit) {
        super(context, token, anchor);
        mEditWidget = edit;

        gridViewColors.setOnItemClickListener(this);
        adapter = new AdapterColors(mContext, null);
        gridViewColors.setAdapter(adapter);

        new Thread(new TaskLoadPaintColors(new Callback() {
            @Override
            public void onLoaded(List<PaintInfoBean> colors) {
                adapter.refresh(colors);
            }
        })).start();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PaintInfoBean bean = (PaintInfoBean) parent.getAdapter().getItem(position);
        mEditWidget.changeColor(bean.getColor());
        dismiss();
    }


}
