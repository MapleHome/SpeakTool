package com.speaktool.impl.bean

/**
 * 清除页面数据
 *
 * @author maple
 * @time 2018/12/26
 */
class ClearPageData(
        var pageId: Int, // 页面ID
        var option: String?// 清除类型
) {

    companion object {
        const val OPT_CLEAR_ALL = "all"// 清除所有
        const val OPT_CLEAR_NOTES = "notes"// 清除笔记
    }

}
