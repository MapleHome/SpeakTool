package com.speektool.impl.shapes;

import com.speektool.api.Page;

import android.view.View;

public interface ViewShape_ extends Shape_ {

	View view();

	ViewShape_ copySelf();

	Page getPage();

	void setPosition(int x, int y, boolean isNeedConvertPivot);

	void refresh();

}
