package com.revature.services;

import com.revature.persistence.DAO;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ORM_Helper {
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
                outputType = "double precision";
                break;
            case "java.lang.String":
                outputType = "text";
                break;
        }
        return outputType;
    }

    public static Field[] getFieldsFromAnnotation(Class<?> clazz, String annotation) {
        List<Field> fieldList = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (Arrays.toString(field.getDeclaredAnnotations()).contains(annotation)) {
                fieldList.add(field);
            }
        }
        return fieldList.toArray(new Field[0]);
    }

    public static boolean isClassValid(Class<?> clazz) {
        List<Field> primaryKeyFields = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> Arrays.toString(field.getDeclaredAnnotations()).contains("PrimaryKey")).collect(Collectors.toList());
        return primaryKeyFields.size() == 1;
    }

    /**
     * @param obj object to check if valid in that its NotNull annotations are in fact not null
     * @return whether its valid or not
     */
    public static boolean isObjectValid(Object obj) {
        if (!isClassValid(obj.getClass())) {
            return false;
        }
        //NOT NULL KEYS
        List<Field> notNullKeyFields = Arrays.stream(obj.getClass().getDeclaredFields())
                .filter(field -> Arrays.toString(field.getDeclaredAnnotations()).contains("NotNull")).collect(Collectors.toList());
        if (notNullKeyFields.size() > 0) {
            try {
                for (Field notNullKeyField : notNullKeyFields) {
                    notNullKeyField.setAccessible(true);
                    if (notNullKeyField.get(obj) == null) {
                        return false;
                    }
                }
            } catch (IllegalAccessException e) {
                System.out.println("Variable(NotNull) can't be accessed");
                e.printStackTrace();
            }
        }
        return true;
    }

    public static boolean isObjectValidInsert(Object potentialNewObject) {
        if (!isObjectValid(potentialNewObject)) {
            return false;
        }
        //PRIMARY KEY
        List<Field> primaryKeyFields = Arrays.stream(potentialNewObject.getClass().getDeclaredFields())
                .filter(field -> Arrays.toString(field.getDeclaredAnnotations()).contains("PrimaryKey")).collect(Collectors.toList());
        StringBuilder query1 = new StringBuilder();
        try {
            primaryKeyFields.get(0).setAccessible(true);
            query1.append("Select \"").append(primaryKeyFields.get(0).getName()).append("\" from \"").append(potentialNewObject.getClass().getSimpleName())
                    .append("\" where \"").append(primaryKeyFields.get(0).getName()).append("\"=").append("'").append(primaryKeyFields.get(0).get(potentialNewObject)).append("'");
        } catch (IllegalAccessException e) {
            System.out.println("Variable(Primary) can't be accessed");
            e.printStackTrace();
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
        return DAO.checkValidToInsert(uniqueFieldsExist, query1, query2);
    }

    public static boolean isObjectValidUpdate(Object obj) {
        if (!isObjectValid(obj))
            return false;
        List<Field> primaryKeyFields = Arrays.stream(obj.getClass().getDeclaredFields())
                .filter(field -> Arrays.toString(field.getDeclaredAnnotations()).contains("PrimaryKey")).collect(Collectors.toList());
        StringBuilder query1 = new StringBuilder();
        Object objID = null;

        try {
            primaryKeyFields.get(0).setAccessible(true);
            query1.append("Select \"").append(primaryKeyFields.get(0).getName()).append("\" from \"").append(obj.getClass().getSimpleName())
                    .append("\" where \"").append(primaryKeyFields.get(0).getName()).append("\"=")
                    .append(primaryKeyFields.get(0).get(obj));
            objID = primaryKeyFields.get(0).get(obj);
        } catch (IllegalAccessException e) {
            System.out.println("Variable(Primary) can't be accessed");
            e.printStackTrace();
        }
        return DAO.checkIDExists(objID, primaryKeyFields.get(0)) && DAO.checkUniqueFieldsAreUnique(obj);
    }

    public static Object getInstance(Class<?> clazz){
        Constructor<?> noArgsConstructor = null;

        // constructor with a parameter of 0

        noArgsConstructor = Arrays.stream(clazz.getDeclaredConstructors())
                .filter( c -> c.getParameterCount() == 0)
                .findFirst().orElse(null);

        if(noArgsConstructor != null){
            try {
                return noArgsConstructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Object convertStringToType(Field field, String s) {
        String fieldString = field.getType().getSimpleName();
        switch(fieldString){
            case "boolean": return Boolean.parseBoolean(s);
            case "char" : return s.charAt(0);
            case "byte": return Byte.parseByte(s);
            case "short":return Short.parseShort(s);
            case "int": return Integer.parseInt(s);
            case "long": return Long.parseLong(s);
            case "float": return Float.parseFloat(s);
            case "double": return Double.parseDouble(s);
            case "String": return s;
        }
        return null;
    }
}
