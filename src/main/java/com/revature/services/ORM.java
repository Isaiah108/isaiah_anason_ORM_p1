package com.revature.services;

import com.revature.annotations.PrimaryKey;
import com.revature.persistence.DAO;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ORM {
    public static void makeTable(Class<?> clazz) {
        if (!ORM_Helper.isClassValid(clazz))
            return;

        String tableName = clazz.getSimpleName();
        StringBuilder half1Query = new StringBuilder();
        StringBuilder half2Query = new StringBuilder();
        Field[] fields = clazz.getDeclaredFields();

        half1Query.append("CREATE TABLE IF NOT EXISTS \"").append(tableName).append("\"(");

        //Checks for primary key and makes that first column
        for (Field field : fields) {
            if (Arrays.toString(field.getAnnotations()).contains("PrimaryKey")) {
                half2Query.append(field.getName()).append(" ");
                if (field.getDeclaredAnnotation(PrimaryKey.class).isSerial())
                    half2Query.append("serial");
                else
                    half2Query.append(ORM_Helper.convertType(field.getType()));
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
                half2Query.append("\"").append(field.getName()).append("\" ").append(ORM_Helper.convertType(field.getType())).append(" ");
                if (fieldAnnotations.contains("Unique"))
                    half2Query.append("Unique ");
                if (fieldAnnotations.contains("NotNull"))
                    half2Query.append("not null ");
            }
        }

        //finish building query String and execute it
        half2Query.append(");");
        DAO.executeCreateTable(half1Query.toString() + half2Query.toString());

    }

    /**
     * @param obj to be inserted into the database. If PrimaryKey is serial will update object automatically
     * @return whether record could be added
     */
    public static boolean addRecord(Object obj) {
        if (!ORM_Helper.isObjectValidInsert(obj)) {
            return false;
        }
        StringBuilder half1Query = new StringBuilder();
        StringBuilder half2Query = new StringBuilder();
        StringBuilder values = new StringBuilder();
        String getSerialIDQuery = null;
        String serialFieldName = null;

        values.append("values(");
        half1Query.append("insert into \"").append(obj.getClass().getSimpleName()).append("\"(");
        //loop over fields
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            String fieldAnnotations = Arrays.toString(field.getDeclaredAnnotations());

            //If annotation on field is one we want for columns &NOT a primary field
            if (!fieldAnnotations.contains("PrimaryKey") && (fieldAnnotations.contains("Column") || fieldAnnotations.contains("NotNull") || fieldAnnotations.contains("Unique"))) {
                if (half2Query.length() != 0) {
                    half2Query.append(",");
                    values.append(",");
                }
                half2Query.append("\"").append(field.getName()).append("\"");
                field.setAccessible(true);
                try {
                    values.append("'").append(field.get(obj)).append("'");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            //If annotation on field is primary key and not serial
            if (fieldAnnotations.contains("PrimaryKey")) {
                if (!(field.getDeclaredAnnotation(PrimaryKey.class).isSerial())) {
                    if (half2Query.length() != 0) {
                        half2Query.append(",");
                        values.append(",");
                    }
                    half2Query.append("\"").append(field.getName()).append("\"");
                    field.setAccessible(true);
                    try {
                        values.append("'").append(field.get(obj)).append("'");
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                //IF primary key field is serial
                else {
                    getSerialIDQuery = "select \"" + field.getName() + "\" from \"" + obj.getClass().getSimpleName()
                            + "\" order by " + field.getName() + " asc";
                    System.out.println("getSerialQuery: " + getSerialIDQuery);
                    serialFieldName = field.getName();
                }
            }
        }
        String finalQuery = half1Query.toString() + half2Query.toString() + ") " + values + ")";
        System.out.println("addRecord query: " + finalQuery);
        int serialIDIFExists = DAO.insert(finalQuery, getSerialIDQuery);

        if (serialFieldName != null) {
            try {
                Field serialField = obj.getClass().getDeclaredField(serialFieldName);
                serialField.setAccessible(true);
                serialField.set(obj, serialIDIFExists);

            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //READ
    public static void readRecord() {

    }

    //UPDATE
    public static boolean updateRecord(Object obj) {
        if (!ORM_Helper.isObjectValid(obj)) {
            return false;
        }
        String half1Query = "Update \"" + obj.getClass().getSimpleName() + "\" Set ";
        StringBuilder half2Query = new StringBuilder();
        Field primaryKeyField = null;

        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            String fieldAnnotations = Arrays.toString(field.getDeclaredAnnotations());

            //If annotation on field is one we want for columns &NOT a primary field
            if (!fieldAnnotations.contains("PrimaryKey") && (fieldAnnotations.contains("Column") || fieldAnnotations.contains("NotNull") || fieldAnnotations.contains("Unique"))) {
                if (half2Query.length() != 0) {
                    half2Query.append(",");
                }
                half2Query.append("\"").append(field.getName()).append("\"=");
                field.setAccessible(true);
                try {
                    half2Query.append("'").append(field.get(obj)).append("'");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            //If annotation on field is primary key and not serial
            if (fieldAnnotations.contains("PrimaryKey")) {
                primaryKeyField = field;
                if (half2Query.length() != 0) {
                    half2Query.append(",");
                }
                half2Query.append("\"").append(field.getName()).append("\"=");
                field.setAccessible(true);
                try {
                    half2Query.append("'").append(field.get(obj)).append("'");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        primaryKeyField.setAccessible(true);
        try {
            half2Query.append(" where \"").append(primaryKeyField.getName()).append("\"='").append(primaryKeyField.get(obj)).append("'");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        String finalString = half1Query + half2Query.toString();
        System.out.println("Final updateRecordString: " + finalString);
        if (ORM_Helper.isObjectValidUpdate(obj)) {
            DAO.update(obj, primaryKeyField, finalString);
            return true;
        }
        return false;
    }

    //DELETE
    public static boolean deleteRecord() {
        return true;
    }

    public static <T> void getAnnotation(Class<T> clazz) {
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            System.out.println("Annotation: " + Arrays.toString(field.getAnnotations()));
        }

    }


}
