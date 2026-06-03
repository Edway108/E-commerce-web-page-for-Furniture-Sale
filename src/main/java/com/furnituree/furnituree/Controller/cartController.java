package com.furnituree.furnituree.Controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.furnituree.furnituree.dto.addToCartRequest;
import com.furnituree.furnituree.dto.deleteFromCart;
import com.furnituree.furnituree.model.Cart;
import com.furnituree.furnituree.service.CartService;

@RestController
@RequestMapping("/cart")
@SuppressWarnings("InitializerMayBeStatic")
public class cartController {

    private final CartService cartService;

    public cartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/addcart")
    public Cart addToCart(@RequestHeader("Authorization") String header, @RequestBody addToCartRequest request) {
        return cartService.addToCart(header, request);
    }

    @GetMapping("/getcart")
    public Cart getCart(@RequestHeader("Authorization") String header) {
        return cartService.getCart(header);
    }

    @DeleteMapping("/{cartId}")
    public void deleteCart(@PathVariable Long cartId, @RequestHeader("Authorization") String header) {
        cartService.deleteCart(cartId, header);
    }

    @DeleteMapping("/item")
    public Cart deleteFromCart(@RequestHeader("Authorization") String header, @RequestBody deleteFromCart request) {
        return cartService.deleteFromCart(header, request);
    }
}
