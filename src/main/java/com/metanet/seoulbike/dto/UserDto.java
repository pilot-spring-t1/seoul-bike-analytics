package com.metanet.seoulbike.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserDto {
	private int userNo; // 고유번호
	private String userId; // 아이디
	private String userPw; // 비밀번호 (테스트 후엔 보안상 제외 권장)
	private String userName; // 이름
	private String gender; // 성별 (M/F)
	private int age; // 나이 (int로 변경된 부분 반영)
	private String userRole; // 권한 (ROLE_USER, ROLE_ADMIN)
	private LocalDateTime joinDate; // 가입일
	private LocalDateTime lastLogin; // 접속기록
}