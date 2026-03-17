package com.furnituree.furnituree.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.furnituree.furnituree.model.User;
import java.util.List;

public interface user_repo extends JpaRepository<User, Long> {
    User findByUsername(String username);
}