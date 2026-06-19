package pers.project.salesmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import pers.project.salesmanagement.dto.request.LoginRequest;
import pers.project.salesmanagement.dto.response.AuthResponse;
import pers.project.salesmanagement.entity.AppUser;
import pers.project.salesmanagement.repository.AppUserRepository;
import pers.project.salesmanagement.security.AppUserDetails;
import pers.project.salesmanagement.security.JwtService;
import pers.project.salesmanagement.service.AuthService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final AppUserRepository appUserRepository;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        AppUser appUser = appUserRepository.findByEmail(request.email()).orElseThrow();
        AppUserDetails userDetails = new AppUserDetails(appUser);

        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token);
    }

}
