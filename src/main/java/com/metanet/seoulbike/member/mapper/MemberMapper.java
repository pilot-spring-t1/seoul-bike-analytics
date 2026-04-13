package com.metanet.seoulbike.member.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.metanet.seoulbike.member.dto.MemberSearchDto;
import com.metanet.seoulbike.member.model.Member;

@Mapper
public interface MemberMapper {
	void insertMember(Member member);

	Member selectMemberByLoginId(String loginId);

	Member selectMemberByMemberId(Long memberId);
	
	List<Member> selectAllMembers();

	List<Member> selectAllMembersByPage(@Param("dto") MemberSearchDto dto, @Param("offset") int offset);

	int selectMemberCountBySearch(@Param("dto") MemberSearchDto dto);

	void updateMember(Member member);

	void deleteMember(Long memberId);
}