package com.revature.Example;

import com.revature.annotations.PrimaryKey;
import com.revature.persistence.DAO;
import com.revature.services.ORM;
import com.revature.services.ORM_Helper;

import java.lang.reflect.Field;

public class Application {
    public static void main(String[] args) throws NoSuchFieldException {
        //TODO Have bug where Wrapper classes get converted to SQL integers and therefor cant handle null values
        User user = new User();

        user.setUsername("2");
        user.setPassword("1234");
        ORM.makeTable(user.getClass());
        ORM.addRecord(user);

        user.setPassword(null);

        ORM.updateRecord(user);

//        System.out.println("Table Exist: "+DAO.doesTableExist(user.getClass()));
//        System.out.println(ORM.updateRecord(user));
//        System.out.println(ORM_Helper.isObjectValidUpdate(user));

//        Field field = User.class.getDeclaredField("username");
//        DAO.checkIDExists(user,3,field);
        DAO.checkUniqueFieldsAreUnique(user);





    }
}
