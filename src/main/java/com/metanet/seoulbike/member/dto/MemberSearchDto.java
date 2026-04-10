<<<<<<<< HEAD:src/main/java/com/metanet/seoulbike/admin/dto/MemberSearchDto.java
package com.metanet.seoulbike.admin.dto;
========
package com.metanet.seoulbike.member.dto;
>>>>>>>> b3bac3bef98646b7bed3d0af9aa80548d169b975:src/main/java/com/metanet/seoulbike/member/dto/MemberSearchDto.java

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