package com.revature.Example;

import com.revature.DAO.DAO;

public class Application {
    public static void main(String[] args) {
        User user = new User();
        DAO.makeTable(User.class);
    }
}
