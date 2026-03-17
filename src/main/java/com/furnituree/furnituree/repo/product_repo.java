package com.furnituree.furnituree.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.furnituree.furnituree.model.Product;

public interface product_repo extends JpaRepository<Product, Long> {
    
}
