package com.speaktool.ui.Player;

import com.speaktool.bean.MoveData;
import com.speaktool.bean.PositionData;

import java.util.List;

/**
 * @author maple
 * @time 2018/12/17
 */
public class ReleaseBean {
    public List<Event> wbEvents;
    public List<String> resources;
    public Sound sound;
    public int inputScreenWidth;
    public int inputScreenHeight;
    public int density;
    public int version;
    public int inputRate;

    public class Sound {
        public String path;
        public String type;
    }

    public class Event {
        public Data data;
        public long time;
        public long endTime;
        public String type;
    }

    public class Data {
        public int pageID;
        public int position;
        public String backgroundType;
        //
        public List<PositionData> maxXY;
        public List<PositionData> minXY;
        public List<MoveData> points;
        public int alpha;
        public int shapeID;
        public int strokeWidth;
        public String strokeColor;
        public String type;
    }

    public class CreateShapeData {
        public List<PositionData> maxXY;
        public List<PositionData> minXY;
        public List<MoveData> points;
        public int alpha;
        public int shapeID;
        public int strokeWidth;
        public String strokeColor;
        public String type;
    }

}
