package com.revhire.service;

import com.revhire.dto.auth.AuthResponse;
import com.revhire.dto.auth.LoginRequest;
import com.revhire.dto.auth.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}