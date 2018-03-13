package com.speaktool.impl.shapes;


/**
 * 图片小工具
 * 
 * @author shaoshuai
 * 
 */
public interface ImageWidget extends ViewShape_ {

	/** 删除 */
	void delete();

	/** 复制 */
	void copy();

	/** 旋转 */
	void rotate();

	/** 宽度适配 */
	void widthAutoFit();

	/** 高度适配 */
	void heightAutoFit();

	/** 锁定 */
	void switchLock();

	//
	void setResourceID(String resourceID);

	String getResourceID();
}
