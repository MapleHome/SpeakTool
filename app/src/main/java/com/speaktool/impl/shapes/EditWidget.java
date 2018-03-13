package com.speaktool.impl.shapes;


/**
 * 编辑小工具
 * 
 * @author shaoshuai
 * 
 */
public interface EditWidget extends ViewShape_ {

	void delete();

	void scaleBig();

	void scaleSmall();

	void changeColor(int newColor);

	void switchLock();

	void copy();

	//

	boolean isInEdit();

	void intoEdit(boolean isCreate);

	void outEdit();
	//

}
