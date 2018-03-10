package com.speektool.utils;

import java.util.regex.Pattern;

import android.text.TextUtils;

/**
 * 用户信息有效检查
 * 
 * @author shaoshuai
 * 
 */
public class UserInfoValidateUtil {
	
	/** 检查账户 */
	public static boolean checkAccount(String input) {
		if (TextUtils.isEmpty(input))
			return false;
		return Pattern.matches("^[a-zA-Z0-9_]{6,20}$", input);
	}

	/** 检查密码【只接受6至20位单字节字符】 */
	public static boolean checkPassword(String input) {
		if (TextUtils.isEmpty(input))
			return false;
		int len = input.length();
		if (len < 6 || len > 20)
			return false;
		for (int i = 0; i < len; i++) {
			String s = input.charAt(i) + "";
			if (s.getBytes().length > 1) {
				return false;
			}
		}
		return true;
	}

	/** 检查邮箱 */
	public static boolean checkEmail(String input) {
		if (TextUtils.isEmpty(input))
			return false;
		return Pattern.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", input);
	}

}
