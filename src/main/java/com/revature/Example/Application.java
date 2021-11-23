package com.revature.Example;

import com.revature.annotations.PrimaryKey;
import com.revature.persistence.DAO;
import com.revature.services.ORM;
import com.revature.services.ORM_Helper;

import java.lang.reflect.Field;
import java.sql.SQLOutput;

public class Application {
    public static void main(String[] args) throws NoSuchFieldException, InterruptedException {
        //TODO Have bug where Wrapper classes get converted to SQL integers and therefor cant handle null values
        User user = new User();
        user.setUsername("Isaiah_108");
        user.setPassword("1234");

        User user2 = new User();
        user2.setUsername("Jane");
        user2.setPassword("1234");

        ORM.addRecord(user);
        ORM.addRecord(user2);

        ORM.readAll(User.class);

        ORM.dropTable(User.class);

    }
}
