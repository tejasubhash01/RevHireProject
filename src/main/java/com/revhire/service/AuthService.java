package com.revhire.service;

import com.revhire.dto.auth.AuthResponse;
import com.revhire.dto.auth.LoginRequest;
import com.revhire.dto.auth.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);


    String getSecurityQuestion(String email);
    String verifyAnswerAndGenerateToken(String email, String answer);
    void resetPassword(String token, String newPassword);
}