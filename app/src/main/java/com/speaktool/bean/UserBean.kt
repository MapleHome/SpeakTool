package com.speaktool.bean

import java.io.Serializable

/**
 *
 * @author maple
 * @time 2019/1/3
 */
class UserBean : Serializable {
    var id: String = "id"//用户ID
    var activity: Boolean = false

    var account: String = ""//帐号
    var password: String = ""//密码
    var nickName: String = "小可爱" //昵称
    var email: String = "939078792@qq.com"//邮箱
    var introduce: String = "qiong"//自我介绍
    var portraitPath: String = ""// 头像地址

    var sex: String = "未知"// 性别
    var birthdate: String = "1990-09-01"// 出生日期
}
