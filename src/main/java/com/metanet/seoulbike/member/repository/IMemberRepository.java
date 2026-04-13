package com.metanet.seoulbike.member.repository;

import com.metanet.seoulbike.member.dto.MemberSearchDto;
import com.metanet.seoulbike.member.model.Member;
import java.util.List;

public interface IMemberRepository {
    // 로그인 ID로 회원 찾기
    Member findByLoginId(String loginId);
    
    // PK(ID)로 회원 찾기
    Member findById(Long memberId);
    
    List<Member> findAll();
    
    // 회원 저장
    void save(Member member);
    
    // 회원 정보 수정
    void update(Member member);
    
    // 회원 삭제
    void delete(Long memberId);
    
    // 페이징 목록 조회
    List<Member> findAllByPage(MemberSearchDto dto, int offset);
    
    // 검색 조건에 따른 전체 회원 수
    int countBySearch(MemberSearchDto dto);
}