package com.metanet.seoulbike.test.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.metanet.seoulbike.test.dto.MemberDto;

@Mapper
public interface MemberMapper {
    List<MemberDto> findAllMembers();       // 전체 회원 조회 (이름 변경)
    MemberDto findMemberByLoginId(String loginId); // 아이디로 조회
    int insertMember(MemberDto memberDto);  // 회원 가입
    int updateMember(MemberDto memberDto);  // 정보 수정
    int deleteMember(String loginId);       // 회원 탈퇴
}