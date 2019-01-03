package com.speaktool.impl.bean

/**
 * 复制页面数据
 *
 * @author maple
 * @time 2018/12/26
 */
class CopyPageData(
        var srcPageId: Int, // 源页面ID
        var destPageId: Int, // 目标页面ID
        var option: String? // 操作类型
) {
    companion object {
        const val OPT_COPY_ALL = "all"
        const val OPT_COPY_VIEWS = "views"
    }
}
