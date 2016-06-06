package com.aries.storyreader.bean;

import java.io.Serializable;

/**
 * Created by kyly on 2016/5/26.
 *
 */
public class ChapterItem implements Serializable {
    private int index;
    private String describe;

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
