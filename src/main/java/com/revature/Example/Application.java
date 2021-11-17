package com.revature.Example;

import com.revature.services.ORM;

public class Application {
    public static void main(String[] args) throws NoSuchFieldException {
        ORM.makeTable(User.class);

    }
}
