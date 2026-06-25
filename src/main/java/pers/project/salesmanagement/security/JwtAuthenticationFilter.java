package pers.project.salesmanagement.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

        private final JwtService jwtService;
        private final AppUserDetailsService appUserDetailsService;

        @Override
        protected void doFilterInternal(
                        @NonNull HttpServletRequest request,
                        @NonNull HttpServletResponse response,
                        @NonNull FilterChain filterChain)
                        throws ServletException, IOException {

                final String authHeader = request.getHeader("Authorization");

                try {
                        if (authHeader != null && authHeader.startsWith("Bearer ")) {
                                final String jwt = authHeader.substring(7);
                                final String email = jwtService.extractUsername(jwt);

                                if (email != null &&
                                                SecurityContextHolder.getContext().getAuthentication() == null) {

                                        UserDetails userDetails = appUserDetailsService.loadUserByUsername(email);

                                        if (jwtService.isTokenValid(jwt, userDetails)) {

                                                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                                                userDetails,
                                                                null,
                                                                userDetails.getAuthorities());

                                                authToken.setDetails(
                                                                new WebAuthenticationDetailsSource()
                                                                                .buildDetails(request));

                                                SecurityContextHolder
                                                                .getContext()
                                                                .setAuthentication(authToken);

                                                // Set tenant context
                                                java.util.UUID tenantId = jwtService.extractTenantId(jwt);
                                                TenantContext.setTenantId(tenantId);
                                        }
                                }
                        }
                        filterChain.doFilter(request, response);
                } catch (Exception e) {
                        log.warn("JWT authentication failed: {}", e.getMessage());
                        // Do not set SecurityContext — Spring Security will return 401
                        filterChain.doFilter(request, response);
                } finally {
                        TenantContext.clear();
                }
        }
}