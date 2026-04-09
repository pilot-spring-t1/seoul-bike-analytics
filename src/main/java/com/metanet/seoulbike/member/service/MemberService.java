package com.metanet.seoulbike.member.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.metanet.seoulbike.auth.JwtTokenProvider;
import com.metanet.seoulbike.member.dto.MemberSearchDto;
import com.metanet.seoulbike.member.mapper.MemberMapper;
import com.metanet.seoulbike.member.model.Member;

@Service
public class MemberService {
	
	@Autowired
	private MemberMapper memberMapper;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public void signUp(Member member) {
		if (memberMapper.selectMemberByLoginId(member.getLoginId()) != null) {
			throw new RuntimeException("이미 존재하는 아이디입니다");
		}
		String encodedPassword = passwordEncoder.encode(member.getPassword());
		member.setPassword(encodedPassword);
		member.setRole("ROLE_USER");
		memberMapper.insertMember(member);
	}
	
	public String login(Map<String, String> user) {
		Member member = memberMapper.selectMemberByLoginId(user.get("loginId"));
		
		if (member == null) {
			throw new RuntimeException("아이디 또는 비밀번호가 일치하지 않습니다");
		}
		
		if (!passwordEncoder.matches(user.get("password"), member.getPassword())) {
			throw new RuntimeException("아이디 또는 비밀번호가 일치하지 않습니다");
		}
		member.setLastLoginAt(LocalDate.now());
		memberMapper.updateMember(member);
		return jwtTokenProvider.generateToken(member);
	}
	
	public Member selectMember(String loginId) {
		return memberMapper.selectMemberByLoginId(loginId);
	}
	
	public Map<String, Object> selectAllMembersByPage(MemberSearchDto dto) {
		int offset = (dto.getPage() - 1) * dto.getSize();
	    
	    List<Member> list = memberMapper.selectAllMembersByPage(dto, offset);
	    int total = memberMapper.selectMemberCountBySearch(dto);

	    int totalPages = (int) Math.ceil((double) total / dto.getSize());

	    Map<String, Object> result = new HashMap<>();
	    result.put("list", list);        
	    result.put("total", total);      
	    result.put("totalPages", totalPages);
	    result.put("currentPage", dto.getPage());

	    return result;
	}
	
	public void updateMember(Member member) {
		memberMapper.updateMember(member);
	}
	
	public void deleteMember(Long memberId) {
		memberMapper.deleteMember(memberId);
	}
	
	
}
