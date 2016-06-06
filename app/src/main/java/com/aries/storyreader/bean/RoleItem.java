package com.aries.storyreader.bean;

/**
 * Created by kyly on 2016/5/26.
 * 角色信息
 */
public class RoleItem {
    private int id;
    private String name;
    private String portrait;

    public RoleItem(int id, String name, String portrait) {
        this.id = id;
        this.name = name;
        this.portrait = portrait;
    }

    public RoleItem(String name){
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }
}
