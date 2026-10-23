package com.ai.docMind.config;

import com.ai.docMind.entity.Users;
import com.ai.docMind.exception.ResourceNotFoundException;
import com.ai.docMind.repository.UserRepository;
import com.ai.docMind.service.CustomUserDetailService;
import com.ai.docMind.service.CustomUserDetails;
import com.ai.docMind.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private  final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailService customUserDetailService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if(authHeader == null && !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }
        String token = authHeader.substring(7);
        String username;
        try {
            username = jwtService.extractUsername(token);
            if(username == null || !jwtService.isTokenValid(token, customUserDetailService.loadUserByUsername(username))){
                filterChain.doFilter(request,response);
                return;
            }
            Users user = userRepository.findByUserName(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, new CustomUserDetails(user).getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            filterChain.doFilter(request,response);
            logger.error("Invalid token: {}", e.getMessage());
            return;
        }
        filterChain.doFilter(request,response);
    }
}
