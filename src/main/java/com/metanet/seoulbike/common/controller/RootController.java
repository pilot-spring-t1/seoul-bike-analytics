package com.metanet.seoulbike.common.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {

	@GetMapping("/")
	public String index() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		// 1. 토큰이 있고 인증된 사용자라면 대시보드로 보냄
		if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
			return "redirect:/dashboard";
		}

		// 2. 인증되지 않은 사용자(토큰 없음)라면 로그인 페이지로 보냄
		return "redirect:/members/login";
	}
}