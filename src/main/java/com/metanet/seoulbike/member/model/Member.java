package com.metanet.seoulbike.member.model;

import java.sql.Date;
import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Member {
	private String userId;
	private String userPw;
	private String gender;
	private Integer age;
	private String userRole;
	private Date joinDate;
	private Timestamp lastLogin;
	
}
