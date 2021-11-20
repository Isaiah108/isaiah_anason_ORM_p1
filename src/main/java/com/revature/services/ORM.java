package com.revature.services;

import com.revature.annotations.PrimaryKey;
import com.revature.persistence.DAO;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.revature.services.ORM_Helper;

public class ORM {
    public static void makeTable(Class<?> clazz) {
        String tableName = clazz.getSimpleName();
        StringBuilder half1Query = new StringBuilder();
        StringBuilder half2Query = new StringBuilder();
        Field[] fields = clazz.getDeclaredFields();
        System.out.println(Arrays.toString(fields));

        half1Query.append("CREATE TABLE IF NOT EXISTS \"" + tableName + "\"(");

        //Checks for primary key and makes that first column
        for (Field field : fields) {
            if (Arrays.toString(field.getAnnotations()).contains("PrimaryKey")) {
                half2Query.append(field.getName() + " ");
                if (field.getDeclaredAnnotation(PrimaryKey.class).isSerial())
                    half2Query.append("serial");
                else
                    half2Query.append(ORM_Helper.convertType(field.getType()));
                half2Query.append(" primary key");
            }
        }
        //Check for other columns
        for (Field field : fields) {
            String fieldAnnotations = Arrays.toString(field.getAnnotations());

            //If the field is one we want in the database.. add it
            if (!fieldAnnotations.contains("PrimaryKey") && (fieldAnnotations.contains("Column") || fieldAnnotations.contains("NotNull") || fieldAnnotations.contains("Unique"))) {
                if (half2Query.length() != 0)
                    half2Query.append(", ");
                half2Query.append("\"").append(field.getName()).append("\" ").append(ORM_Helper.convertType(field.getType())).append(" ");
                if (fieldAnnotations.contains("Unique"))
                    half2Query.append("Unique ");
                if (fieldAnnotations.contains("NotNull"))
                    half2Query.append("not null ");
            }
        }

        //finish building query String and execute it
        half2Query.append(");");
        DAO.executeCreateTable(half1Query.toString() + half2Query.toString());

    }

    //CREATE
    public static boolean addRecord(Object obj) {
        if (!isObjectValidInsert(obj)) {
            return false;
        }
        StringBuilder half1Query = new StringBuilder();
        StringBuilder half2Query = new StringBuilder();
        StringBuilder values = new StringBuilder();
        values.append("values(");
        half1Query.append("insert into \"").append(obj.getClass().getSimpleName()).append("\"(");
        //loop over fields
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            String fieldAnnotations = Arrays.toString(field.getDeclaredAnnotations());

            //If annotation on field is one we want for columns &NOT a primary field
            if (!fieldAnnotations.contains("PrimaryKey") && (fieldAnnotations.contains("Column") || fieldAnnotations.contains("NotNull") || fieldAnnotations.contains("Unique"))) {
                if (half2Query.length() != 0) {
                    half2Query.append(",");
                    values.append(",");
                }
                half2Query.append("\"").append(field.getName()).append("\"");
                field.setAccessible(true);
                try {
                    values.append("\"").append(field.get(obj)).append("\"");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            //If annotation on field is primary key and not serial
            if (fieldAnnotations.contains("PrimaryKey") && !(field.getDeclaredAnnotation(PrimaryKey.class).isSerial())) {
                if (half2Query.length() != 0) {
                    half2Query.append(",");
                    values.append(",");
                }
                half2Query.append("\"").append(field.getName()).append("\"");
                field.setAccessible(true);
                try {
                    values.append("\"").append(field.get(obj)).append("\"");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        String finalQuery = half1Query.toString() + half2Query.toString() + ") " + values + ")";
        System.out.println("addRecord query: " + finalQuery);

        DAO.insert(finalQuery);
        return false;
    }

    //READ
    public static void readRecord(){

    }

    //UPDATE
    public static boolean updateRecord(Object obj){
        if (!isObjectValidInsert(obj)) {
            return false;
        }
        StringBuilder half1Query = new StringBuilder();
        StringBuilder half2Query = new StringBuilder();
        half1Query.append("Update ").append(obj.getClass().getSimpleName()).append(" Set ");







        return true;
    }

    //DELETE


    public static <T> void getAnnotation(Class<T> clazz) {
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            System.out.println("Annotation: " + Arrays.toString(field.getAnnotations()));
        }

    }

    public static boolean isObjectValidInsert(Object potentialNewObject) {

        //PRIMARY KEY
        List<Field> primaryKeyFields = Arrays.stream(potentialNewObject.getClass().getDeclaredFields())
                .filter(field -> Arrays.toString(field.getDeclaredAnnotations()).contains("PrimaryKey")).collect(Collectors.toList());
        boolean primaryKeyExists = false;
        StringBuilder query1 = new StringBuilder();
        if (primaryKeyFields.size() > 1)
            return false;
        else if (primaryKeyFields.size() != 0) {
            primaryKeyExists = true;
            try {
                primaryKeyFields.get(0).setAccessible(true);
                query1.append("Select \"").append(primaryKeyFields.get(0).getName()).append("\" from \"").append(potentialNewObject.getClass().getSimpleName())
                        .append("\" where \"").append(primaryKeyFields.get(0).getName()).append("\"=")
                        .append(primaryKeyFields.get(0).get(potentialNewObject));
            } catch (IllegalAccessException e) {
                System.out.println("Variable(Primary) can't be accessed");
                e.printStackTrace();
            }
        }

        //UNIQUE KEYS
        boolean uniqueFieldsExist = false;
        List<Field> uniqueKeyFields = Arrays.stream(potentialNewObject.getClass().getDeclaredFields())
                .filter(field -> Arrays.toString(field.getDeclaredAnnotations()).contains("Unique")).collect(Collectors.toList());
        StringBuilder[] query2 = new StringBuilder[uniqueKeyFields.size()];
        for (int i = 0; i < uniqueKeyFields.size(); i++) {
            query2[i] = new StringBuilder();
        }
        if (uniqueKeyFields.size() >= 1) {
            uniqueFieldsExist = true;
            try {
                for (int i = 0; i < uniqueKeyFields.size(); i++) {
                    uniqueKeyFields.get(i).setAccessible(true);
//                    ORM_Helper.getFieldsFromAnnotation(potentialNewObject.getClass(), "Unique")[i].setAccessible(true);
                    query2[i].append("Select \"").append(uniqueKeyFields.get(i).getName()).append("\" from \"").append(potentialNewObject.getClass().getSimpleName())
                            .append("\" where \"").append(uniqueKeyFields.get(i).getName()).append("\" ='")
                            .append(uniqueKeyFields.get(i).get(potentialNewObject)).append("'");
                }
            } catch (IllegalAccessException e) {
                System.out.println("Variable(Unique) can't be accessed");
                e.printStackTrace();
            }
        }

        //NOT NULL KEYS
        List<Field> notNullKeyFields = Arrays.stream(potentialNewObject.getClass().getDeclaredFields())
                .filter(field -> Arrays.toString(field.getDeclaredAnnotations()).contains("NotNull")).collect(Collectors.toList());
        if (notNullKeyFields.size() > 1) {
            try {
                for (Field notNullKeyField : notNullKeyFields) {
                    notNullKeyField.setAccessible(true);
                    if (notNullKeyField.get(potentialNewObject) == null) {
                        return false;
                    }
                }
            } catch (IllegalAccessException e) {
                System.out.println("Variable(NotNull) can't be accessed");
                e.printStackTrace();
            }
        }
        return DAO.checkValid(primaryKeyExists,uniqueFieldsExist,query1,query2);
    }
}
