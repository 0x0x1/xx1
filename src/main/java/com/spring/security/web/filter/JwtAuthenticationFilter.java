package com.spring.security.web.filter;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.spring.security.service.JwtTokenService;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenService tokenService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenService tokenService, UserDetailsService userDetailsService) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final var token = request.getHeader(AUTHORIZATION_HEADER);

        if (token != null && token.startsWith(BEARER_PREFIX)) {
            final var trimmedToken = tokenService.trimToken(token, BEARER_PREFIX);

            if (Boolean.TRUE.equals(tokenService.isTokenValid(trimmedToken))) {
                final var extractedUsername = tokenService.extractUsername(trimmedToken);
                final var appUserPrincipal = userDetailsService.loadUserByUsername(extractedUsername);
                final var authentication = new UsernamePasswordAuthenticationToken(appUserPrincipal, null, appUserPrincipal.getAuthorities());
                //let application know the authenticated user for  the current request
                setAuthenticationToContext(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    /* @Desc Sets the context with the authentication object.
     * The authentication object will be used for Authorization.
     * @param authentication
     */
    private void setAuthenticationToContext(Authentication authentication) {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}