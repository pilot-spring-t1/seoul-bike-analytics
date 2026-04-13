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
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 누구나 접근 가능 (로그인, 회원가입, 정적 파일)
                .requestMatchers("/members/login", "/members/signup").permitAll()
                .requestMatchers("/assets/**", "/css/**", "/js/**", "/img/**", "/*.ico").permitAll()
                

                // 관리자 전용 그룹 (경로 패턴으로 묶기)
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/members/list", "/members/delete/**").hasRole("ADMIN")
                .requestMatchers("/archive/write", "/archive/register", "/archive/delete/**").hasRole("ADMIN")
                .requestMatchers("/dashboard/admin/**").hasRole("ADMIN") // 관리자용 대시보드가 있다면

                // 인증필요
                .requestMatchers("/dashboard/**").authenticated() 
                .requestMatchers("/archive/download/**", "/attachments/download/**").authenticated()
                .requestMatchers("/api/notifications/**").authenticated()
                .requestMatchers("/archive/download/**", "/boards/like/**", "/boards/comment/**").authenticated()
                .requestMatchers("/boards/notice", "/boards/suggestion", "/boards/view/**").authenticated()
                .requestMatchers("/archive/list").authenticated()
                
                // 나머지 모든 요청도 인증 필요
                .anyRequest().authenticated()
            )
            .exceptionHandling(handler -> handler.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}