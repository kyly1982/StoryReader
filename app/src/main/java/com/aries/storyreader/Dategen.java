package com.aries.storyreader;

import android.content.Context;

import com.aries.storyreader.bean.Node;
import com.aries.storyreader.bean.User;
import com.aries.storyreader.dao.DaoSession;

import java.util.ArrayList;

/**
 * Created by kyly on 2016/6/6.
 */
public class Dategen {
    private static ArrayList<User> roles;
    private static ArrayList<User> users;
    private static ArrayList<Node> nodeItems;
    private static Context mContext;
    private static DaoSession session;

    public Dategen(Context context) {
        mContext = context;
        session = MyApplication.instence.session;
    }

    public static ArrayList<Node> getNodeItems(Context context){
        mContext = context;
        if (null == nodeItems){
            loadNodes();
        }
        return nodeItems;
    }

    private static void loadRoles(){
        if (null == roles){
            roles = new ArrayList<>(6);
        }
        String[] roleNames = mContext.getResources().getStringArray(R.array.rolesname);
        String[] roleimage = mContext.getResources().getStringArray(R.array.rolesimage);
        for (int i = 0;i < roleimage.length;i++){
            User item = new User((long)(i+1),roleNames[i],roleimage[i]);
            roles.add(item);
        }
    }

    private static User getRolebyId(int id){
        for (User item:roles){
            if (item.getId() == id){
                return item;
            }
        }
        return null;
    }



    private static void loadNodes(){
        if (null == nodeItems){
            nodeItems = new ArrayList<>(20);
        }
        if (null == roles){
            loadRoles();
        }
        String[] content = mContext.getResources().getStringArray(R.array.dialogs);
        int[] roleId = mContext.getResources().getIntArray(R.array.noderolesid);
        for (int i = 0;i<content.length -1;i++){
            Node item = new Node((long)i,content[i]);
            if (roleId[i] > 0){
                item.setRole(getRolebyId(roleId[i]));
            }
            nodeItems.add(item);
        }
    }
}
