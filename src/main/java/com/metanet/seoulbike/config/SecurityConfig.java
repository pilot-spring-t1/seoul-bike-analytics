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

import com.metanet.seoulbike.auth.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Autowired
	JwtAuthenticationFilter authenticationFilter;

	@Bean
	PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests((authHttpReq) -> authHttpReq
						// 1. 누구나 접근 가능한 경로 (목록, 상세, 로그인 등)
						.requestMatchers("/boards/notice", "/boards/suggestion", "/boards/view/**").permitAll()
						.requestMatchers("/archive/list").permitAll() // 아카이브 목록은 공개 (필요시)
						.requestMatchers("/login", "/join", "/assets/**", "/css/**", "/js/**", "/img/**").permitAll()

						// 2. 인증된 사용자만 가능한 경로 (다운로드 등)
						.requestMatchers("/archive/download/**").authenticated()
						.requestMatchers("/boards/like/**", "/boards/comment/**").authenticated()

						// 3. 관리자만 가능한 경로 (아카이브 등록/삭제 등)
						// 컨트롤러에 @PreAuthorize가 있어도 필터 레벨에서 한 번 더 막아주는 것이 안전합니다.
						.requestMatchers("/archive/write", "/archive/register", "/archive/delete/**").hasRole("ADMIN")

						// 4. 그 외 모든 요청은 인증 필요
						.anyRequest().authenticated())
				.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}