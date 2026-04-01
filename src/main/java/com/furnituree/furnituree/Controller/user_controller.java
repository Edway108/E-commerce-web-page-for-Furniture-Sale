package com.furnituree.furnituree.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.furnituree.furnituree.model.User;
import com.furnituree.furnituree.repo.user_repo;

@RestController
@RequestMapping("/users")
public class user_controller {

    private final user_repo repo;

    public user_controller(user_repo repo) {
        this.repo = repo;
    }

    // admin be able to find all user
    @GetMapping("/findall")
    public List<User> getAllUser() {

        return repo.findAll();

    }

    // Create new User
    @PostMapping("/addUser")
    public User createUser(@RequestBody User user) {

        return repo.save(user);
    }

    // Delete User
    public void deleteUser(@PathVariable Long id) {
        repo.deleteById(id);
    }

    // update user infomation
    // Can be apply for both user and admin, however cannot change the role
    @PutMapping("update/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User u) {
        User old = repo.findById(id).orElse(null);

        if (old == null)
            return null;
        old.setUsername(u.getUsername());
        old.setPassword(u.getPassword());
        old.setAddress(u.getAddress());
        old.setPhonenumber(u.getPhonenumber());

        return repo.save(u);
    }

}
