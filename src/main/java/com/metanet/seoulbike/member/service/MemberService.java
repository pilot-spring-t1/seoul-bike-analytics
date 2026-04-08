package com.metanet.seoulbike.member.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.metanet.seoulbike.member.model.Member;
import com.metanet.seoulbike.member.repository.MemberRepository;

@Service
public class MemberService {
	
	@Autowired
	private MemberRepository memberRepository;
	
	public void insertMember(Member member) {
		memberRepository.insertMember(member);
	}
	
	public Member selectMember(String userId) {
		return memberRepository.selectMember(userId);
	}
	
	public List<Member> selectAllMembers() {
		return memberRepository.selectAllMembers();
	}
	
	public void updateMember(Member member) {
		memberRepository.updateMember(member);
	}
	
	public void deleteMember(Member member) {
		memberRepository.deleteMember(member);
	}
	
	
}
