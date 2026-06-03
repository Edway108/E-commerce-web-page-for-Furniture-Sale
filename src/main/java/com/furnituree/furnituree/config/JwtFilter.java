package com.furnituree.furnituree.config;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.furnituree.furnituree.model.User;
import com.furnituree.furnituree.repo.user_repo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final user_repo userRepo;

    public JwtFilter(user_repo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain)
            throws ServletException, IOException {

        String header = req.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                String username = JwtUtil.extractUsername(token);

                if (username != null && JwtUtil.isTokenValid(token)
                        && SecurityContextHolder.getContext().getAuthentication() == null) {

                    User user = userRepo.findByUsername(username);

                    if (user != null && Boolean.TRUE.equals(user.getActive())) {
                        String role = user.getRole();

                        if (role == null || role.isBlank()) {
                            role = "user";
                        }

                        String authorityName = "ROLE_" + role.toUpperCase();

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        username,
                                        null,
                                        List.of(new SimpleGrantedAuthority(authorityName))
                                );

                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            } catch (Exception e) {
                System.out.println("JWT error: " + e.getMessage());
            }
        }

        chain.doFilter(req, res);
    }
}