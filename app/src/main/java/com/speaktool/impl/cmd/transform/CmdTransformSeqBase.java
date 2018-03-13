package com.speaktool.impl.cmd.transform;

import com.speaktool.impl.cmd.CmdBase;

public abstract class CmdTransformSeqBase<DATATYPE> extends CmdBase<DATATYPE> {

	public CmdTransformSeqBase() {
		super();
		setType(TYPE_TRANSFORM_SHAPE);
	}

}
