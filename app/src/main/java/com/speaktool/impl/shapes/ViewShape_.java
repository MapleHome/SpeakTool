package com.speaktool.impl.shapes;

import com.speaktool.api.Page;

import android.view.View;

public interface ViewShape_ extends Shape_ {

	View view();

	ViewShape_ copySelf();

	Page getPage();

	void setPosition(int x, int y, boolean isNeedConvertPivot);

	void refresh();

}
