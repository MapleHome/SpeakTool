package com.speaktool.impl.bean

import com.speaktool.impl.api.Page.Page_BG

/**
 * 画板纸张属性
 *
 * @author maple
 * @time 2018/12/26
 */
class CreatePageData(
        var pageID: Int, // 纸张ID
        var position: Int, // 纸张索引
        var backgroundType: Page_BG? // 纸张背景
)
