package com.revature.services;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
                outputType = "double";
                break;
            case "java.lang.String":
                outputType = "text";
                break;
        }
        return outputType;
    }
    public static Field[] getFieldsFromAnnotation(Class<?> clazz, String annotation){
        List<Field> fieldList = new ArrayList<>();
        for(Field field:clazz.getDeclaredFields()){
            if(Arrays.toString(field.getDeclaredAnnotations()).contains(annotation)){
                fieldList.add(field);
            }
        }
        return fieldList.toArray(new Field[0]);
    }
}
