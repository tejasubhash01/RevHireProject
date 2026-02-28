package com.revhire.service;

import com.revhire.dto.auth.RegisterRequest;
import com.revhire.entity.User;
import com.revhire.entity.enums.Role;
import com.revhire.exception.BadRequestException;
import com.revhire.repository.EmployerProfileRepository;
import com.revhire.repository.JobSeekerProfileRepository;
import com.revhire.repository.UserRepository;
import com.revhire.security.JwtService;
import com.revhire.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JobSeekerProfileRepository jobSeekerProfileRepository;
    @Mock
    private EmployerProfileRepository employerProfileRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest validJobSeekerRequest;
    private RegisterRequest validEmployerRequest;
    private User mockUser;

    @BeforeEach
    void setUp() {
        validJobSeekerRequest = new RegisterRequest();
        validJobSeekerRequest.setEmail("seeker@test.com");
        validJobSeekerRequest.setPassword("password");
        validJobSeekerRequest.setName("Test Seeker");
        validJobSeekerRequest.setRole(Role.JOB_SEEKER);

        validEmployerRequest = new RegisterRequest();
        validEmployerRequest.setEmail("employer@test.com");
        validEmployerRequest.setPassword("password");
        validEmployerRequest.setName("Test Employer");
        validEmployerRequest.setRole(Role.EMPLOYER);
        validEmployerRequest.setCompanyName("TestCorp");
        validEmployerRequest.setIndustry("IT");
        validEmployerRequest.setCompanySize("50-200");

        mockUser = User.builder()
                .email(validJobSeekerRequest.getEmail())
                .name(validJobSeekerRequest.getName())
                .role(Role.JOB_SEEKER)
                .build();
        mockUser.setId(1L);
    }

    @Test
    void register_ShouldThrowException_WhenEmailExists() {
        when(userRepository.existsByEmail(validJobSeekerRequest.getEmail())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(validJobSeekerRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_ShouldCreateJobSeeker_WhenValid() {
        when(userRepository.existsByEmail(validJobSeekerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(validJobSeekerRequest.getPassword())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

        var response = authService.register(validJobSeekerRequest);

        assertNotNull(response);
        assertEquals(validJobSeekerRequest.getEmail(), response.getEmail());
        assertEquals("jwt-token", response.getToken());
        verify(jobSeekerProfileRepository, times(1)).save(any());
        verify(employerProfileRepository, never()).save(any());
    }

    @Test
    void register_ShouldCreateEmployer_WhenValid() {
        when(userRepository.existsByEmail(validEmployerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(validEmployerRequest.getPassword())).thenReturn("encoded");
        User employerUser = User.builder()
                .email(validEmployerRequest.getEmail())
                .name(validEmployerRequest.getName())
                .role(Role.EMPLOYER)
                .build();
        employerUser.setId(2L);
        when(userRepository.save(any(User.class))).thenReturn(employerUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

        var response = authService.register(validEmployerRequest);

        assertNotNull(response);
        assertEquals(validEmployerRequest.getEmail(), response.getEmail());
        verify(employerProfileRepository, times(1)).save(any());
        verify(jobSeekerProfileRepository, never()).save(any());
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsValid() {
        var loginRequest = new com.revhire.dto.auth.LoginRequest();
        loginRequest.setEmail("seeker@test.com");
        loginRequest.setPassword("password");

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(mockUser));
        when(jwtService.generateToken(mockUser)).thenReturn("jwt-token");

        var response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_ShouldThrow_WhenCredentialsInvalid() {
        var loginRequest = new com.revhire.dto.auth.LoginRequest();
        loginRequest.setEmail("seeker@test.com");
        loginRequest.setPassword("wrong");

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any());

        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_ShouldThrow_WhenUserNotFound() {
        var loginRequest = new com.revhire.dto.auth.LoginRequest();
        loginRequest.setEmail("notfound@test.com");
        loginRequest.setPassword("password");

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authService.login(loginRequest));
    }
}