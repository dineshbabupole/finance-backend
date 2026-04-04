package com.dinesh.finance.service;

import com.dinesh.finance.dto.AuthResponse;
import com.dinesh.finance.dto.LoginRequest;
import com.dinesh.finance.dto.RegisterRequest;
import com.dinesh.finance.model.Role;
import com.dinesh.finance.model.User;
import com.dinesh.finance.dto.*;
import com.dinesh.finance.model.*;
import com.dinesh.finance.repository.UserRepository;
import com.dinesh.finance.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        // it is for checking the user is already exist ot not with this email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        //creating user with role viewer by default because new user should have the least privilege
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.VIEWER)
                .active(true)
                .build();

        userRepository.save(user);
        //generating jwt token and sending back to the user  for further authentication
        //it removes session management
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getRole().name(), user.getName());
    }

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isActive()) {
            throw new RuntimeException("Your account has been deactivated");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getRole().name(), user.getName());
    }
}