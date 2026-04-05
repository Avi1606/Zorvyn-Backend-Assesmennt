package org.avi1606.financedataprocessing.service;

import lombok.extern.slf4j.Slf4j;
import org.avi1606.financedataprocessing.dto.RegisterRequest;
import org.avi1606.financedataprocessing.dto.UserMapper;
import org.avi1606.financedataprocessing.dto.UserResponse;
import org.avi1606.financedataprocessing.enums.Role;
import org.avi1606.financedataprocessing.enums.UserStatus;
import org.avi1606.financedataprocessing.exception.ResourceNotFoundException;
import org.avi1606.financedataprocessing.model.User;
import org.avi1606.financedataprocessing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + registerRequest.getEmail());
        }
        User user = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.VIEWER)
                .status(UserStatus.ACTIVE)
                .build();
        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getEmail());
        return savedUser;
    }

    // Internal method for getting User entity (not DTO)
    public User getUserEntityById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    public UserResponse getUserById(UUID userId) {
        User user = getUserEntityById(userId);
        return UserMapper.toResponse(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return UserMapper.toResponseList(users);
    }

    @Transactional
    public UserResponse updateUserStatus(UUID userId, UserStatus status) {
        User user = getUserEntityById(userId);
        user.setStatus(status);
        User updatedUser = userRepository.save(user);
        log.info("User status updated: {} - {}", userId, status);
        return UserMapper.toResponse(updatedUser);
    }

    @Transactional
    public UserResponse updateUserRole(UUID userId, Role role) {
        User user = getUserEntityById(userId);
        user.setRole(role);
        User updatedUser = userRepository.save(user);
        log.info("User role updated: {} - {}", userId, role);
        return UserMapper.toResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(UUID userId) {
        User user = getUserEntityById(userId);
        userRepository.delete(user);
        log.info("User deleted: {}", userId);
    }
}

