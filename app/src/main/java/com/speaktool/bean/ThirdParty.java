package com.speaktool.bean;

import java.io.Serializable;

/**
 * 第三方
 * 
 * @author shaoshuai
 * 
 */
public class ThirdParty implements Serializable {
	private static final long serialVersionUID = -8116064201299369530L;
	public static final int ICON_TYPE_NET = 1;
	public static final int ICON_TYPE_RES = 2;
	// 第三方Id
	public static final int ID_QQ_WEIBO = 1;
	public static final int ID_SINA_WEIBO = 2;
	public static final int ID_QQ = 3;
	public static final int ID_WECHART = 4;
	public static final int ID_NET_COMPANY = 5;
	// 第三方动作
	/** 第三方登陆 */
	public static final String ACTION_LOGIN = "thirdPartyLogin";
	/** 获取第三方用户信息 */
	public static final String ACTION_GET_USERINFO = "getThirdPartyUserInfo";
	/** 上传课程 */
	public static final String ACTION_UPLOAD_COURSE = "uploadCourse";

	private String name;
	private int iconResId;
	private String iconUrl;
	private int id;
	private int iconType;
	//
	private String action;
	private String interfaceUrlPrefix;
	private String interfaceUrlSuffix;
	private String companyId;
	private String moduleId;
	//
	private int userType;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIconResId() {
		return iconResId;
	}

	public void setIconResId(int iconResId) {
		this.iconResId = iconResId;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIconType() {
		return iconType;
	}

	public void setIconType(int iconType) {
		this.iconType = iconType;
	}

	public static int getIconTypeNet() {
		return ICON_TYPE_NET;
	}

	public static int getIconTypeRes() {
		return ICON_TYPE_RES;
	}

	public String getInterfaceUrlPrefix() {
		return interfaceUrlPrefix;
	}

	public void setInterfaceUrlPrefix(String interfaceUrlPrefix) {
		this.interfaceUrlPrefix = interfaceUrlPrefix;
	}

	public String getInterfaceUrlSuffix() {

		return interfaceUrlSuffix;
	}

	public void setInterfaceUrlSuffix(String interfaceUrlSuffix) {

		this.interfaceUrlSuffix = interfaceUrlSuffix;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

}
