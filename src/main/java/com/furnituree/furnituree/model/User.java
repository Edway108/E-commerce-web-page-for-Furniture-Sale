package com.furnituree.furnituree.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// this class is the definition for User class included User_Id; name; role; phonenumber; address
@Entity // to declare that this is the table to sql
public class User {
    @Id // define that this one is the id and it is unique
    @GeneratedValue(strategy = GenerationType.IDENTITY) // to make it count up when new user is created
    private Long User_Id;

    @Column(unique = true) // make username unique
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String role; // admin / manager / user

    private int phonenumber;

    private String address;

    private Boolean active = true;

    // getter and setter

    @JsonProperty("userId")
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(int phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getActive() {
        return active == null ? true : active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
