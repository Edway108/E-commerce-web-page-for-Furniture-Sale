package com.furnituree.furnituree.Controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
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
    @CrossOrigin(origins = "*")
    @PostMapping("/register")
    public ResponseEntity RegisterUser(@RequestBody User user) {
        User dbUser = uRepo.findByUsername(user.getUsername());
        if (dbUser == null) {
            user.setPassword(encoder.encode(user.getPassword()));

            uRepo.save(user);

            return ResponseEntity.ok(Map.of("message", "Username sucessfully register"));
        } else {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Username already exists"));
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
