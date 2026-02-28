package com.revhire.service;

import com.revhire.dto.employer.CreateEmployerProfileRequest;
import com.revhire.dto.employer.EmployerDashboardDto;
import com.revhire.dto.employer.EmployerProfileDto;
import org.springframework.security.core.userdetails.UserDetails;

public interface EmployerService {
    EmployerProfileDto createProfile(UserDetails currentUser, CreateEmployerProfileRequest request);
    EmployerProfileDto getMyProfile(UserDetails currentUser);
    EmployerProfileDto updateProfile(UserDetails currentUser, CreateEmployerProfileRequest request);
    EmployerDashboardDto getDashboardStats(UserDetails currentUser);
}