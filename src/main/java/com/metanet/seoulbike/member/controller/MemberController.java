package com.metanet.seoulbike.member.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.metanet.seoulbike.auth.JwtTokenProvider;
import com.metanet.seoulbike.common.ApiResponse;
import com.metanet.seoulbike.member.model.Member;
import com.metanet.seoulbike.member.service.MemberService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/member")
public class MemberController {
	
	@Autowired
	MemberService memberService;
	
	@Autowired
	JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<String>> signUp(@RequestBody Member member) {
		memberService.signUp(member);
		return ResponseEntity.ok(ApiResponse.success("회원가입 성공"));
	}
	
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<String>> login(@RequestBody Map<String, String> user, HttpServletResponse response) {
		String jwt = memberService.login(user);
		
		Cookie cookie = new Cookie("JWT", jwt);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		
		response.addCookie(cookie);
        
		return ResponseEntity.ok(ApiResponse.success("로그인 성공"));
	}
	
	@GetMapping("/logout")
	public ResponseEntity<ApiResponse<String>> logout(HttpServletResponse response) {
		Cookie cookie = new Cookie("JWT", null);
		
		cookie.setMaxAge(0);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		
		response.addCookie(cookie);
		
		return ResponseEntity.ok(ApiResponse.success("로그아웃 성공"));
	}
	
	@GetMapping("/test")
	public String test(HttpServletRequest request) {
		String token = jwtTokenProvider.resolveToken(request);
		log.info("token {}", token);
		Authentication auth = jwtTokenProvider.getAuthentication(token);
		log.info("principal {}, jwtTokenProvider {}, authorities {}", auth.getPrincipal(), auth.getName(), auth.getAuthorities());
		log.info("isValid {}", jwtTokenProvider.validateToken(token));
		return jwtTokenProvider.getUserId(token);
	}
}
