package com.revature.Example;

import com.revature.annotations.Column;
import com.revature.annotations.NotNull;
import com.revature.annotations.PrimaryKey;
import com.revature.annotations.Unique;

public class User {

    @PrimaryKey(isSerial = true)
    public int id;
    @Unique
    @NotNull
    private String username;
    @NotNull
    private String password;
    @Column
    private String firstName;
    @Column
    Integer age;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
