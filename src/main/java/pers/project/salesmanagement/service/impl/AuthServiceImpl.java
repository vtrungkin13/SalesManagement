package pers.project.salesmanagement.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pers.project.salesmanagement.dto.request.LoginRequest;
import pers.project.salesmanagement.dto.request.LogoutRequest;
import pers.project.salesmanagement.dto.request.RefreshTokenRequest;
import pers.project.salesmanagement.dto.request.RegisterRequest;
import pers.project.salesmanagement.dto.response.AuthResponse;
import pers.project.salesmanagement.dto.response.RegisterResponse;
import pers.project.salesmanagement.entity.AppUser;
import pers.project.salesmanagement.entity.RefreshToken;
import pers.project.salesmanagement.entity.Role;
import pers.project.salesmanagement.exception.EmailAlreadyExistsException;
import pers.project.salesmanagement.repository.AppUserRepository;
import pers.project.salesmanagement.repository.RoleRepository;
import pers.project.salesmanagement.security.AppUserDetails;
import pers.project.salesmanagement.security.JwtService;
import pers.project.salesmanagement.service.AuthService;
import pers.project.salesmanagement.service.RefreshTokenService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final String DEFAULT_ROLE = "USER";

    private final AuthenticationManager authenticationManager;
    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        AppUser appUser = appUserRepository.findByEmail(request.email()).orElseThrow();
        AppUserDetails userDetails = new AppUserDetails(appUser);

        String accessToken = jwtService.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(appUser);

        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {
        // Check email uniqueness
        if (appUserRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailAlreadyExistsException(request.email());
        }

        // Hash password — plain-text password is never stored
        String hashedPassword = passwordEncoder.encode(request.password());

        // Assign default USER role (if no role found, register without role)
        List<Role> roles = roleRepository.findByName(DEFAULT_ROLE)
                .map(List::of)
                .orElse(List.of());

        AppUser appUser = new AppUser();
        appUser.setEmail(request.email());
        appUser.setPassword(hashedPassword);
        appUser.setName(request.name());
        appUser.setPhone(request.phone());
        appUser.setRoles(roles);

        AppUser savedUser = appUserRepository.save(appUser);

        // Build response — password hash is NEVER included
        List<String> rolesName = savedUser.getRoles() == null
                ? List.of()
                : savedUser.getRoles().stream().map(Role::getName).toList();

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getPhone(),
                savedUser.getStatus(),
                savedUser.getCreatedAt(),
                rolesName);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        // Verify & rotate: xóa token cũ, tạo token mới
        RefreshToken newRefreshToken = refreshTokenService.verifyAndRotate(
                request.refreshToken());

        // Tạo access token mới
        AppUser appUser = newRefreshToken.getAppUser();
        AppUserDetails userDetails = new AppUserDetails(appUser);
        String accessToken = jwtService.generateToken(userDetails);

        return new AuthResponse(accessToken, newRefreshToken.getToken());
    }

    @Override
    public void logout(LogoutRequest request) {
        // Tìm refresh token trong DB
        RefreshToken refreshToken = refreshTokenService
                .findByToken(request.refreshToken());

        // Xóa hết refresh token của user
        refreshTokenService.revokeAllTokens(refreshToken.getAppUser());
    }
}
