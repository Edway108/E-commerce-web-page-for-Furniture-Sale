package com.furnituree.furnituree.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.furnituree.furnituree.dto.ChangePasswordRequest;
import com.furnituree.furnituree.dto.ProfileUpdateRequest;
import com.furnituree.furnituree.dto.RoleUpdateRequest;
import com.furnituree.furnituree.dto.UserCreateRequest;
import com.furnituree.furnituree.dto.UserResponse;
import com.furnituree.furnituree.dto.UserUpdateRequest;
import com.furnituree.furnituree.exception.BadRequestException;
import com.furnituree.furnituree.exception.ResourceNotFoundException;
import com.furnituree.furnituree.model.User;
import com.furnituree.furnituree.repo.user_repo;

@Service
public class UserService {

    private static final Set<String> ALLOWED_ROLES = Set.of("admin", "manager", "user");

    private final user_repo userRepo;
    private final BCryptPasswordEncoder encoder;

    public UserService(user_repo userRepo, BCryptPasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    public List<UserResponse> getAllUser() {
        return userRepo.findAll().stream().map(UserResponse::new).collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        return new UserResponse(findUser(id));
    }

    public UserResponse getMyProfile() {
        return new UserResponse(getCurrentUser());
    }

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        ensureUsernameAvailable(request.getUsername(), null);

        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(normalizeRole(request.getRole()));
        user.setPhonenumber(safePhone(request.getPhonenumber()));
        user.setAddress(request.getAddress());
        user.setActive(request.getActive() == null ? true : request.getActive());

        return new UserResponse(userRepo.save(user));
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = findUser(id);
        ensureUsernameAvailable(request.getUsername(), id);

        user.setUsername(request.getUsername().trim());
        user.setAddress(request.getAddress());
        user.setPhonenumber(safePhone(request.getPhonenumber()));
        user.setRole(normalizeRole(request.getRole()));
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        return new UserResponse(userRepo.save(user));
    }

    @Transactional
    public UserResponse updateProfile(ProfileUpdateRequest request) {
        User current = getCurrentUser();

        current.setPhonenumber(safePhone(request.getPhonenumber()));
        current.setAddress(request.getAddress());

        return new UserResponse(userRepo.save(current));
    }

    @Transactional
    public Map<String, String> changePassword(ChangePasswordRequest request) {
        User current = getCurrentUser();

        if (!encoder.matches(request.getCurrentPassword(), current.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Confirm password does not match");
        }

        if (encoder.matches(request.getNewPassword(), current.getPassword())) {
            throw new BadRequestException("New password must be different from current password");
        }

        current.setPassword(encoder.encode(request.getNewPassword()));
        userRepo.save(current);

        return Map.of("message", "Password changed successfully");
    }

    @Transactional
    public UserResponse deactivateUser(Long id) {
        User user = findUser(id);
        user.setActive(false);
        return new UserResponse(userRepo.save(user));
    }

    @Transactional
    public UserResponse activateUser(Long id) {
        User user = findUser(id);
        user.setActive(true);
        return new UserResponse(userRepo.save(user));
    }

    @Transactional
    public UserResponse changeRole(Long id, RoleUpdateRequest request) {
        User user = findUser(id);
        user.setRole(normalizeRole(request.getRole()));
        return new UserResponse(userRepo.save(user));
    }

    @Transactional
    public Map<String, String> softDeleteUser(Long id) {
        User user = findUser(id);
        user.setActive(false);
        userRepo.save(user);
        return Map.of("message", "User deactivated");
    }

    private User findUser(Long id) {
        return userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please login again");
        }

        String username = authentication.getPrincipal().toString();
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User account not found");
        }

        if (Boolean.FALSE.equals(user.getActive())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User account is inactive");
        }

        return user;
    }

    private void ensureUsernameAvailable(String username, Long currentUserId) {
        User existing = userRepo.findByUsername(username.trim());
        if (existing != null && (currentUserId == null || !existing.getUser_Id().equals(currentUserId))) {
            throw new BadRequestException("Username already exists");
        }
    }

    private String normalizeRole(String role) {
        String normalized = role == null || role.isBlank() ? "user" : role.trim().toLowerCase();
        if (!ALLOWED_ROLES.contains(normalized)) {
            throw new BadRequestException("Role must be admin, manager, or user");
        }
        return normalized;
    }

    private int safePhone(Integer phone) {
        if (phone == null) {
            return 0;
        }
        if (phone < 0) {
            throw new BadRequestException("Phone number must be 0 or greater");
        }
        return phone;
    }
}
