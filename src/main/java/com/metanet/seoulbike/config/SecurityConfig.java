package com.metanet.seoulbike.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.metanet.seoulbike.auth.JwtAuthenticationEntryPoint;
import com.metanet.seoulbike.auth.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 컨트롤러의 @PreAuthorize 활성화
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter authenticationFilter;
    
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. CSRF 비활성화 (JWT 사용 시 필수)
            .csrf(csrf -> csrf.disable())
            
            // 2. 세션 정책: Stateless (서버에 세션을 저장하지 않음)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 3. 권한 설정
            .authorizeHttpRequests(auth -> auth
                // [누구나 접근 가능]
                .requestMatchers(
                    "/members/login", "/members/signup", // 회원가입/로그인 경로 (정확한 매칭)
                    "/boards/notice", "/boards/suggestion", // 게시판 목록
                    "/boards/view/**",                     // 게시판 상세
                    "/archive/list",                        // 아카이브 목록
                    "/assets/**", "/css/**", "/js/**", "/img/**", "/favicon.ico" // 정적 리소스
                ).permitAll()

                // [인증된 사용자만 접근 가능]
                .requestMatchers(
                    "/archive/download/**",
                    "/boards/like/**",
                    "/boards/comment/**",
                    "/dashboard/**"                        // 대시보드 및 상세 분석
                ).authenticated()

                // [관리자 전용]
                .requestMatchers(
                    "/archive/write", 
                    "/archive/register", 
                    "/archive/delete/**",
                    "/members/list",                       // 회원 관리 목록
                    "/members/delete/**"
                ).hasRole("ADMIN")

                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )
            
            // 예외 처리 핸들러 등록
            .exceptionHandling(handler -> handler
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
            
            // 4. JWT 필터 배치
            .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}