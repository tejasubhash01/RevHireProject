package com.revhire.service.impl;

import com.revhire.dto.auth.AuthResponse;
import com.revhire.dto.auth.LoginRequest;
import com.revhire.dto.auth.RegisterRequest;
import com.revhire.entity.*;
import com.revhire.entity.enums.Role;
import com.revhire.exception.BadRequestException;
import com.revhire.repository.*;
import com.revhire.security.JwtService;
import com.revhire.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final EmployerProfileRepository employerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phone(request.getPhone())
                .location(request.getLocation())
                .role(request.getRole())
                .build();
        user = userRepository.save(user);

        if (request.getRole() == Role.JOB_SEEKER) {
            JobSeekerProfile profile = JobSeekerProfile.builder()
                    .user(user)
                    .employmentStatus(request.getEmploymentStatus())
                    .build();
            jobSeekerProfileRepository.save(profile);
        } else if (request.getRole() == Role.EMPLOYER) {
            EmployerProfile profile = EmployerProfile.builder()
                    .user(user)
                    .companyName(request.getCompanyName())
                    .industry(request.getIndustry())
                    .companySize(request.getCompanySize())
                    .companyDescription(request.getCompanyDescription())
                    .website(request.getWebsite())
                    .location(request.getLocation())
                    .build();
            employerProfileRepository.save(profile);
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getEmail(), user.getRole(), user.getName());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getEmail(), user.getRole(), user.getName());
    }
}