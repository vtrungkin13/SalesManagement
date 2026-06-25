package pers.project.salesmanagement.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pers.project.salesmanagement.entity.AppUser;
import pers.project.salesmanagement.entity.status.UserStatus;

import java.util.Collection;
import java.util.UUID;

@RequiredArgsConstructor
public class AppUserDetails implements UserDetails {

    private final AppUser appUser;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return appUser.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName())).toList();
    }

    public UUID getId() {
        return appUser.getId();
    }

    public UUID getTenantId() {
        return appUser.getTenant() != null ? appUser.getTenant().getId() : null;
    }

    @Override
    public String getPassword() {
        return appUser.getPassword();
    }

    @Override
    public String getUsername() {
        return appUser.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return appUser.getStatus() == UserStatus.ACTIVE;
    }
}
