package com.speaktool.impl.cmd.copy;

import com.speaktool.api.Draw;
import com.speaktool.api.Page;
import com.speaktool.bean.CopyPageData;
import com.speaktool.impl.cmd.CmdBase;
import com.speaktool.impl.cmd.ICmd;

/**
 * 界面清除-复制命令
 *
 * @author shaoshuai
 */
public class CmdCopyPage extends CmdBase<CopyPageData> {

    public CmdCopyPage() {
        super();
        setType(TYPE_COPY_PAGE);
    }

    @Override
    public void run(final Draw draw, Page board) {
        draw.postTaskToUiThread(new Runnable() {
            @Override
            public void run() {
                draw.copyPageImpl(
                        getData().getSrcPageId(),
                        getData().getDestPageId(),
                        getData().getOption()
                );
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
