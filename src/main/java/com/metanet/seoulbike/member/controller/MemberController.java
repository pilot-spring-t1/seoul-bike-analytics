package com.metanet.seoulbike.member.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.metanet.seoulbike.auth.JwtTokenProvider;
import com.metanet.seoulbike.member.dto.MemberSearchDto;
import com.metanet.seoulbike.member.model.Member;
import com.metanet.seoulbike.member.service.MemberService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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

    // --- [공통: 회원가입 및 로그인] ---

    @GetMapping("/signup")
    public String signupForm() {
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signUp(@ModelAttribute Member member, RedirectAttributes rttr) {
        try {
            memberService.signUp(member);
            rttr.addFlashAttribute("message", "회원가입이 완료되었습니다");
            return "redirect:/members/login";
        } catch (Exception e) {
            rttr.addFlashAttribute("error", "회원가입 중 오류 발생: " + e.getMessage());
            return "redirect:/members/signup?error=true";
        }
    }

    @GetMapping("/login")
    public String loginForm() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/dashboard";
        }
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
            return "redirect:/dashboard";
        } catch (Exception e) {
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
        return "redirect:/members/login";
    }

    // --- [관리자 전용: 회원 관리] ---

    /**
     * 회원 목록 조회 (검색 및 페이징)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public String getAllMembersByPage(@ModelAttribute("searchDto") MemberSearchDto dto, Authentication auth, Model model) {
        // 서비스에서 startPage, endPage, totalPages 등 모든 페이징 계산을 수행함
        Map<String, Object> result = memberService.getAllMembersByPage(dto);
        
        if (auth != null) {
            model.addAttribute("userName", auth.getName());
        } else {
            model.addAttribute("userName", "Guest");
        }
        model.addAllAttributes(result);
        model.addAttribute("searchDto", dto);
        return "members/member-list";
    }

    /**
     * 회원 수정 폼 이동 (누락되었던 부분)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{memberId}")
    public String editForm(@PathVariable("memberId") Long memberId, Model model) {
        // Service에 해당 ID로 회원 정보를 가져오는 메서드가 있어야 함
        Member member = memberService.getMemberById(memberId); 
        model.addAttribute("member", member);
        return "members/member-edit";
    }

    /**
     * 회원 정보 수정 처리
     */
    @PreAuthorize("hasRole('ADMIN') or #member.memberId == authentication.principal.memberId")
    @PostMapping("/update")
    public String updateMember(@ModelAttribute Member member, RedirectAttributes rttr) {
        memberService.updateMember(member);
        rttr.addFlashAttribute("message", "회원 정보가 수정되었습니다.");
        return "redirect:/members/list";
    }

    /**
     * 회원 삭제 처리
     */
    @PreAuthorize("hasRole('ADMIN') or #memberId == authentication.principal.memberId")
    @GetMapping("/delete/{memberId}")
    public String deleteMember(@PathVariable("memberId") Long memberId, RedirectAttributes rttr) {
        memberService.deleteMember(memberId);
        rttr.addFlashAttribute("message", "회원이 삭제되었습니다.");
        return "redirect:/members/list";
    }
}