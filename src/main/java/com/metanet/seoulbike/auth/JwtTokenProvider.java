package com.metanet.seoulbike.auth;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/*
@Slf4j
@Component
public class JwtTokenProvider {
	
	private static final String SECRET_KEY = System.getenv("JWT_SECRET_KEY");
	private static final byte[] decodedKey = Decoders.BASE64.decode(SECRET_KEY);
	private static final SecretKey key = new SecretKeySpec(decodedKey, "HmacSHA256");
	
	private static final String AUTH_HEADER = "X-AUTH-TOKEN";
	private long tokenValidTime = 30 * 60 * 1000L;
	
	@Autowired
	UserDetailsService userDetailsService;
	
	public String generateToken(Member member) {
		long now = System.currentTimeMillis();
		Claims claims = Jwts.claims()
				.subject(member.getUserId()) 
				.issuer("seoul-bike-analytics") // 토큰 발급자
				.issuedAt(new Date(now)) //발급일
				.expiration(new Date(now + tokenValidTime)) // 만료
				.add("roles", member.getUserRole()) // 역할
				.build();
		return Jwts.builder()
				.claims(claims)
				.signWith(key)
				.compact();
	}
	
	public String resolveToken(HttpServletRequest request) {
		return request.getHeader(AUTH_HEADER);
	}
	
	private Claims parseClaims(String token) {
		log.info(token);
		return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
	
	public String getUserId(String token) {
		return parseClaims(token).getSubject();
	}
	
	public Authentication getAuthentication(String token) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(getUserId(token));
		log.info(userDetails.getUsername());
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}
	
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
*/
