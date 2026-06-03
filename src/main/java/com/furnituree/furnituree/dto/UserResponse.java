package com.furnituree.furnituree.dto;

import com.furnituree.furnituree.model.User;

public class UserResponse {
    private Long userId;
    private String username;
    private String role;
    private int phonenumber;
    private String address;
    private Boolean active;

    public UserResponse(User user) {
        this.userId = user.getUser_Id();
        this.username = user.getUsername();
        this.role = user.getRole();
        this.phonenumber = user.getPhonenumber();
        this.address = user.getAddress();
        this.active = user.getActive();
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public int getPhonenumber() {
        return phonenumber;
    }

    public String getAddress() {
        return address;
    }

    public Boolean getActive() {
        return active;
    }
}
