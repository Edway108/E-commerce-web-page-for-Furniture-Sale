package com.furnituree.furnituree.Controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.furnituree.furnituree.config.JwtUtil;
import com.furnituree.furnituree.model.User;
import com.furnituree.furnituree.repo.user_repo;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final user_repo uRepo;
    private final BCryptPasswordEncoder encoder;

    public AuthController(user_repo uRepo, BCryptPasswordEncoder encoder) {
        this.uRepo = uRepo;
        this.encoder = encoder;

    }

    // Register
    @PostMapping("/register")
    public String RegisterUser(@RequestBody User user) {
        User dbUser = uRepo.findByUsername(user.getUsername());
        if (dbUser == null) {
            user.setPassword(encoder.encode(user.getPassword()));

            uRepo.save(user);

            return ("User registered sucessfully");
        } else {
            return ("Username has already been registered");

        }
    }

    // Login
    @PostMapping("/login")
    public String LoginUser(@RequestBody User user) {

        User dbUser = uRepo.findByUsername(user.getUsername());

        if (dbUser == null) {
            return "User not found .";
        }
        if (encoder.matches(user.getPassword(), dbUser.getPassword())) {

            String token = JwtUtil.generateToken(user.getUsername());

            return token;
        }

        return "Wrong Password";
    }

}
