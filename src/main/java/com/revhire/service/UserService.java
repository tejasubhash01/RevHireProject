package com.revhire.service;

import com.revhire.dto.user.ChangePasswordRequest;
import com.revhire.dto.user.UpdateUserRequest;
import com.revhire.dto.user.UserResponse;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    UserResponse getCurrentUser(UserDetails currentUser);
    UserResponse updateUser(UserDetails currentUser, UpdateUserRequest request);
    void changePassword(UserDetails currentUser, ChangePasswordRequest request);
    void deactivateAccount(UserDetails currentUser);
}