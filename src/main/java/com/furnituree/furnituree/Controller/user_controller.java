package com.furnituree.furnituree.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.furnituree.furnituree.dto.ChangePasswordRequest;
import com.furnituree.furnituree.dto.ProfileUpdateRequest;
import com.furnituree.furnituree.dto.RoleUpdateRequest;
import com.furnituree.furnituree.dto.UserCreateRequest;
import com.furnituree.furnituree.dto.UserResponse;
import com.furnituree.furnituree.dto.UserUpdateRequest;
import com.furnituree.furnituree.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class user_controller {

    private final UserService userService;

    public user_controller(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/findall")
    public List<UserResponse> getAllUser() {
        return userService.getAllUser();
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping({ "/me", "/profile" })
    public UserResponse getMyProfile() {
        return userService.getMyProfile();
    }

    @PostMapping("/addUser")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @PutMapping("/update/{id}")
    public UserResponse updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return userService.updateUser(id, request);
    }

    @PutMapping("/profile")
    public UserResponse updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        return userService.updateProfile(request);
    }

    @PutMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(userService.changePassword(request));
    }

    @PatchMapping("/{id}/deactivate")
    public UserResponse deactivateUser(@PathVariable Long id) {
        return userService.deactivateUser(id);
    }

    @PatchMapping("/{id}/activate")
    public UserResponse activateUser(@PathVariable Long id) {
        return userService.activateUser(id);
    }

    @PatchMapping("/{id}/role")
    public UserResponse changeRole(@PathVariable Long id, @Valid @RequestBody RoleUpdateRequest request) {
        return userService.changeRole(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> softDeleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.softDeleteUser(id));
    }
}
