package pers.project.salesmanagement.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

        @Value("${jwt.secret-key}")
        private String secretKey;

        @Value("${jwt.expiration}")
        private long expiration;

        private Key getSignInKey() {
                byte[] keyBytes = Decoders.BASE64.decode(secretKey);

                return Keys.hmacShaKeyFor(keyBytes);
        }

        public String generateToken(
                        AppUserDetails userDetails) {

                UUID tenantId = userDetails.getTenantId();

                return Jwts.builder()
                                .subject(userDetails.getUsername())
                                .claim("tenantId", tenantId != null ? tenantId.toString() : null)
                                .issuedAt(new Date())
                                .expiration(
                                                new Date(
                                                                System.currentTimeMillis()
                                                                                + expiration))
                                .signWith(
                                                getSignInKey())
                                .compact();
        }

        public String extractUsername(
                        String token) {

                return Jwts.parser()
                                .verifyWith(
                                                (SecretKey) getSignInKey())
                                .build()
                                .parseSignedClaims(token)
                                .getPayload()
                                .getSubject();
        }

        public UUID extractTenantId(
                        String token) {
                String tenantIdStr = Jwts.parser()
                                .verifyWith(
                                                (SecretKey) getSignInKey())
                                .build()
                                .parseSignedClaims(token)
                                .getPayload()
                                .get("tenantId", String.class);
                return tenantIdStr != null ? UUID.fromString(tenantIdStr) : null;
        }

        public boolean isTokenExpired(
                        String token) {

                return Jwts.parser()
                                .verifyWith(
                                                (SecretKey) getSignInKey())
                                .build()
                                .parseSignedClaims(token)
                                .getPayload()
                                .getExpiration()
                                .before(new Date());
        }

        public boolean isTokenValid(
                        String token,
                        UserDetails userDetails) {

                String username = extractUsername(token);

                return username.equals(
                                userDetails.getUsername())
                                && !isTokenExpired(token);
        }
}
