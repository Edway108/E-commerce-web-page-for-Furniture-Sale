package com.furnituree.furnituree.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long User_Id;

    @Column(unique = true)
    private String username;

    private String password;

    // getter and setter

    public Long getUser_Id() {
        return User_Id;
    }

    public void setUser_Id(Long User_Id) {
        this.User_Id = User_Id;
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

}
