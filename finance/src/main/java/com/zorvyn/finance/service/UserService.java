package com.zorvyn.finance.service;

import com.zorvyn.finance.dto.*;
import com.zorvyn.finance.model.User;
import com.zorvyn.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // ─── GET ALL USERS ────────────────────────────────────
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── GET USER BY ID ───────────────────────────────────
    public UserResponse getUserById(Long id) {
        User user = findById(id);
        return toResponse(user);
    }

    // ─── UPDATE ROLE ──────────────────────────────────────
    public UserResponse updateRole(Long id, UpdateRoleRequest request) {
        User user = findById(id);
        user.setRole(request.getRole());
        return toResponse(userRepository.save(user));
    }

    // ─── ACTIVATE USER ────────────────────────────────────
    public UserResponse activateUser(Long id) {
        User user = findById(id);

        if (user.isActive()) {
            throw new RuntimeException("User is already active");
        }

        user.setActive(true);
        return toResponse(userRepository.save(user));
    }

    // ─── DEACTIVATE USER ──────────────────────────────────
    public UserResponse deactivateUser(Long id) {
        User user = findById(id);

        if (!user.isActive()) {
            throw new RuntimeException("User is already inactive");
        }

        user.setActive(false);
        return toResponse(userRepository.save(user));
    }

    // ─── HELPERS ──────────────────────────────────────────
    private User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.isActive()
        );
    }
}