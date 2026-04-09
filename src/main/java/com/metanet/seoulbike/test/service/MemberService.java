package com.metanet.seoulbike.test.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.metanet.seoulbike.test.dto.MemberDto;
import com.metanet.seoulbike.test.dto.MemberSearchDto;
import com.metanet.seoulbike.test.mapper.MemberMapper;

@Service("testMemberService")
public class MemberService {
	@Autowired
	private MemberMapper memberMapper;

	public Map<String, Object> getMemberListByPage(MemberSearchDto dto) {
		int offset = (dto.getPage() - 1) * dto.getSize();
		List<MemberDto> list = memberMapper.getMemberListByPage(dto, offset);
		int total = memberMapper.getMemberCountBySearch(dto);

		Map<String, Object> result = new HashMap<>();
		result.put("list", list);
		result.put("total", total);
		return result;
	}

	public MemberDto getMemberById(Long memberId) {
		return memberMapper.getMemberById(memberId);
	}

	public void updateMember(MemberDto dto) {
		memberMapper.updateMember(dto);
	}

	public void deleteMember(Long memberId) {
		memberMapper.deleteMemberById(memberId);
	}
}