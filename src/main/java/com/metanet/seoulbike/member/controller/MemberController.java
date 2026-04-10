package com.metanet.seoulbike.member.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.metanet.seoulbike.auth.JwtTokenProvider;
import com.metanet.seoulbike.member.dto.MemberSearchDto;
import com.metanet.seoulbike.member.model.Member;
import com.metanet.seoulbike.member.service.MemberService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/members")
public class MemberController {
	
	@Autowired
	MemberService memberService;
	
	@Autowired
	JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@GetMapping("/signup")
    public String signupForm() {
        return "auth/signup";
    }
	
	@PostMapping("/signup")
	public String signUp(@ModelAttribute Member member) {
		try {
            memberService.signUp(member);
            log.info("회원가입 성공 - ID: {}", member.getMemberId());
            return "redirect:/members/login";
        } catch (Exception e) {
            log.error("회원가입 실패 - 사유: {}", e.getMessage());
            return "auth/signup"; // 실패 시 다시 가입 페이지로
        }
	}
	
	@GetMapping("/login")
    public String loginForm() {
        return "auth/login"; 
    }
	
	@PostMapping("/login")
	public String login(@RequestParam Map<String, String> user, HttpServletResponse response) {
		try {
            String jwt = memberService.login(user);
            
            Cookie cookie = new Cookie("JWT", jwt);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);
            log.info("로그인 성공 - ID: {}, JWT 쿠키 발급 완료", user.get("loginId"));
            return "redirect:/members/login"; // 로그인 성공 시 화면
        } catch (Exception e) {
        	log.warn("로그인 실패 - ID: {}, 사유: {}", user.get("loginId"), e.getMessage());
            return "redirect:/members/login?error=true";
        }
	}
	
	@GetMapping("/logout")
	public String logout(HttpServletResponse response) {
		Cookie cookie = new Cookie("JWT", null);
		
		cookie.setMaxAge(0);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		
		response.addCookie(cookie);
		
		return "redirect:/login";
	}
	
	@PostMapping("/update")
	public String updateMember(@ModelAttribute Member member) {
		memberService.updateMember(member);
		return "redirect:/members/list";
	}

	@GetMapping("/delete/{memberId}")
	public String deleteMember(@PathVariable("memberId") Long memberId) {
	    memberService.deleteMember(memberId);
	    return "redirect:/members/list";
	}
	
	@GetMapping("/list")
	public String getAllMembersByPage(@ModelAttribute("searchDto") MemberSearchDto dto, Model model) {
		Map<String, Object> result = memberService.selectAllMembersByPage(dto);
	    
	    int total = (int) result.get("total");
	    int totalPages = (int) Math.ceil((double) total / dto.getSize());
	    
	    model.addAttribute("list", result.get("list"));
	    model.addAttribute("total", total);
	    model.addAttribute("totalPages", totalPages);
	    model.addAttribute("searchDto", dto); 
	    
		return "admin-users";
	}
}
