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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public UserResponse getCurrentUser(UserDetails currentUser) {
        User user = getUser(currentUser);
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
        if (request.getName() != null) user.setName(request.getName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getLocation() != null) user.setLocation(request.getLocation());
        user = userRepository.save(user);
        return getCurrentUser(currentUser);
    }

    @Override
    @Transactional
    public void changePassword(UserDetails currentUser, ChangePasswordRequest request) {
        User user = getUser(currentUser);
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword()))
            throw new BadRequestException("Old password is incorrect");
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deactivateAccount(UserDetails currentUser) {
        User user = getUser(currentUser);
        userRepository.delete(user);
    }
}