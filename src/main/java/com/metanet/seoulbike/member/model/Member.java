package com.metanet.seoulbike.member.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Member {
	private Long memberId;
	private String loginId;
	private String password;
	private String name;
	private String gender;
	private Integer age;
	private String role;
	private LocalDate createdAt;
	private LocalDate lastLoginAt;
	
}
