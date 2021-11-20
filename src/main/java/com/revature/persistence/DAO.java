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
import java.util.List;
import java.util.stream.Collectors;

public class DAO {

    public static void executeCreateTable(String queryString) {
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

    /**
     * Called only if not using serial database option. User defines the PrimaryKey and as such needs to be checked
     *
     * @return boolean of whether potentialNewObject primarykey isUnique, otherwise if it exists in database returns false
     */
    public static boolean checkValid(boolean primaryKeyExists,boolean uniqueFieldsExist,StringBuilder query1,StringBuilder[] query2){

        System.out.println("Query1:" + query1);
        System.out.println("Query2:" + Arrays.toString(query2));

        try (Connection conn = ConnectionService.getInstance()) {
            if (primaryKeyExists) {
                PreparedStatement statement = conn.prepareStatement(query1.toString());
                ResultSet rs = statement.executeQuery();
                if (rs.next())
                    return false; // false = not unique
            }
            if(uniqueFieldsExist){
                for (StringBuilder stringBuilder : query2) {
                    PreparedStatement statement = conn.prepareStatement(stringBuilder.toString());
                    ResultSet rs = statement.executeQuery();
                    if (rs.next())
                        return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void insert(String finalQuery) {
        try(Connection conn = ConnectionService.getInstance()) {
            PreparedStatement statement = conn.prepareStatement(finalQuery);
            statement.execute();
        } catch(SQLException e){
            System.out.println("Failed Insert");
            e.printStackTrace();
        }
    }
}
