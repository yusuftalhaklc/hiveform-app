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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            final String authHeader = request.getHeader("Authorization");
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
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
                throw new AuthenticationRequiredException("Invalid authentication token");
            }
        } catch (AuthenticationRequiredException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthenticationRequiredException("Authentication required");
        }
        
        filterChain.doFilter(request, response);
    }
}