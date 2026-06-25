package pers.project.salesmanagement.service;

import pers.project.salesmanagement.dto.request.LoginRequest;
import pers.project.salesmanagement.dto.request.LogoutRequest;
import pers.project.salesmanagement.dto.request.RefreshTokenRequest;
import pers.project.salesmanagement.dto.request.RegisterRequest;
import pers.project.salesmanagement.dto.response.AuthResponse;
import pers.project.salesmanagement.dto.response.RegisterResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);

    RegisterResponse register(RegisterRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void logout(LogoutRequest request);
}
