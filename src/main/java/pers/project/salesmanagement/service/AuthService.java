package pers.project.salesmanagement.service;

import pers.project.salesmanagement.dto.request.LoginRequest;
import pers.project.salesmanagement.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
}
