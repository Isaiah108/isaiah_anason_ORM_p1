package com.revature.Example;

import com.revature.persistence.DAO;
import com.revature.services.ORM;

public class Application {
    public static void main(String[] args) throws NoSuchFieldException {
//        ORM.makeTable(User.class);
        User user = new User();
        user.setid(5);
        DAO.isPrimaryKeyUnique(user);

    }
}
