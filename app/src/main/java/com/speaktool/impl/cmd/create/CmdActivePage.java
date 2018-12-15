package com.speaktool.impl.cmd.create;

import com.speaktool.api.BaseDraw;
import com.speaktool.api.Page;
import com.speaktool.bean.ActivePageData;
import com.speaktool.impl.cmd.CmdBase;
import com.speaktool.impl.cmd.ICmd;

/**
 * 互动页面
 *
 * @author shaoshuai
 */
public class CmdActivePage extends CmdBase<ActivePageData> {

    public CmdActivePage(long time, ActivePageData data) {
        super(TYPE_SET_ACTIVE_PAGE, time, data);
    }

    @Override
    public void run(final BaseDraw draw, Page board) {
        final int pageId = getData().getPageID();
        draw.postTaskToUiThread(new Runnable() {
            @Override
            public void run() {
                draw.setActivePageImpl(pageId);
            }
        });
    }

    @Override
    public ICmd inverse() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ICmd copy() {
        return this;
    }

}
