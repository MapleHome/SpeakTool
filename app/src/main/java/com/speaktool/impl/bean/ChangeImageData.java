package com.speaktool.impl.bean;

import java.util.List;

public class ChangeImageData<SEQ> extends ImageCommonData {
	private List<SEQ> sequence;

	public List<SEQ> getSequence() {
		return sequence;
	}

	public void setSequence(List<SEQ> sequence) {
		this.sequence = sequence;
	}

}
