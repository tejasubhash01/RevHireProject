package com.revhire.controller.users;

import com.revhire.dto.ApiResponse;
import com.revhire.dto.user.ChangePasswordRequest;
import com.revhire.dto.user.UpdateUserRequest;
import com.revhire.dto.user.UserResponse;
import com.revhire.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetails currentUser) {
        UserResponse response = userService.getCurrentUser(currentUser);
        return ApiResponse.success("User profile retrieved", response);
    }

    @PutMapping("/me")
    public ApiResponse<UserResponse> updateUser(@AuthenticationPrincipal UserDetails currentUser,
                                                @Valid @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.updateUser(currentUser, request);
        return ApiResponse.success("User profile updated", response);
    }

    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@AuthenticationPrincipal UserDetails currentUser,
                                            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(currentUser, request);
        return ApiResponse.success("Password changed successfully", null);
    }

    @DeleteMapping("/me")
    public ApiResponse<Void> deactivateAccount(@AuthenticationPrincipal UserDetails currentUser) {
        userService.deactivateAccount(currentUser);
        return ApiResponse.success("Account deactivated", null);
    }
}