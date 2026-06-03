package com.furnituree.furnituree.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.furnituree.furnituree.model.Order;
import com.furnituree.furnituree.model.User;

public interface order_repo extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByCreatedAtDesc(User user);

    List<Order> findAllByOrderByCreatedAtDesc();
}
