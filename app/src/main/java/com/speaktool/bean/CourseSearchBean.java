package com.speaktool.bean;

/**
 * 课程搜索
 * 
 * @author shaoshuai
 * 
 */
public class CourseSearchBean {
	public static final int SEARCH_COURSE_TYPE_VIDEO = 1;
	public static final int SEARCH_COURSE_TYPE_ALL = 9;

	private String uid;
	private int pageSize;
	private String keywords;
	private int pageNumber;
	private int courseType = SEARCH_COURSE_TYPE_ALL;
	//
	private SearchCategoryBean category;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public int getCourseType() {
		return courseType;
	}

	public void setCourseType(int courseType) {
		this.courseType = courseType;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public SearchCategoryBean getCategory() {
		return category;
	}

	public void setCategory(SearchCategoryBean category) {
		this.category = category;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

}
