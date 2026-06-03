package com.furnituree.furnituree.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.furnituree.furnituree.model.OrderItem;

public interface orderItem_repo extends JpaRepository<OrderItem, Long> {
}
