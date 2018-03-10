package com.speektool.bean;

import com.j256.ormlite.field.DatabaseField;

public class UserBean {
	public static final int USER_TYPE_SPEAKTOOL = 0;// 用户类型-讲讲
	public static final int USER_TYPE_SINA = 1;// 用户类型-新浪微博
	public static final int USER_TYPE_TENCENT = 2;// 用户类型-腾讯微博
	public static final int USER_TYPE_QQ = 2;// 用户类型-QQ帐号
	public static final int USER_TYPE_PARTNER = 99;// 用户类型-讲讲
	/** 登录状态。 */
	public static final int STATE_IN = 1;
	public static final int STATE_OUT = 0;
	//
	/** 帐号 */
	private String account;
	/** 密码 */
	@DatabaseField
	private String password;
	/** 昵称 */
	@DatabaseField
	private String nickName;
	/** 邮箱 */
	@DatabaseField
	private String email;
	/** 自我介绍 */
	@DatabaseField
	private String introduce;
	/** 头像地址 */
	@DatabaseField
	private String portraitPath;

	/** 用户ID */
	@DatabaseField(id = true)
	private String id;
	/** 绝对型账户 */
	@DatabaseField
	private String widgetUserId;
	/** 用户类型 */
	@DatabaseField
	private int type;
	/** 登陆状态 */
	@DatabaseField
	private int loginState;
	/** 公司ID */
	@DatabaseField
	private String companyId;
	@DatabaseField
	private String token;

	public UserBean() {
		super();
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

	public String getPortraitPath() {
		return portraitPath;
	}

	public void setPortraitPath(String portraitPath) {
		this.portraitPath = portraitPath;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getWidgetUserId() {
		return widgetUserId;
	}

	public void setWidgetUserId(String widgetUserId) {
		this.widgetUserId = widgetUserId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getLoginState() {
		return loginState;
	}

	public void setLoginState(int loginState) {
		this.loginState = loginState;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
