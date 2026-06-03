package com.furnituree.furnituree.Controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.furnituree.furnituree.model.User;
import com.furnituree.furnituree.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> RegisterUser(@RequestBody User user) {
        return ResponseEntity.ok(authService.register(user));
    }

    @CrossOrigin(origins = "http://127.0.0.1:5500", allowCredentials = "true")
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> LoginUser(@RequestBody User user) {
        return ResponseEntity.ok(authService.login(user));
    }
}
