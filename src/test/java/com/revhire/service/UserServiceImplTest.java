package com.revhire.service;

import com.revhire.dto.user.ChangePasswordRequest;
import com.revhire.dto.user.UpdateUserRequest;
import com.revhire.entity.User;
import com.revhire.entity.enums.Role;
import com.revhire.exception.BadRequestException;
import com.revhire.exception.NotFoundException;
import com.revhire.repository.UserRepository;
import com.revhire.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDetails mockUserDetails;
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("user@test.com")
                .password("pass")
                .roles("USER")
                .build();

        mockUser = User.builder()
                .email("user@test.com")
                .name("Test User")
                .phone("1234567890")
                .location("Mumbai")
                .role(Role.JOB_SEEKER)
                .build();
        mockUser.setId(1L);
    }

    @Test
    void getCurrentUser_WhenUserNotFound_ThrowsNotFoundException() {
        when(userRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getCurrentUser(mockUserDetails));
    }

    @Test
    void getCurrentUser_ReturnsUser() {
        when(userRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(mockUser));
        var result = userService.getCurrentUser(mockUserDetails);
        assertEquals(mockUser.getEmail(), result.getEmail());
        assertEquals(mockUser.getName(), result.getName());
    }

    @Test
    void updateUser_UpdatesFields() {
        when(userRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Updated Name");
        request.setPhone("9999999999");
        request.setLocation("Delhi");

        var result = userService.updateUser(mockUserDetails, request);
        assertEquals("Updated Name", result.getName());
        assertEquals("9999999999", result.getPhone());
        assertEquals("Delhi", result.getLocation());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void changePassword_Success() {
        when(userRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("oldPass", mockUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNew");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPass");
        request.setNewPassword("newPass");

        userService.changePassword(mockUserDetails, request);

        verify(userRepository, times(1)).save(mockUser);
        assertEquals("encodedNew", mockUser.getPassword());
    }

    @Test
    void changePassword_WrongOld_ThrowsBadRequest() {
        when(userRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("wrong", mockUser.getPassword())).thenReturn(false);

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("wrong");
        request.setNewPassword("newPass");

        assertThrows(BadRequestException.class, () -> userService.changePassword(mockUserDetails, request));
    }

    @Test
    void deactivateAccount_DeletesUser() {
        when(userRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(mockUser));
        doNothing().when(userRepository).delete(mockUser);

        userService.deactivateAccount(mockUserDetails);

        verify(userRepository, times(1)).delete(mockUser);
    }
}