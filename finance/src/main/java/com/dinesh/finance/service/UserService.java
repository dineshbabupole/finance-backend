package com.dinesh.finance.service;

import com.dinesh.finance.dto.AuthResponse;
import com.dinesh.finance.dto.RegisterRequest;
import com.dinesh.finance.dto.UpdateRoleRequest;
import com.dinesh.finance.dto.UserResponse;
import com.dinesh.finance.model.Role;
import com.dinesh.finance.dto.*;
import com.dinesh.finance.model.User;
import com.dinesh.finance.repository.UserRepository;
import com.dinesh.finance.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // ─── GET ALL USERS ────────────────────────────────────

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public AuthResponse registerUser(RegisterRequest request) {
        // it is for checking the user is already exist ot not with this email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        //creating user with role viewer by default because new user should have the least privilege
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .active(true)
                .build();
          if(request.getRole().equalsIgnoreCase("analyst")){
              user.setRole(Role.ANALYST);
          }
          else {
            user.setRole(Role.VIEWER);
        }
        userRepository.save(user);
        //generating jwt token and sending back to the user  for further authentication
        //it removes session management
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getRole().name(), user.getName());
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