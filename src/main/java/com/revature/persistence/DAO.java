package com.revature.persistence;

import com.revature.annotations.PrimaryKey;
import com.revature.services.ConnectionService;
import com.revature.services.ORM_Helper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class DAO {

    public static void executeCreateTable(String queryString){
        System.out.println("MakeTableQuery: " + queryString);
        try (Connection conn = ConnectionService.getInstance()) {
            PreparedStatement statement = conn.prepareStatement(queryString);
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    /** Called only if not using serial database option. User defines the PrimaryKey and as such needs to be checked
     *
     * @param potentialNewObject must be object with PrimaryKey annotation
     * @return boolean of whether potentialNewObject primarykey isUnique, otherwise if it exists in database returns false
     */
    public static boolean isPrimaryKeyUnique(Object potentialNewObject) {
        String primaryKeyField = Arrays.stream(potentialNewObject.getClass().getDeclaredFields())
                .filter(field->Arrays.toString(field.getDeclaredAnnotations()).contains("PrimaryKey"))
                .findAny().orElse(null).getName();
        StringBuilder query = new StringBuilder();
        try {
            ORM_Helper.getFieldsFromAnnotation(potentialNewObject.getClass(),"PrimaryKey")[0].setAccessible(true);
            query.append("Select \"").append(primaryKeyField).append("\" from \"").append(potentialNewObject.getClass().getSimpleName())
                    .append("\" where \"").append(primaryKeyField).append("\"=")
                    .append(ORM_Helper.getFieldsFromAnnotation(potentialNewObject.getClass(), "PrimaryKey")[0].get(potentialNewObject));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        System.out.println(query);

        try (Connection conn = ConnectionService.getInstance()) {
            PreparedStatement statement = conn.prepareStatement(query.toString());
            ResultSet rs = statement.executeQuery();
            if(rs.next())
                return false; // false = not unique
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
