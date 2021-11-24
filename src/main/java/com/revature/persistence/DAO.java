package com.revature.persistence;

import com.revature.annotations.PrimaryKey;
import com.revature.annotations.Unique;
import com.revature.services.ConnectionService;
import com.revature.services.ORM_Helper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DAO {

    public static void executeCreateTable(String queryString) {
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
     * Called only if not using serial database onewObjectsption. User defines the PrimaryKey and as such needs to be checked
     *
     * @return boolean of whether potentialNewObject primarykey isUnique, otherwise if it exists in database returns false
     */
    public static boolean checkValidToInsert(boolean uniqueFieldsExist, StringBuilder query1, StringBuilder[] query2) {

        try (Connection conn = ConnectionService.getInstance()) {
            PreparedStatement statement = conn.prepareStatement(query1.toString());
            ResultSet rs = statement.executeQuery();
            if (rs.next())
                return false; // false = not unique
            if (uniqueFieldsExist) {
                for (StringBuilder stringBuilder : query2) {
                    statement = conn.prepareStatement(stringBuilder.toString());
                    rs = statement.executeQuery();
                    if (rs.next())
                        return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * @param finalQuery       query to execute insert statement
     * @param getSerialIDQuery potential query for serial type of primary key. May be null
     * @return int of the serial ID otherwise returns 0
     */
    public static int insert(Object obj, String finalQuery, String getSerialIDQuery) {
        try (Connection conn = ConnectionService.getInstance()) {
            PreparedStatement statement = conn.prepareStatement(finalQuery);
            statement.execute();
            if (getSerialIDQuery != null) {
                statement = conn.prepareStatement(getSerialIDQuery);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            else return 0;
        } catch (SQLException e) {
            System.out.println("Failed Insert");
            e.printStackTrace();
        }
        return -1;
    }

    public static List<String> readByID(Class<?> clazz, String query) {
        List<String> objectField_Values = new ArrayList<>();
        try (Connection conn = ConnectionService.getInstance()) {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    objectField_Values.add(rs.getMetaData().getColumnName(i) + ":" + rs.getObject(i).toString());
                }
                return objectField_Values;
            } else return null;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<List<String>> readAll(Class<?> clazz, String query) {
        List<List<String>> objects = new ArrayList<>();
        try (Connection conn = ConnectionService.getInstance()) {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                List<String> objectField_Values = new ArrayList<>();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    objectField_Values.add(rs.getMetaData().getColumnName(i) + ":" + rs.getObject(i).toString());
                }
                objects.add(objectField_Values);
            }
            return objects;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param query the updatequery.. gets result of potentially old primary to search against
     */
    public static void update(String query) {
        try (Connection conn = ConnectionService.getInstance()) {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * @param query query to delete by specified ID within query
     */
    public static void deleteByID(String query) {
        try (Connection conn = ConnectionService.getInstance()) {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static boolean doesTableExist(Class<?> clazz) {
        String tableName = clazz.getSimpleName();
        String query = "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'project1' AND table_name = ?);";

        try (Connection conn = ConnectionService.getInstance()) {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, tableName);
            ResultSet rs = statement.executeQuery();
            if (rs.next())
                return rs.getBoolean("exists");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean checkIDExists(Object obj, Object objID, Field primaryKeyField) {
        if (!doesTableExist(obj.getClass()))
            return false;
        String query = "select \"" + primaryKeyField.getName() + "\" from \"" + obj.getClass().getSimpleName() + "\" where \""
                + primaryKeyField.getName() + "\"='" + objID + "'";
        try (Connection conn = ConnectionService.getInstance()) {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            if (rs.next())
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * checks whether potential update objects unique fields are unique in relation to database
     *
     * @return
     */
    public static boolean checkUniqueFieldsAreUnique(Object obj) {
        List<Field> uniqueFields = Arrays.stream(obj.getClass().getDeclaredFields())
                .filter(field -> Arrays.toString(field.getDeclaredAnnotations()).contains("Unique")).collect(Collectors.toList());
        List<String> queries = new ArrayList<>(uniqueFields.size());

        for (Field uniqueField : uniqueFields) {
            uniqueField.setAccessible(true);
            try {
                queries.add("Select \"" + uniqueField.getName() + "\" from \"" + obj.getClass().getSimpleName() + "\" where \""
                        + uniqueField.getName() + "\"='" + uniqueField.get(obj) + "'");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }

        try (Connection conn = ConnectionService.getInstance()) {
            for (String query : queries) {
                PreparedStatement statement = conn.prepareStatement(query);
                ResultSet rs = statement.executeQuery();
                if (rs.next())
                    return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void dropTable(Class<?> clazz) {
        String query = "Drop  table \"" + clazz.getSimpleName() + "\"";
        try (Connection conn = ConnectionService.getInstance()) {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
