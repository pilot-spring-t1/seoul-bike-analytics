package com.metanet.seoulbike.member.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.metanet.seoulbike.member.model.Member;

@Mapper
public interface MemberMapper {
	void insertMember(Member member);
	Member selectMember(String userId);
	List<Member> selectAllMembers();
	void updateMember(Member member);
	void deleteMember(Member member);
}
