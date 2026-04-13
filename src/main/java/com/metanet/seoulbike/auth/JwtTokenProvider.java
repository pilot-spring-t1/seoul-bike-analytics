package com.metanet.seoulbike.auth;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.metanet.seoulbike.member.model.Member;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

	private static final String SECRET_KEY = System.getenv("JWT_SECRET_KEY");
	private static final byte[] decodedKey = Decoders.BASE64.decode(SECRET_KEY);
	private static final SecretKey key = new SecretKeySpec(decodedKey, "HmacSHA256");

	private static final String AUTH_HEADER = "X-AUTH-TOKEN";
	private long tokenValidTime = 30 * 60 * 1000L; // 30분

	@Autowired
	@Lazy
	UserDetailsService userDetailsService;

	/**
	 * JWT 토큰 생성 memberId와 role을 페이로드(Claims)에 포함하여 생성합니다.
	 */
	public String generateToken(Member member) {
		long now = System.currentTimeMillis();

		// 1. Claims 생성: 사용자 식별 정보 및 권한 정보를 담습니다.
		Claims claims = Jwts.claims().subject(member.getLoginId()) // 토큰 제목 (로그인 ID)
				.issuer("seoul-bike-analytics") // 발급자
				.issuedAt(new Date(now)) // 발급 시간
				.expiration(new Date(now + tokenValidTime)) // 만료 시간
				.add("memberId", member.getMemberId()) // ★ PK (Long) 추가
				.add("role", member.getRole()) // ★ 권한 추가
				.add("name", member.getName()) // (선택) 이름 추가 시 컨트롤러가 더 편해짐
				.build();

		// 2. 토큰 빌드 및 서명
		return Jwts.builder().claims(claims).signWith(key) // SecretKey로 서명
				.compact();
	}

	/**
	 * 요청에서 토큰 추출 (헤더 우선, 없으면 쿠키 확인)
	 */
	public String resolveToken(HttpServletRequest request) {
		// 1. 헤더(X-AUTH-TOKEN)에서 토큰 추출 시도
		String bearerToken = request.getHeader(AUTH_HEADER);
		if (bearerToken != null && !bearerToken.isEmpty()) {
			return bearerToken;
		}

		// 2. 쿠키(JWT)에서 토큰 추출 시도 (브라우저 주소창 입력 등 GET 요청 대응)
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("JWT".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}

		return null;
	}

	/**
	 * 토큰 복호화 및 페이로드 추출
	 */
	private Claims parseClaims(String token) {
		return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
	}

	/**
	 * 토큰에서 로그인 ID 추출
	 */
	public String getUserId(String token) {
		return parseClaims(token).getSubject();
	}

	/**
	 * 토큰을 기반으로 Authentication 객체 생성
	 */
	public Authentication getAuthentication(String token) {
		Claims claims = parseClaims(token);
		String role = claims.get("role", String.class);

		if (role == null) {
			throw new RuntimeException("권한 정보가 없는 토큰입니다.");
		}

		// 1. 권한 목록 생성
		Collection<? extends GrantedAuthority> authorities = Collections
				.singletonList(new SimpleGrantedAuthority(role));

		// 2. 중요: 시큐리티 기본 User 대신, 실제 우리 서비스의 'Member' 객체를 생성합니다.
		// 이렇게 해야 컨트롤러에서 @AuthenticationPrincipal Member로 받을 수 있습니다.
		Member member = new Member();
		member.setLoginId(claims.getSubject());
		member.setMemberId(claims.get("memberId", Long.class)); // 토큰에 담긴 PK 세팅
		member.setRole(role);
		member.setName(claims.get("name", String.class)); // 토큰에 담긴 이름 세팅

		// 3. UsernamePasswordAuthenticationToken의 첫 번째 인자로 'member'를 넘깁니다.
		return new UsernamePasswordAuthenticationToken(member, "", authorities);
	}

	/**
	 * 토큰 유효성 및 만료 여부 확인
	 */
	public boolean validateToken(String token) {
		try {
			Claims claims = parseClaims(token);
			return !claims.getExpiration().before(new Date());
		} catch (SecurityException | MalformedJwtException e) {
			log.info("잘못된 JWT 서명입니다.");
		} catch (ExpiredJwtException e) {
			log.info("만료된 JWT 토큰입니다.");
		} catch (UnsupportedJwtException e) {
			log.info("지원되지 않는 JWT 토큰입니다.");
		} catch (IllegalArgumentException e) {
			log.info("JWT 토큰이 잘못되었습니다.");
		}
		return false;
	}

}