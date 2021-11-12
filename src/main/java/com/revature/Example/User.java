package com.revature.Example;

import com.revature.annotations.PrimaryKey;

public class User {

    @PrimaryKey
    public int id;
    public String username;
    public String password;
}
