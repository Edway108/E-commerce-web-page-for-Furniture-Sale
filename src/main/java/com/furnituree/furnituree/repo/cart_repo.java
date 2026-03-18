package com.furnituree.furnituree.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.furnituree.furnituree.model.Cart;

public interface cart_repo extends JpaRepository<Cart, Long> {
    
}
