package com.aries.storyreader.bean;

import com.aries.storyreader.dao.DaoSession;
import com.aries.storyreader.dao.DubDao;

import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "DUB".
 */
public class Dub {

    private Long id;
    private Integer time = 0;
    private String file;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient DubDao myDao;

    private User owner;
    private Long owner__resolvedKey;


    public Dub() {
    }

    public Dub(Long id) {
        this.id = id;
    }

    public Dub(Long id, Integer time, String file) {
        this.id = id;
        this.time = time;
        this.file = file;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDubDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    /** To-one relationship, resolved on first access. */
    public User getOwner() {
        /*if (null == owner){
            Long __key = this.id;
            if (owner__resolvedKey == null || !owner__resolvedKey.equals(__key)) {
                if (daoSession == null) {
                    throw new DaoException("Entity is detached from DAO context");
                }
                UserDao targetDao = daoSession.getUserDao();
                User ownerNew = targetDao.load(__key);
                synchronized (this) {
                    owner = ownerNew;
                    owner__resolvedKey = __key;
                }
            }
        }*/
        return owner;
    }

    public void setOwner(User owner) {
        synchronized (this) {
            this.owner = owner;
            id = owner == null ? null : owner.getId();
            owner__resolvedKey = id;
        }
    }

    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }


    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}
