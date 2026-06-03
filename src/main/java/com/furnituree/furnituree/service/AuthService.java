package com.furnituree.furnituree.service;

import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.furnituree.furnituree.config.JwtUtil;
import com.furnituree.furnituree.exception.BadRequestException;
import com.furnituree.furnituree.model.User;
import com.furnituree.furnituree.repo.user_repo;

@Service
public class AuthService {

    private final user_repo userRepo;
    private final BCryptPasswordEncoder encoder;

    public AuthService(user_repo userRepo, BCryptPasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    public Map<String, String> register(User user) {
        User dbUser = userRepo.findByUsername(user.getUsername());
        if (dbUser != null) {
            throw new BadRequestException("Username already exists");
        }

        user.setPassword(encoder.encode(user.getPassword()));
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("user");
        }
        if (user.getActive() == null) {
            user.setActive(true);
        }

        userRepo.save(user);
        return Map.of("message", "Username successfully registered");
    }

    public Map<String, Object> login(User user) {
        User dbUser = userRepo.findByUsername(user.getUsername());

        if (dbUser == null) {
            throw new BadRequestException("Username not found");
        }
        if (Boolean.FALSE.equals(dbUser.getActive())) {
            throw new BadRequestException("This account has been deactivated");
        }
        if (!encoder.matches(user.getPassword(), dbUser.getPassword())) {
            throw new BadRequestException("Password is wrong");
        }

        String token = JwtUtil.generateToken(dbUser.getUsername());
        String role = dbUser.getRole();
        if (role == null || role.isBlank()) {
            role = "user";
        }

        return Map.of("token", token, "role", role, "userId", dbUser.getUser_Id());
    }
}
