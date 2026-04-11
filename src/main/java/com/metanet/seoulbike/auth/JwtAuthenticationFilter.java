package com.metanet.seoulbike.auth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 1. 요청에서 토큰 추출 (헤더 또는 쿠키)
            String token = jwtTokenProvider.resolveToken(request);

            // 2. 토큰 유효성 검사 및 인증 객체 생성
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                
                // 3. SecurityContext에 인증 정보 저장 (이후 컨트롤러에서 Authentication 사용 가능)
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            log.error("JWT 인증 필터에서 오류 발생: {}", ex.getMessage());
            // 인증 오류 시 응답 처리 (필요에 따라 401 에러를 내려보냄)
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Invalid or expired token\", \"message\": \"" + ex.getMessage() + "\"}");
            return;
        }

        // 4. 다음 필터로 진행
        filterChain.doFilter(request, response);
    }

    /**
     * 필터를 거치지 않을 경로 설정 (인증이 필요 없는 페이지들)
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String uri = request.getRequestURI();

        // 회원가입, 로그인, 정적 리소스, 웹소켓 등은 JWT 검사를 하지 않음
        return uri.startsWith("/members/signup") 
                || uri.startsWith("/members/login")
                || uri.startsWith("/css/") 
                || uri.startsWith("/js/") 
                || uri.startsWith("/img/")
                || uri.startsWith("/assets/")
                || uri.startsWith("/api/notifications")
                || uri.startsWith("/ws")
                || uri.startsWith("/error");
    }

}