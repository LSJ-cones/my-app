package com.blog.toy.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        System.out.println("🔐 JWT 필터 - 요청 URI: " + requestURI);
        
        try {
            String jwt = getJwtFromRequest(request);
            System.out.println("🔐 JWT 필터 - JWT 토큰: " + (jwt != null ? "존재함" : "없음"));

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromToken(jwt);
                System.out.println("🔐 JWT 필터 - 사용자명: " + username);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("🔐 JWT 필터 - 인증 성공");
            } else {
                System.out.println("🔐 JWT 필터 - 인증 실패 (토큰 없음 또는 유효하지 않음)");
            }
        } catch (Exception ex) {
            System.out.println("🔐 JWT 필터 - 에러: " + ex.getMessage());
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        System.out.println("🔐 JWT 필터 - Authorization 헤더: " + bearerToken);
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            System.out.println("🔐 JWT 필터 - 추출된 토큰: " + (token.length() > 20 ? token.substring(0, 20) + "..." : token));
            return token;
        }
        System.out.println("🔐 JWT 필터 - 유효한 토큰이 없음");
        return null;
    }
}
