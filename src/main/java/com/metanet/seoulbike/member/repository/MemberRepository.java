package com.metanet.seoulbike.member.repository;

import com.metanet.seoulbike.member.dto.MemberSearchDto;
import com.metanet.seoulbike.member.mapper.MemberMapper;
import com.metanet.seoulbike.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository implements IMemberRepository {

    private final MemberMapper memberMapper;

    @Override
    public Member findByLoginId(String loginId) {
        return memberMapper.selectMemberByLoginId(loginId);
    }

    @Override
    public Member findById(Long memberId) {
        return memberMapper.selectMemberByMemberId(memberId);
    }

    @Override
    public void save(Member member) {
        memberMapper.insertMember(member);
    }

    @Override
    public void update(Member member) {
        memberMapper.updateMember(member);
    }

    @Override
    public void delete(Long memberId) {
        memberMapper.deleteMember(memberId);
    }

    @Override
    public List<Member> findAllByPage(MemberSearchDto dto, int offset) {
        return memberMapper.selectAllMembersByPage(dto, offset);
    }

    @Override
    public int countBySearch(MemberSearchDto dto) {
        return memberMapper.selectMemberCountBySearch(dto);
    }

    @Override
    public List<Member> findAll() {
        // 기존 매퍼의 selectAllMembers() 호출
        return memberMapper.selectAllMembers();
    }
}