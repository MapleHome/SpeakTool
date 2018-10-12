package com.speaktool.impl.cmd.transform;

import com.speaktool.api.Draw;
import com.speaktool.api.Page;
import com.speaktool.bean.PageBackgroundData;
import com.speaktool.impl.cmd.CmdBase;
import com.speaktool.impl.cmd.ICmd;

/**
 * 改变背景
 *
 * @author Maple Shao
 */
public class CmdChangePageBackground extends CmdBase<PageBackgroundData> {

    public CmdChangePageBackground() {
        super();
        setType(TYPE_CHANGE_PAGE_BACKGROUND);
    }

    @Override
    public void run(final Draw draw, Page board) {
        draw.postTaskToUiThread(new Runnable() {

            @Override
            public void run() {
                draw.setPageBackgroundImpl(
                        getData().getPageID(),
                        getData().getBackgroundType()
                );
            }
        });

    }

    @Override
    public ICmd inverse() {
        return null;
    }

    @Override
    public ICmd copy() {
        return null;
    }

}
