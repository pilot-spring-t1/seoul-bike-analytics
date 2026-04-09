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
	private MemberMapper memberMapper; // 변수명 변경


	// 회원가입 (POST)
	@PostMapping("/register")
	public String register(@RequestBody MemberDto memberDto) {
		// 기본 권한 설정 (ROLE_USER)
		if (memberDto.getRole() == null)
			memberDto.setRole("ROLE_USER");

		int result = memberMapper.insertMember(memberDto);
		return result > 0 ? "회원가입 성공! (ID: " + memberDto.getMemberId() + ")" : "회원가입 실패";
	}

	// 로그인 테스트 (세션 사용)
	@PostMapping("/login")
	public String login(@RequestBody MemberDto loginRequest, HttpSession session) {
		// loginId로 조회하도록 변경
		MemberDto member = memberMapper.findMemberByLoginId(loginRequest.getLoginId());

		if (member != null && member.getPassword().equals(loginRequest.getPassword())) {
			session.setAttribute("loginUser", member); // 세션에 저장
			return member.getName() + "님 환영합니다. 권한: " + member.getRole();
		}
		return "아이디 또는 비밀번호가 틀립니다.";
	}

	// 로그아웃
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "로그아웃 되었습니다.";
	}

	// 관리자 전용: 전체 회원 목록 조회
	@GetMapping("/list")
	public Object getAllMembers(HttpSession session) {
		MemberDto loginUser = (MemberDto) session.getAttribute("loginUser");

		// 권한 체크 (role 필드 사용)
		if (loginUser == null || !"ROLE_ADMIN".equals(loginUser.getRole())) {
			return "권한이 없습니다. 관리자로 로그인하세요.";
		}

		return memberMapper.findAllMembers();
	}

	// 정보 수정
	@PutMapping("/update")
	public String updateMember(@RequestBody MemberDto memberDto, HttpSession session) {
		MemberDto loginUser = (MemberDto) session.getAttribute("loginUser");
		if (loginUser == null)
			return "로그인이 필요합니다.";

		// 현재 로그인된 사용자의 loginId를 DTO에 세팅 (보안상 권장)
		memberDto.setLoginId(loginUser.getLoginId());

		int result = memberMapper.updateMember(memberDto);
		return result > 0 ? "정보 수정 완료" : "수정 실패";
	}

	// 회원 탈퇴
	@DeleteMapping("/delete/{loginId}")
	public String deleteMember(@PathVariable String loginId) {
		int result = memberMapper.deleteMember(loginId);
		return result > 0 ? loginId + " 삭제 완료" : "삭제 실패";
	}
}