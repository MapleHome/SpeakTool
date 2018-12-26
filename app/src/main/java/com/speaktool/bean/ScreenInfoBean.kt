package com.speaktool.bean

import java.io.Serializable

/**
 * 屏幕信息
 *
 * @author shaoshuai
 */
class ScreenInfoBean(
        var width: Int,
        var height: Int,
        var density: Int
) : Serializable {

    override fun toString(): String {
        return "ScreenInfoBean{" +
                "width=" + width + ", height=" + height +
                ", density=" + density +
                '}'.toString()
    }
}
