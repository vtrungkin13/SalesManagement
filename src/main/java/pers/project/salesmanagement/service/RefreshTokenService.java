package pers.project.salesmanagement.service;

import pers.project.salesmanagement.entity.AppUser;
import pers.project.salesmanagement.entity.RefreshToken;

public interface RefreshTokenService {

    /**
     * Tạo mới refresh token cho user, lưu vào database.
     */
    RefreshToken createRefreshToken(AppUser user);

    /**
     * Kiểm tra refresh token hợp lệ + chưa hết hạn.
     * Nếu hợp lệ: xóa token cũ, tạo token mới (rotation) và trả về.
     * Nếu không hợp lệ hoặc hết hạn: throw InvalidRefreshTokenException.
     */
    RefreshToken verifyAndRotate(String token);

    /**
     * Tìm refresh token theo giá trị token.
     * Throw InvalidRefreshTokenException nếu không tìm thấy.
     */
    RefreshToken findByToken(String token);

    /**
     * Xóa hết refresh token của user (dùng khi logout).
     */
    void revokeAllTokens(AppUser user);
}
