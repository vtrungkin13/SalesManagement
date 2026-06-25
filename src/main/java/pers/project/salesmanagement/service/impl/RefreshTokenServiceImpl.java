package pers.project.salesmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.project.salesmanagement.entity.AppUser;
import pers.project.salesmanagement.entity.RefreshToken;
import pers.project.salesmanagement.exception.InvalidRefreshTokenException;
import pers.project.salesmanagement.repository.RefreshTokenRepository;
import pers.project.salesmanagement.service.RefreshTokenService;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(AppUser user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setAppUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshExpiration));

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional
    public RefreshToken verifyAndRotate(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidRefreshTokenException(
                        "Refresh token không tồn tại"));

        // Kiểm tra hết hạn
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            // Token hết hạn → xóa hết token của user (bảo mật)
            refreshTokenRepository.deleteByAppUser(refreshToken.getAppUser());
            throw new InvalidRefreshTokenException(
                    "Refresh token đã hết hạn. Vui lòng đăng nhập lại");
        }

        // Rotation: xóa token cũ, tạo token mới
        AppUser user = refreshToken.getAppUser();
        refreshTokenRepository.delete(refreshToken);

        return createRefreshToken(user);
    }

    @Override
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidRefreshTokenException(
                        "Refresh token không tồn tại"));
    }

    @Override
    @Transactional
    public void revokeAllTokens(AppUser user) {
        refreshTokenRepository.deleteByAppUser(user);
    }
}
