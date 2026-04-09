package com.metanet.seoulbike.test.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSearchDto {
	private String searchType; 	// "loginId", "name"
	private String keyword;
	private String role; 		// "ROLE_USER", "ROLE_ADMIN"
	private String gender; 		// "M", "F"
	private int page = 1;
	private int size = 10;
}