package pers.project.salesmanagement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pers.project.salesmanagement.dto.request.LoginRequest;
import pers.project.salesmanagement.dto.request.LogoutRequest;
import pers.project.salesmanagement.dto.request.RefreshTokenRequest;
import pers.project.salesmanagement.dto.request.RegisterRequest;
import pers.project.salesmanagement.dto.response.AuthResponse;
import pers.project.salesmanagement.dto.response.RegisterResponse;
import pers.project.salesmanagement.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final pers.project.salesmanagement.security.LoginRateLimiter loginRateLimiter;
    private final jakarta.servlet.http.HttpServletRequest httpServletRequest;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        String ip = httpServletRequest.getRemoteAddr();
        if (!loginRateLimiter.isAllowed(ip)) {
            throw new pers.project.salesmanagement.exception.RateLimitExceededException(
                    "Bạn đã thử đăng nhập quá nhiều lần. Vui lòng thử lại sau 1 phút."
            );
        }
        return new ResponseEntity<>(authService.login(request), HttpStatus.OK);
    }

    /**
     * Đăng ký tài khoản mới.
     * - Email phải duy nhất (trả về 409 Conflict nếu đã tồn tại)
     * - Mật khẩu tối thiểu 8 ký tự (trả về 400 nếu vi phạm)
     * - Password hash KHÔNG BAO GIỜ được trả về trong response
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }

    /**
     * Làm mới access token bằng refresh token.
     * - Refresh token cũ bị xóa, token mới được tạo (rotation)
     * - Trả về access token mới + refresh token mới
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return new ResponseEntity<>(authService.refreshToken(request), HttpStatus.OK);
    }

    /**
     * Đăng xuất — xóa hết refresh token của user.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return new ResponseEntity<>("Hello World!", HttpStatus.OK);
    }
}
