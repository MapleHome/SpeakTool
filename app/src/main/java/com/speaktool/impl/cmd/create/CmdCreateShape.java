package com.speaktool.impl.cmd.create;

import com.speaktool.impl.cmd.CmdBase;

public abstract class CmdCreateShape<DATATYPE> extends CmdBase<DATATYPE> {

	public CmdCreateShape() {
		super();
		setType(TYPE_CREATE_SHAPE);
	}

}
