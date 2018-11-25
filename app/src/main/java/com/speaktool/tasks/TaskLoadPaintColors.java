package com.speaktool.tasks;

import com.speaktool.R;
import com.speaktool.bean.PaintInfoBean;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class TaskLoadPaintColors extends BaseRunnable<Integer, Void> {
    private static List<PaintInfoBean> datas;
    private WeakReference<Callback> mListener;

    public interface Callback {
        void onLoaded(List<PaintInfoBean> colors);
    }

    public TaskLoadPaintColors(Callback listener) {
        super();
        this.mListener = new WeakReference<Callback>(listener);
    }

    @Override
    public Void doBackground() {
        synchronized (this) {
            if (datas == null) {
                datas = new ArrayList<>();
            }
            datas.add(new PaintInfoBean(MyColors.BLACK, R.drawable.black, R.drawable.black_seleted));
            datas.add(new PaintInfoBean(MyColors.BLUE, R.drawable.blue, R.drawable.blue_seleted));
            datas.add(new PaintInfoBean(MyColors.RED, R.drawable.red, R.drawable.red_seleted));
            datas.add(new PaintInfoBean(MyColors.GREEN, R.drawable.green, R.drawable.green_seleted));

            datas.add(new PaintInfoBean(MyColors.YELLOW, R.drawable.yellow, R.drawable.yellow_seleted));
            datas.add(new PaintInfoBean(MyColors.BROWN, R.drawable.brown, R.drawable.brown_seleted));
            datas.add(new PaintInfoBean(MyColors.GRAY, R.drawable.gray, R.drawable.gray_seleted));
            datas.add(new PaintInfoBean(MyColors.DARK_BLUE, R.drawable.darkblue, R.drawable.darkblue_seleted));
        }
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
