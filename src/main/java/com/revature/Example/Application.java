package com.revature.Example;

import com.revature.persistence.DAO;
import com.revature.services.ORM;

public class Application {
    public static void main(String[] args) throws NoSuchFieldException {

        User user = new User();

        user.setId(5);
        user.setUsername("Isaiah");
        user.setPassword("1234");
        ORM.makeTable(user.getClass());
        ORM.addRecord(user);
//        DAO.isPrimaryKeyUnique(user);
//        ORM.addRecord(user);

        System.out.println("User Password:" + user.getPassword());
        boolean isValid = DAO.isObjectValidInsert(user);
        System.out.println("IsValid: " + isValid);


    }
}
