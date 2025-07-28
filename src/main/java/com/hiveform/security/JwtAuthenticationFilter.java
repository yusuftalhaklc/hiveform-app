package com.hiveform.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.hiveform.handler.AuthenticationRequiredException;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            final String authHeader = request.getHeader("Authorization");
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                if (isPublicEndpoint(request.getRequestURI())) {
                    filterChain.doFilter(request, response);
                    return;
                }
                throw new AuthenticationRequiredException("Authorization header is required");
            }
            
            String jwt = authHeader.substring(7);
            
            if (jwtUtil.isTokenValid(jwt, jwtUtil.extractEmail(jwt))) {
                JwtClaim jwtClaim = jwtUtil.extractJwtClaim(jwt);
                
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    jwtClaim,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + jwtClaim.getRole()))
                );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                logger.warn("Invalid JWT token provided");
                throw new AuthenticationRequiredException("Invalid authentication token");
            }
        } catch (AuthenticationRequiredException e) {
            throw e;
        } catch (Exception e) {
            logger.error("JWT token validation failed: " + e.getMessage());
            throw new AuthenticationRequiredException("Authentication required");
        }
        
        filterChain.doFilter(request, response);
    }
    
    private boolean isPublicEndpoint(String requestURI) {
        return requestURI.startsWith("/api/auth/") || 
               requestURI.startsWith("/api/form/") && requestURI.matches(".*/\\w+$"); // Form görüntüleme
    }
}