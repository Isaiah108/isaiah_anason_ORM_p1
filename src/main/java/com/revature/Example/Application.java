package com.revature.Example;

import com.revature.annotations.Column;
import com.revature.annotations.NotNull;
import com.revature.annotations.PrimaryKey;
import com.revature.persistence.DAO;
import com.revature.services.ORM;
import com.revature.services.ORM_Helper;

import java.lang.reflect.Field;
import java.sql.SQLOutput;

public class Application {
    public static void main(String[] args) throws NoSuchFieldException, InterruptedException {
        //TODO Have bug where Wrapper classes get converted to SQL integers and therefore cant handle null values
//        public class User {
//
//            @PrimaryKey(isSerial = false)
//            private String username;
//            @NotNull
//            private String password;
//            @Column
//            private String firstName;
//            @Column
//            private int age;
//            @Unique
//            private int id;
    }
}
