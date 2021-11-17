package com.revature.services;

import com.revature.annotations.PrimaryKey;
import com.revature.persistence.DAO;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;

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
                    half2Query.append(convertType(field.getType()));
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
                half2Query.append("\"").append(field.getName()).append("\" ").append(convertType(field.getType())).append(" ");
                if (fieldAnnotations.contains("Unique"))
                    half2Query.append("Unique ");
                if (fieldAnnotations.contains("NotNull"))
                    half2Query.append("not null ");
            }
        }

        //finish building query String and execute it
        half2Query.append(");");
        DAO.executeCreateTable(half1Query.toString()+half2Query.toString());

    }

    //CREATE
    public static void addRecord(Object obj) {
        String query = "insert into \"" + obj.getClass().getSimpleName() + "\" (";

        //loop over fields
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if(Arrays.toString(field.getDeclaredAnnotations()).contains("PrimaryKey")){

            }
        }
    }

    //READ


    //UPDATE


    //DELETE


    public static <T> void getAnnotation(Class<T> clazz) {
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            System.out.println("Annotation: " + Arrays.toString(field.getAnnotations()));
        }

    }
    public static String convertType(Type type) {
        String inputType = type.getTypeName();
        String outputType = null;

        switch (inputType) {
            case "char":
            case "java.lang.Character":
                outputType = "char";
                break;
            case "boolean":
            case "java.lang.Boolean":
                outputType = "bool";
                break;
            case "byte":
            case "java.lang.Byte":
            case "short":
            case "java.lang.Short":
            case "int":
            case "java.lang.Integer":
                outputType = "int";
                break;
            case "long":
            case "java.lang.Long":
                outputType = "bigint";
                break;
            case "float":
            case "java.lang.Float":
            case "double":
            case "java.lang.Double":
                outputType = "double";
                break;
            case "java.lang.String":
                outputType = "text";
                break;
        }
        return outputType;
    }
}
