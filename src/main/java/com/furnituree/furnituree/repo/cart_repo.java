package com.furnituree.furnituree.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.furnituree.furnituree.model.Cart;
import com.furnituree.furnituree.model.Product;
import com.furnituree.furnituree.model.User;

public interface cart_repo extends JpaRepository<Cart, Long> {
    Cart findByUser(User user);

    Product findByProduct(Product product);
}
