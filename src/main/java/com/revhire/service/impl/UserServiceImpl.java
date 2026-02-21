package com.revhire.service.impl;

import com.revhire.dto.user.ChangePasswordRequest;
import com.revhire.dto.user.UpdateUserRequest;
import com.revhire.dto.user.UserResponse;
import com.revhire.entity.User;
import com.revhire.exception.BadRequestException;
import com.revhire.exception.NotFoundException;
import com.revhire.repository.UserRepository;
import com.revhire.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> {
                    log.error("User not found: {}", userDetails.getUsername());
                    return new NotFoundException("User not found");
                });
    }

    @Override
    public UserResponse getCurrentUser(UserDetails currentUser) {
        User user = getUser(currentUser);
        log.debug("Fetching current user: {}", user.getEmail());
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setPhone(user.getPhone());
        response.setLocation(user.getLocation());
        response.setRole(user.getRole());
        return response;
    }

    @Override
    @Transactional
    public UserResponse updateUser(UserDetails currentUser, UpdateUserRequest request) {
        User user = getUser(currentUser);
        log.info("Updating user: {}", user.getEmail());
        if (request.getName() != null) user.setName(request.getName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getLocation() != null) user.setLocation(request.getLocation());
        user = userRepository.save(user);
        log.info("User updated: {}", user.getEmail());
        return getCurrentUser(currentUser);
    }

    @Override
    @Transactional
    public void changePassword(UserDetails currentUser, ChangePasswordRequest request) {
        User user = getUser(currentUser);
        log.info("Changing password for user: {}", user.getEmail());
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            log.warn("Incorrect old password for user: {}", user.getEmail());
            throw new BadRequestException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed for user: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void deactivateAccount(UserDetails currentUser) {
        User user = getUser(currentUser);
        log.info("Deactivating account for user: {}", user.getEmail());
        userRepository.delete(user);
        log.info("Account deactivated for user: {}", user.getEmail());
    }
}