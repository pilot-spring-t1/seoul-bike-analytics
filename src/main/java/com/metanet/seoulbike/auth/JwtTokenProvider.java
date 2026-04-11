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
	 * JWT 토큰 생성
	 */
	public String generateToken(Member member) {
		long now = System.currentTimeMillis();
		Claims claims = Jwts.claims()
				.subject(member.getLoginId()) 
				.issuer("seoul-bike-analytics") 
				.issuedAt(new Date(now))
				.expiration(new Date(now + tokenValidTime))
				.add("role", member.getRole())
				.build();
				
		return Jwts.builder()
				.claims(claims)
				.signWith(key)
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
		return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
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

	    Collection<? extends GrantedAuthority> authorities = 
	        Collections.singletonList(new SimpleGrantedAuthority(role));

	    UserDetails userDetails = org.springframework.security.core.userdetails.User
	            .withUsername(claims.getSubject())
	            .password("") // 패스워드는 인증 완료 후이므로 비워둠
	            .authorities(authorities)
	            .build();
				
		return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
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