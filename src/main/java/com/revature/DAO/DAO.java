package com.revature.DAO;

import com.revature.Example.User;
import com.revature.persistence.ConnectionService;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.util.Arrays;

public class DAO {
    public static void makeTable(Class<?> clazz){
        String tableName = clazz.getSimpleName();
        StringBuilder half1Query = new StringBuilder();
        StringBuilder half2Query = new StringBuilder();
        Field[] fields = clazz.getDeclaredFields();
        System.out.println(Arrays.toString(fields));

        half1Query.append("CREATE TABLE IF NOT EXISTS \"" + tableName + "\"(");

        //Checks for primary key and makes that first column
        for(Field field: fields){
            if(Arrays.toString(field.getAnnotations()).contains("PrimaryKey")){
                half2Query.append(field.getName()+ " " + DAOHelper.convertType(field.getType()) + " primary key");
            }
        }
        //Check for other columns
        for(Field field: fields){
            String fieldAnnotations = Arrays.toString(field.getAnnotations());

            //If the field is one we want in the database.. add it
            if(fieldAnnotations.contains("Column")|fieldAnnotations.contains("NotNull")||fieldAnnotations.contains("Unique"))
            {
                if(half2Query.length()!=0)
                    half2Query.append(", ");
                half2Query.append(field.getName()).append(" ").append(DAOHelper.convertType(field.getType())).append(" ");
                if(fieldAnnotations.contains("Unique"))
                    half2Query.append("Unique ");
                if(fieldAnnotations.contains("NotNull"))
                    half2Query.append("not null ");
            }
        }
        half2Query.append(");");
        String query = half1Query.toString() + half2Query.toString();
        System.out.println("MakeTableQuery: " + query);

        try(Connection conn = ConnectionService.getInstance()){
            PreparedStatement statement = conn.prepareStatement(query);
            statement.execute();

        }catch(SQLException e){
            e.printStackTrace();
        }

    }


    //CREATE



    //READ



    //UPDATE



    //DELETE





    public static <T> void getAnnotation(Class<T> clazz){
        Field[] fields = clazz.getFields();
        for(Field field:fields){
            System.out.println("Annotation: " + Arrays.toString(field.getAnnotations()));
        }

    }
}
