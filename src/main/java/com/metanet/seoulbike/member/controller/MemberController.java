package com.metanet.seoulbike.member.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.metanet.seoulbike.auth.JwtTokenProvider;
import com.metanet.seoulbike.member.model.Member;
import com.metanet.seoulbike.member.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class MemberController {
	
	@Autowired
	MemberService memberService;
	
	@Autowired
	JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@PostMapping("/login")
	public String login(@RequestBody Map<String, String> user) {
		log.info(user.toString());
		Member member = memberService.selectMember(user.get("userId"));
		if (member == null) {
			throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다");
		}
		if (!passwordEncoder.matches(user.get("userPw"), member.getUserPw())) {
			throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다");
		}
		return jwtTokenProvider.generateToken(member);
	}
	
	@GetMapping("/test")
	public String test(HttpServletRequest request) {
		String token = jwtTokenProvider.resolveToken(request);
		log.info("token {}", token);
		Authentication auth = jwtTokenProvider.getAuthentication(token);
		log.info("principal {}, jwtTokenProvider {}, authorities {}", auth.getPrincipal(), auth.getName(), auth.getAuthorities());
		return jwtTokenProvider.getUserId(token);
	}
}
