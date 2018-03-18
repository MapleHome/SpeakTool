package com.speaktool.tasks;

import com.google.common.collect.Lists;
import com.speaktool.R;
import com.speaktool.bean.PaintInfoBean;
import com.speaktool.utils.MyColors;

import java.lang.ref.WeakReference;
import java.util.List;

public class TaskLoadPaintColors extends BaseRunnable<Integer, Void> {

    public interface Callback {
        void onLoaded(List<PaintInfoBean> colors);
    }

    private static List<PaintInfoBean> datas;
    private static final Object lock = new Object();

    private final static int[][] colors = {
            {MyColors.BLACK, R.drawable.black, R.drawable.black_seleted},
            {MyColors.BLUE, R.drawable.blue, R.drawable.blue_seleted},
            {MyColors.RED, R.drawable.red, R.drawable.red_seleted},
            {MyColors.GREEN, R.drawable.green, R.drawable.green_seleted},
            {MyColors.YELLOW, R.drawable.yellow, R.drawable.yellow_seleted},
            {MyColors.BROWN, R.drawable.brown, R.drawable.brown_seleted},
            {MyColors.GRAY, R.drawable.gray, R.drawable.gray_seleted},
            {MyColors.DARK_BLUE, R.drawable.darkblue, R.drawable.darkblue_seleted}
    };

    private final WeakReference<Callback> mListener;

    public TaskLoadPaintColors(Callback listener) {
        super();
        this.mListener = new WeakReference<Callback>(listener);
    }

    @Override
    public Void doBackground() {
        synchronized (lock) {
            if (datas == null) {
                datas = Lists.newArrayList();
                for (int[] one : colors) {
                    PaintInfoBean item = new PaintInfoBean();
                    item.setColor(one[0]);
                    item.setIconResId(one[1]);
                    item.setIconResIdSelected(one[2]);
                    datas.add(item);

                }
            }
        }// synchronized
        //
        uiHandler.post(new Runnable() {

            @Override
            public void run() {
                Callback listener = mListener.get();
                if (null != listener) {
                    listener.onLoaded(datas);
                }

            }
        });

        return null;
    }

}
