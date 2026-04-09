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

import com.metanet.seoulbike.test.dto.UserDto;
import com.metanet.seoulbike.test.mapper.UserMapper;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/test/user")
public class UserTestController {

	@Autowired
	private UserMapper userMapper;

	// 1. DB 연결 확인
	@GetMapping("/check")
	public String checkConnection() {
		int count = userMapper.checkDbConnection();
		return "현재 등록된 유저 수: " + count;
	}

	// 2. 회원가입 (POST)
	@PostMapping("/register")
	public String register(@RequestBody UserDto userDto) {
		// 기본 권한 설정 (테스트 편의상)
		if (userDto.getUserRole() == null)
			userDto.setUserRole("ROLE_USER");

		int result = userMapper.insertUser(userDto);
		return result > 0 ? "회원가입 성공! (번호: " + userDto.getUserNo() + ")" : "회원가입 실패";
	}

	// 3. 로그인 테스트 (세션 사용)
	@PostMapping("/login")
	public String login(@RequestBody UserDto loginRequest, HttpSession session) {
		UserDto user = userMapper.findUserById(loginRequest.getUserId());

		if (user != null && user.getUserPw().equals(loginRequest.getUserPw())) {
			session.setAttribute("loginUser", user); // 세션에 저장
			return user.getUserName() + "님 환영합니다. 권한: " + user.getUserRole();
		}
		return "아이디 또는 비밀번호가 틀립니다.";
	}

	// 4. 로그아웃
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "로그아웃 되었습니다.";
	}

	// 5. 관리자 전용: 전체 유저 목록 조회 (권한 제어 맛보기)
	@GetMapping("/list")
	public Object getAllUsers(HttpSession session) {
		UserDto loginUser = (UserDto) session.getAttribute("loginUser");

		// 권한 체크 로직
		if (loginUser == null || !"ROLE_ADMIN".equals(loginUser.getUserRole())) {
			return "권한이 없습니다. 관리자로 로그인하세요.";
		}

		return userMapper.findAllUsers();
	}

	// 6. 정보 수정
	@PutMapping("/update")
	public String updateUser(@RequestBody UserDto userDto, HttpSession session) {
		UserDto loginUser = (UserDto) session.getAttribute("loginUser");
		if (loginUser == null)
			return "로그인이 필요합니다.";

		int result = userMapper.updateUser(userDto);
		return result > 0 ? "정보 수정 완료" : "수정 실패";
	}

	// 7. 회원 탈퇴
	@DeleteMapping("/delete/{userId}")
	public String deleteUser(@PathVariable String userId) {
		int result = userMapper.deleteUser(userId);
		return result > 0 ? userId + " 삭제 완료" : "삭제 실패";
	}
}