package com.revature.Example;

import com.revature.persistence.DAO;
import com.revature.services.ORM;
import com.revature.services.ORM_Helper;

public class Application {
    public static void main(String[] args) throws NoSuchFieldException {
        //TODO Have bug where Wrapper classes get converted to SQL integers and therefor cant handle null values
        User user = new User();

        user.setUsername("1");
        user.setPassword("1234");
        ORM.addRecord(user);
//        ORM.makeTable(user.getClass());
//        DAO.isPrimaryKeyUnique(user);
//        ORM.addRecord(user);

        System.out.println("Table Exist: "+DAO.doesTableExist(user.getClass()));
        System.out.println(ORM.updateRecord(user));


    }
}
