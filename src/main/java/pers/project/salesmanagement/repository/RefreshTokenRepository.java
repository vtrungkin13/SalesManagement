package pers.project.salesmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pers.project.salesmanagement.entity.AppUser;
import pers.project.salesmanagement.entity.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByAppUser(AppUser appUser);
}
