package com.speaktool.impl.cmd.create;

import com.speaktool.impl.api.BaseDraw;
import com.speaktool.impl.api.Page;
import com.speaktool.impl.bean.CreatePageData;
import com.speaktool.impl.cmd.CmdBase;
import com.speaktool.impl.cmd.ICmd;

/**
 * 创建画纸界面
 *
 * @author shaoshuai
 */
public class CmdCreatePage extends CmdBase<CreatePageData> {

    public CmdCreatePage() {
        super();
        setType(TYPE_CREATE_PAGE);
    }

    @Override
    public void run(final BaseDraw draw, Page bw) {
        draw.postTaskToUiThread(new Runnable() {
            @Override
            public void run() {
                draw.createPageImpl(
                        getData().getBackgroundType(),
                        getData().getPosition(),
                        getData().getPageID()
                );
            }
        });

    }

    // 实现接口 - 反选
    @Override
    public ICmd inverse() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ICmd copy() {
        return this;
    }

}
