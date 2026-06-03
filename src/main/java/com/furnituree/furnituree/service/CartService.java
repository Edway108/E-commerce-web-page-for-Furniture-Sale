package com.furnituree.furnituree.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.furnituree.furnituree.config.JwtUtil;
import com.furnituree.furnituree.dto.addToCartRequest;
import com.furnituree.furnituree.dto.deleteFromCart;
import com.furnituree.furnituree.exception.BadRequestException;
import com.furnituree.furnituree.exception.ResourceNotFoundException;
import com.furnituree.furnituree.model.Cart;
import com.furnituree.furnituree.model.CartItem;
import com.furnituree.furnituree.model.Product;
import com.furnituree.furnituree.model.User;
import com.furnituree.furnituree.repo.cartItem_repo;
import com.furnituree.furnituree.repo.cart_repo;
import com.furnituree.furnituree.repo.product_repo;
import com.furnituree.furnituree.repo.user_repo;

@Service
public class CartService {

    private final user_repo userRepo;
    private final cart_repo cartRepo;
    private final product_repo productRepo;
    private final cartItem_repo cartItemRepo;

    public CartService(user_repo userRepo, cart_repo cartRepo, product_repo productRepo, cartItem_repo cartItemRepo) {
        this.userRepo = userRepo;
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
        this.cartItemRepo = cartItemRepo;
    }

    @Transactional
    public Cart addToCart(String authorizationHeader, addToCartRequest request) {
        User user = getUserFromHeader(authorizationHeader);
        validateAddToCartRequest(request);

        Cart cart = cartRepo.findByUser(user);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cartRepo.save(cart);
        }

        Product product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + request.getProductId()));

        Long requestedQuantity = request.getProductQuantity();
        long stock = product.getQuantity() == null ? 0 : product.getQuantity();
        if (stock <= 0) {
            throw new BadRequestException("Product is out of stock");
        }

        CartItem cartItem = cartItemRepo.findByCartAndProduct(cart, product);
        long currentQuantity = cartItem == null ? 0 : cartItem.getQuantity();
        if (currentQuantity + requestedQuantity > stock) {
            throw new BadRequestException("Cannot add more than available stock. Available stock: " + stock);
        }

        if (cartItem != null) {
            cartItem.setQuantity(currentQuantity + requestedQuantity);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(requestedQuantity);
            cartItem.setPrice(product.getPrice());
        }
        cartItemRepo.save(cartItem);

        return cart;
    }

    public Cart getCart(String authorizationHeader) {
        User user = getUserFromHeader(authorizationHeader);
        return cartRepo.findByUser(user);
    }

    @Transactional
    public void deleteCart(Long cartId, String authorizationHeader) {
        User user = getUserFromHeader(authorizationHeader);
        Cart cart = cartRepo.findByUser(user);
        if (cart == null || !cart.getCartId().equals(cartId)) {
            throw new ResourceNotFoundException("Cart not found or unauthorized access");
        }

        cartRepo.delete(cart);
    }

    @Transactional
    public Cart deleteFromCart(String authorizationHeader, deleteFromCart request) {
        User user = getUserFromHeader(authorizationHeader);
        if (request == null || request.getProductId() == null) {
            throw new BadRequestException("Product id is required");
        }

        Cart cart = cartRepo.findByUser(user);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart not found");
        }

        Product product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + request.getProductId()));

        CartItem cartItem = cartItemRepo.findByCartAndProduct(cart, product);
        if (cartItem == null) {
            throw new ResourceNotFoundException("Item not found in cart");
        }

        cartItemRepo.delete(cartItem);
        return cartRepo.findById(cart.getCartId()).orElse(cart);
    }

    private User getUserFromHeader(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new BadRequestException("Authorization header is missing or invalid");
        }

        String token = header.substring(7);
        String username = JwtUtil.extractUsername(token);
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        return user;
    }

    private void validateAddToCartRequest(addToCartRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }
        if (request.getProductId() == null) {
            throw new BadRequestException("Product id is required");
        }
        if (request.getProductQuantity() == null || request.getProductQuantity() <= 0) {
            throw new BadRequestException("Product quantity must be greater than 0");
        }
    }
}
