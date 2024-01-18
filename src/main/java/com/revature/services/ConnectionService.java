package com.revature.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionService {
    private static Properties properties;
    private static final String propertiesPath = "src/main/resources/application.properties";
    private static Connection instance;

    private static final String url = "jdbc:postgresql://database-1.cbuweq0ues0z.us-west-2.rds.amazonaws.com:5432/postgres?currentSchema=orm";
    private static final String username =  "postgres";
    private static final String password = "taco4Isaiah#";

    //private constructor in order to make class a singleton design pattern
    private ConnectionService() {}

    private static void loadProperties() {
        properties = new Properties();
        File file = new File(propertiesPath);

        try {
            InputStream stream = new FileInputStream(new File(propertiesPath).getAbsoluteFile());
            properties.load(stream);
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
    public static Connection getInstance() {
        if (properties == null)
            //loadProperties();
        try {
            Class.forName("org.postgresql.Driver");
//            instance = DriverManager.getConnection(
//                    properties.getProperty("dbURL"),
//                    properties.getProperty("username"),
//                    properties.getProperty("password"));
            instance = DriverManager.getConnection(url,username,password);
        } catch (Exception e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        return instance;
    }
}
