package com.speaktool.impl.cmd.delete;

import com.speaktool.impl.api.BaseDraw;
import com.speaktool.api.Page;
import com.speaktool.impl.bean.ActivePageData;
import com.speaktool.impl.cmd.CmdBase;
import com.speaktool.impl.cmd.ICmd;

/**
 * 删除界面
 *
 * @author shaoshuai
 */
public class CmdDeletePage extends CmdBase<ActivePageData> {
    public CmdDeletePage() {
        super();
        setType(TYPE_DELETE_PAGE);
    }

    @Override
    public void run(final BaseDraw draw, Page bw) {
        draw.postTaskToUiThread(new Runnable() {
            @Override
            public void run() {
                draw.deletePageImpl(getData().getPageID());
            }
        });
    }

    @Override
    public ICmd inverse() {
        return null;
    }

    @Override
    public ICmd copy() {
        return this;
    }

}
