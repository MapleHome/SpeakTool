package com.speaktool.ui.Draw;

/**
 * @author maple
 * @time 2018/12/10
 */
public class RecordBean {
//    public String recordTitle;
//    public String recordDir;
//    public long createTime;
//    public long duration;
//
//    public String tab;
//    public String type;
//    public String introduce;
//
//    public String url;
//    public String courseId;
    //
    public int pageWidth;
    public int pageHeight;

    private static final RecordBean ourInstance = new RecordBean();

    public static RecordBean getInstance() {
        return ourInstance;
    }

    private RecordBean() {
    }


}
