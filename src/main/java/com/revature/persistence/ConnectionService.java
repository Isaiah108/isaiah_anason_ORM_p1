package com.revature.persistence;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Handles connecting to the database. Only ever has 1 connection open
 */
public class ConnectionService {

    private static Properties properties;
    private static final String propertiesPath = "src/main/resources/application.properties";
    private static Connection instance;

    private ConnectionService() {

    }

    /**
     * loads loggin credentials of the database
     */
    private static void loadProperties() {
        properties = new Properties();
        File file = new File(propertiesPath);

        try {
            InputStream stream = new FileInputStream(new File(propertiesPath).getAbsoluteFile());
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return Connection to the database
     */
    public static Connection getInstance() {
        if (properties == null)
            loadProperties();
        try {
            Class.forName("org.postgresql.Driver");
            instance = DriverManager.getConnection(
                    properties.getProperty("dbURL"),
                    properties.getProperty("username"),
                    properties.getProperty("password"));
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return instance;
    }
}