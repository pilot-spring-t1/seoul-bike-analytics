package com.metanet.seoulbike.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.metanet.seoulbike.dto.UserDto;
import com.metanet.seoulbike.mapper.UserMapper;

@RestController
@RequestMapping("/test")
public class DbTestController {

	@Autowired
	private UserMapper userMapper;

	// 전체 조회
	@GetMapping("/users")
	public List<UserDto> getUsers() {
		return userMapper.findAllUsers();
	}

	// 회원가입
	@PostMapping("/insert")
	public String insertUser(@RequestBody UserDto userDto) {
		int result = userMapper.insertUser(userDto);
		return result > 0 ? "회원가입 성공! 번호: " + userDto.getUserNo() : "실패";
	}

	// 정보 수정
	@PostMapping("/update")
	public String updateUser(@RequestBody UserDto userDto) {
		int result = userMapper.updateUser(userDto);
		return result > 0 ? userDto.getUserId() + "님 수정 완료" : "실패";
	}
}