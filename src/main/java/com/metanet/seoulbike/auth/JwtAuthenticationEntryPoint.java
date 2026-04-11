package com.metanet.seoulbike.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        
        // 1. 요청에서 토큰을 추출해봅니다.
        String token = jwtTokenProvider.resolveToken(request);

        // 2. 토큰이 존재하고 유효하다면? (이미 로그인된 상태인데 인증 에러가 난 경우)
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 이미 로그인 된 상태이므로 대시보드로 보냅니다.
            response.sendRedirect("/dashboard");
        } else {
            // 3. 토큰이 없거나 만료되었다면 로그인 페이지로 보냅니다.
            response.sendRedirect("/members/login");
        }
    }
}