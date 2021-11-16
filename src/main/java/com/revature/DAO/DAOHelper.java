package com.revature.DAO;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Arrays;

public class DAOHelper {
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

    public static Object getInstance(Class<?> clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?> noArgsConstructor = null;

        // constructor with a parameter of 0

        noArgsConstructor = Arrays.stream(clazz.getDeclaredConstructors())
                .filter(c -> c.getParameterCount() == 0)
                .findFirst().orElse(null);

        if (noArgsConstructor != null) {
            return noArgsConstructor.newInstance();
        }
        return null;
    }
}
