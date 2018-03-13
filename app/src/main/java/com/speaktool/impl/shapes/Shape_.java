package com.speaktool.impl.shapes;

import com.speaktool.api.Page;

public interface Shape_ {

	int getShapeID();

	void drawToPage(Page page);

	void deleteFromPage(Page page);
}
