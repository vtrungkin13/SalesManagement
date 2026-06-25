package pers.project.salesmanagement.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TenantSecurityUtil {

    public static UUID getCurrentTenantId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AppUserDetails userDetails) {
            return userDetails.getTenantId();
        }
        return TenantContext.getTenantId();
    }

    public static void verifyTenantAccess(UUID entityTenantId) {
        UUID currentTenantId = getCurrentTenantId();
        if (currentTenantId == null || !currentTenantId.equals(entityTenantId)) {
            throw new AccessDeniedException("Bạn không có quyền truy cập dữ liệu của tenant này.");
        }
    }
}
