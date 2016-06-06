package com.mckuai.daogen;


import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

/**
 * Created by kyly on 2016/6/6.
 */
public class DaoGen {
//    static Entity role;
    static Entity user;
    static Entity dub;

//    static Property roleId;
    static Property userId;
    static Property dubId;

    public static void main(String args[]) throws Exception{

        Schema schema = new Schema(1,"com.aries.storyreader.bean");
        schema.setDefaultJavaPackageDao("com.aries.storyreader.dao");
//        addRoleEntity(schema);
        addUserEntity(schema);
        addDubEntity(schema);
        addNodeEntity(schema);
        String outDir = "F:\\Repository\\StoryReader\\app\\src\\main\\java-gen";
        new DaoGenerator().generateAll(schema,outDir);
    }

/*    private static void addRoleEntity(Schema schema){
        role = schema.addEntity("Role");
        roleId = role.addIdProperty().getProperty();
        role.addStringProperty("name").notNull();
        role.addStringProperty("portrait").notNull();
    }*/

    private static void addUserEntity(Schema schema){
        user = schema.addEntity("User");
        userId = user.addIdProperty().columnName("userId").getProperty();
        user.addStringProperty("name").notNull();
        user.addStringProperty("portrait").notNull();
    }

    private static void addDubEntity(Schema schema){
        dub = schema.addEntity("Dub");
        dubId = dub.addIdProperty().columnName("dubId").autoincrement().getProperty();
        dub.addIntProperty("time");
        dub.addStringProperty("file");
        dub.addToOne(user,userId,"owner");

    }

    private static void addNodeEntity(Schema schema){
        Entity entity = schema.addEntity("Node");
        entity.addIdProperty().columnName("nodeId").autoincrement();
        entity.addStringProperty("content");
        entity.addToOne(user,userId,"role");
        entity.addToOne(dub,dubId,"dub");
    }
}
