package com.revhire.controller.employer;

import com.revhire.dto.ApiResponse;
import com.revhire.dto.employer.CreateEmployerProfileRequest;
import com.revhire.dto.employer.EmployerDashboardDto;
import com.revhire.dto.employer.EmployerProfileDto;
import com.revhire.service.EmployerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employers")
@RequiredArgsConstructor
public class EmployerController {

    private final EmployerService employerService;

    @PostMapping("/profile")
    public ApiResponse<EmployerProfileDto> createProfile(@AuthenticationPrincipal UserDetails currentUser,
                                                         @Valid @RequestBody CreateEmployerProfileRequest request) {
        EmployerProfileDto profile = employerService.createProfile(currentUser, request);
        return ApiResponse.success("Employer profile created", profile);
    }

    @GetMapping("/profile/me")
    public ApiResponse<EmployerProfileDto> getMyProfile(@AuthenticationPrincipal UserDetails currentUser) {
        EmployerProfileDto profile = employerService.getMyProfile(currentUser);
        return ApiResponse.success("Profile retrieved", profile);
    }

    @PutMapping("/profile")
    public ApiResponse<EmployerProfileDto> updateProfile(@AuthenticationPrincipal UserDetails currentUser,
                                                         @Valid @RequestBody CreateEmployerProfileRequest request) {
        EmployerProfileDto profile = employerService.updateProfile(currentUser, request);
        return ApiResponse.success("Profile updated", profile);
    }

    @GetMapping("/dashboard")
    public ApiResponse<EmployerDashboardDto> getDashboard(@AuthenticationPrincipal UserDetails currentUser) {
        EmployerDashboardDto dashboard = employerService.getDashboardStats(currentUser);
        return ApiResponse.success("Dashboard stats", dashboard);
    }
}