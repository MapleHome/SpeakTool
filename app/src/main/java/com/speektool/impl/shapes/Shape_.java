package com.speektool.impl.shapes;

import com.speektool.api.Page;

public interface Shape_ {

	int getShapeID();

	void drawToPage(Page page);

	void deleteFromPage(Page page);
}
