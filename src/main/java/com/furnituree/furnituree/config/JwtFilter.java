package com.furnituree.furnituree.config;

import java.util.ArrayList;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest req,
            HttpServletResponse res, FilterChain fil)
            throws ServletException, IOException, java.io.IOException {

        // take the header Authorization
        String header = req.getHeader("Authorization");

        // Check does it have the Bearer

        if (header != null && header.startsWith("Bearer ")) {

            // cut the token
            String token = header.substring(7);
            try {
                String username = JwtUtil.extractUsername(token);

                if (username != null && JwtUtil.isTokenValid(token)) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null,
                            new ArrayList<>());
                    SecurityContextHolder.getContext().setAuthentication(auth);

                }
            } catch (Exception e) {
                System.out.println("JWT error : " + e.getMessage());

            }
        }
        fil.doFilter(req, res);

    }

}
