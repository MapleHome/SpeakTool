package com.speaktool.impl.cmd.clear;

import com.speaktool.api.BaseDraw;
import com.speaktool.api.Page;
import com.speaktool.bean.ClearPageData;
import com.speaktool.impl.cmd.CmdBase;
import com.speaktool.impl.cmd.ICmd;

/**
 * 界面清除-操作命令
 *
 * @author shaoshuai
 */
public class CmdClearPage extends CmdBase<ClearPageData> {

    public CmdClearPage() {
        super();
        setType(TYPE_CLEAR_PAGE);
    }

    @Override
    public void run(final BaseDraw draw, Page board) {
        draw.postTaskToUiThread(new Runnable() {
            @Override
            public void run() {
                draw.clearPageImpl(getData().getPageId(), getData().getOption());
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
