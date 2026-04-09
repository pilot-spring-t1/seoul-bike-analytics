package com.metanet.seoulbike.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.metanet.seoulbike.test.dto.MemberDto;
import com.metanet.seoulbike.test.mapper.MemberMapper;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/test/member")
public class MemberTestController {

    @Autowired
    private MemberMapper memberMapper; // 직접 호출이 필요한 간단한 테스트용

    @PostMapping("/register")
    public String register(@RequestBody MemberDto dto) {
        if (dto.getRole() == null) dto.setRole("ROLE_USER");
        return memberMapper.insertMember(dto) > 0 ? "성공" : "실패";
    }

    @PostMapping("/login")
    public String login(@RequestBody MemberDto req, HttpSession session) {
        MemberDto m = memberMapper.getMemberByLoginId(req.getLoginId());
        if (m != null && m.getPassword().equals(req.getPassword())) {
            session.setAttribute("loginUser", m);
            return m.getName() + "님 환영합니다.";
        }
        return "인증 실패";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "로그아웃 완료";
    }
}