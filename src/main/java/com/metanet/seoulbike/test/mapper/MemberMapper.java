package com.metanet.seoulbike.test.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.metanet.seoulbike.admin.dto.MemberSearchDto;
import com.metanet.seoulbike.test.dto.MemberDto;

@Mapper
public interface MemberMapper {
    List<MemberDto> getMemberListByPage(@Param("dto") MemberSearchDto dto, @Param("offset") int offset);
    int getMemberCountBySearch(@Param("dto") MemberSearchDto dto);
    
    MemberDto getMemberById(Long memberId);
    MemberDto getMemberByLoginId(String loginId);

    int insertMember(MemberDto memberDto);
    int updateMember(MemberDto memberDto);
    int deleteMemberById(Long memberId);
    int deleteMemberByLoginId(String loginId);
}