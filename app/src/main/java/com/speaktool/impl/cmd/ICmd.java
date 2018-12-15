package com.speaktool.impl.cmd;

import com.speaktool.api.BaseDraw;
import com.speaktool.api.Draw;
import com.speaktool.api.Page;

/**
 * 操作命令
 * 
 * @author shaoshuai
 * 
 * @param <DATATYPE>
 */
public interface ICmd<DATATYPE> {
	/** 创建页面 */
	public static final String TYPE_CREATE_PAGE = "createPage";// 创建画板纸张
	public static final String TYPE_CREATE_SHAPE = "createShape";

	/** 删除页面 */
	public static final String TYPE_DELETE_PAGE = "deletePage";
	public static final String TYPE_DELETE_SHAPE = "deleteShape";
	/** 复制页面 */
	public static final String TYPE_COPY_PAGE = "copyPage";

	/** 清空页面内容 */
	public static final String TYPE_CLEAR_PAGE = "clearPage";

	public static final String TYPE_CHANGE_TEXT = "changeText";
	public static final String TYPE_TRANSFORM_SHAPE = "transformShape";

	public static final String TYPE_SET_ACTIVE_PAGE = "setActivePage";

	public static final String TYPE_CHANGE_PAGE_BACKGROUND = "changePageBackground";

	public static final int TIME_DELETE_FLAG = 0;

	void setType(String type);

	String getType();

	void setTime(long millsecs);

	long getTime();

	void setData(DATATYPE data);

	DATATYPE getData();

	void run(BaseDraw draw, Page board);

	/** 反选 */
	@SuppressWarnings("rawtypes")
	ICmd inverse();

	/** 复制 */
	@SuppressWarnings("rawtypes")
	ICmd copy();
}
