package com.speaktool.ui.Draw;

import java.io.Serializable;

/**
 * @author maple
 * @time 2018/12/10
 */
public class RecordBean implements Serializable {
    public String courseId;
    public String title;
    public String dir;
    public long createTime;
    public long duration;

    public String tab;
    public String type;
    public String introduce;

    public String thumbNailName;
    public String thumbNailPath;

    public int pageWidth;
    public int pageHeight;

//    private static RecordBean ourInstance = new RecordBean();
//
//    public static RecordBean getInstance() {
//        return ourInstance;
//    }

    public RecordBean() {
    }

}
