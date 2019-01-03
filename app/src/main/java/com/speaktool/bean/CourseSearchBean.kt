package com.speaktool.bean

/**
 * 课程搜索
 *
 * @author shaoshuai
 */
class CourseSearchBean {

    var uid: String? = null
    var pageSize: Int = 0
    var keywords: String? = null
    var pageNumber: Int = 0
    var courseType = SEARCH_COURSE_TYPE_ALL
    //
    var category: SearchCategoryBean? = null

    companion object {
        val SEARCH_COURSE_TYPE_VIDEO = 1
        val SEARCH_COURSE_TYPE_ALL = 9
    }

}
