package com.furnituree.furnituree.Controller;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.furnituree.furnituree.config.JwtUtil;
import com.furnituree.furnituree.model.Cart;
import com.furnituree.furnituree.model.User;
import com.furnituree.furnituree.repo.cart_repo;
import com.furnituree.furnituree.repo.user_repo;

@RestController
@RequestMapping
public class cartController {
    private final user_repo usRepo;
    private final cart_repo caRepo;

    public cartController(user_repo usRepo, cart_repo caRepo) {
        this.caRepo = caRepo;
        this.usRepo = usRepo;
    }

    public String addtoCart(@RequestHeader("Authorization") String header) {

        //1.Find user name and connect it with the cart 
        // cut out "Bearer "
        String token = header.substring(7);
        // "take username from tokpasswordEncoder"
        String username = JwtUtil.extractUsername(token);

        User user = usRepo.findByUsername(username);

        Cart cart = caRepo.findByUser(user);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            caRepo.save(cart);

        }

        return "Cart added";
    }

}
