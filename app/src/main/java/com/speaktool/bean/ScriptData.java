package com.speaktool.bean;

import com.speaktool.impl.cmd.ICmd;

import java.util.List;

public class ScriptData {
    private int inputRate;
    private int version;
    private List<ICmd> wbEvents;
    //
    private int inputScreenWidth;
    private int inputScreenHeight;
    private int density;

    private String wbEventsString;
    private List<Html5ImageInfoBean> resources;
    private Html5SoundInfoBean sound;


    public int getInputRate() {
        return inputRate;
    }

    public void setInputRate(int inputRate) {
        this.inputRate = inputRate;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<ICmd> getWbEvents() {
        return wbEvents;
    }

    public void setWbEvents(List<ICmd> wbEvents) {
        this.wbEvents = wbEvents;
    }

    public int getInputScreenWidth() {
        return inputScreenWidth;
    }

    public void setInputScreenWidth(int inputScreenWidth) {
        this.inputScreenWidth = inputScreenWidth;
    }

    public int getInputScreenHeight() {
        return inputScreenHeight;
    }

    public void setInputScreenHeight(int inputScreenHeight) {
        this.inputScreenHeight = inputScreenHeight;
    }

    public int getDensity() {
        return density;
    }

    public void setDensity(int density) {
        this.density = density;
    }

    public List<Html5ImageInfoBean> getPictureNames() {
        return resources;
    }

    public void setPictureNames(List<Html5ImageInfoBean> pictureNames) {
        this.resources = pictureNames;
    }

    public List<Html5ImageInfoBean> getResources() {
        return resources;
    }

    public void setResources(List<Html5ImageInfoBean> resources) {
        this.resources = resources;
    }

    public Html5SoundInfoBean getSound() {
        return sound;
    }

    public void setSound(Html5SoundInfoBean sound) {
        this.sound = sound;
    }

    public String getWbEventsString() {
        return wbEventsString;
    }

    public void setWbEventsString(String wbEventsString) {
        this.wbEventsString = wbEventsString;
    }

}
