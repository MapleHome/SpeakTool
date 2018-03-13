package com.speaktool.impl.cmd;


/**
 * 基础操作命令
 * 
 * @author shaoshuai
 * 
 * @param <DATATYPE>
 *            具体指令（作用页面+执行动作）
 */
public abstract class CmdBase<DATATYPE> implements ICmd<DATATYPE> {
	private String type;
	private long time;
	private DATATYPE data;

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setTime(long millsecs) {
		this.time = millsecs;
	}

	@Override
	public long getTime() {
		return time;
	}

	@Override
	public void setData(DATATYPE data) {
		this.data = data;
	}

	@Override
	public DATATYPE getData() {
		return data;
	}

}
