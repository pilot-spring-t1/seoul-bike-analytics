package com.metanet.seoulbike.member.repository;

import java.util.List;

import com.metanet.seoulbike.member.model.Member;

public interface MemberRepository {
	void insertMember(Member member);
	Member selectMember(String userId);
	List<Member> selectAllMembers();
	void updateMember(Member member);
	void deleteMember(Member member);
}
