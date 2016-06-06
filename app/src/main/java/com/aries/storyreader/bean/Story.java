package com.aries.storyreader.bean;

import java.io.Serializable;

/**
 * Created by kyly on 2016/6/3.
 */
public class Story implements Serializable {
    private int id;
    private String name;
    private String image;
    private User writer;

    public Story(int id, String name, String image, User writer) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.writer = writer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getWriter() {
        return writer;
    }

    public void setWriter(User writer) {
        this.writer = writer;
    }
}
