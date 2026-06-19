package pers.project.salesmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pers.project.salesmanagement.dto.request.CreateAppUserRequest;
import pers.project.salesmanagement.dto.request.LoginRequest;
import pers.project.salesmanagement.dto.response.AppUserResponse;
import pers.project.salesmanagement.dto.response.AuthResponse;
import pers.project.salesmanagement.service.AppUserService;
import pers.project.salesmanagement.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AppUserService appUserService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return new ResponseEntity<>(authService.login(request), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<AppUserResponse> register(@RequestBody CreateAppUserRequest request) {
        return new ResponseEntity<>(appUserService.createUser(request), HttpStatus.CREATED);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return new ResponseEntity<>("Hello World!", HttpStatus.OK);
    }
}
