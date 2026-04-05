package org.avi1606.financedataprocessing.service;

import lombok.extern.slf4j.Slf4j;
import org.avi1606.financedataprocessing.dto.AuthResponse;
import org.avi1606.financedataprocessing.dto.LoginRequest;
import org.avi1606.financedataprocessing.dto.RegisterRequest;
import org.avi1606.financedataprocessing.model.User;
import org.avi1606.financedataprocessing.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuthService {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        User user = userService.registerUser(registerRequest);
        String token = jwtUtil.generateToken(user.getId().toString(), user.getEmail(), user.getRole().toString());
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .email(user.getEmail())
                .role(user.getRole().toString())
                .message("User registered successfully")
                .build();
    }

    public AuthResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
            User user = userService.getUserByEmail(loginRequest.getEmail());
            String token = jwtUtil.generateToken(user.getId().toString(), user.getEmail(), user.getRole().toString());
            log.info("User logged in successfully: {}", loginRequest.getEmail());
            return AuthResponse.builder()
                    .token(token)
                    .type("Bearer")
                    .email(user.getEmail())
                    .role(user.getRole().toString())
                    .message("User logged in successfully")
                    .build();
        } catch (Exception e) {
            log.warn("Authentication failed for user: {}", loginRequest.getEmail());
            throw e;
        }
    }
}

