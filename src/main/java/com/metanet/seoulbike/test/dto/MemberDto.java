package com.metanet.seoulbike.test.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberDto {
    private Long memberId;
    private String loginId;
    private String password;
    private String name;   
    private String gender; 
    private int age;     
    private String role;     
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}