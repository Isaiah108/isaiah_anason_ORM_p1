package com.revature.DAO;

import com.revature.Example.User;

import java.lang.reflect.Field;
import java.sql.SQLOutput;
import java.util.Arrays;

public class DAO {
    public static void main(String[] args) {
        getAnnotation(User.class);
    }
    public static void makeTable(Class<?> clazz){
        String tableName = clazz.getSimpleName();
        System.out.println("Table Name: " + tableName);
        String query = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
    }


    //CREATE



    //READ



    //UPDATE



    //DELETE





    public static <T> void getAnnotation(Class<T> clazz){
        Field[] fields = clazz.getFields();
        for(Field field:fields){
            System.out.println("Annotation: " + Arrays.toString(field.getAnnotations()));
        }

    }
}
