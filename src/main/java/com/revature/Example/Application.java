package com.revature.Example;

import com.revature.DAO.DAO;
import com.revature.DAO.DAOHelper;

public class Application {
    public static void main(String[] args) throws NoSuchFieldException {
//        DAO.makeTable(User.class);
        String result = DAOHelper.convertType(User.class.getDeclaredField("age").getType());
        System.out.println(result);
        result = DAOHelper.convertType(User.class.getDeclaredField("firstName").getType());
        System.out.println(result);

    }
}
