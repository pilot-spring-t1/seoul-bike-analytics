package com.metanet.seoulbike.member.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 회원가입
     */
    @Transactional
    public void signUp(Member member) {
        // Mapper 명칭: selectMemberByLoginId
        if (memberMapper.selectMemberByLoginId(member.getLoginId()) != null) {
            throw new RuntimeException("이미 존재하는 아이디입니다");
        }
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        member.setRole("ROLE_USER");
        memberMapper.insertMember(member);
    }

    /**
     * 로그인
     */
    @Transactional
    public String login(Map<String, String> user) {
        // Mapper 명칭: selectMemberByLoginId
        Member member = memberMapper.selectMemberByLoginId(user.get("loginId"));

        if (member == null || !passwordEncoder.matches(user.get("password"), member.getPassword())) {
            throw new RuntimeException("아이디 또는 비밀번호가 일치하지 않습니다");
        }
        
        member.setLastLoginAt(LocalDate.now());
        memberMapper.updateMember(member); 
        return jwtTokenProvider.generateToken(member);
    }

    /**
     * ID(PK)로 회원 상세 조회 (컨트롤러 editForm에서 호출)
     */
    public Member getMemberById(Long memberId) {
        // 만약 매퍼에 selectMemberByMemberId(Long id)가 없다면 추가해야 합니다.
        // 현재 매퍼 구조상 PK 조회가 없다면 에러가 날 수 있으므로 매퍼 인터페이스 확인 필요!
        return memberMapper.selectMemberByMemberId(memberId); 
    }

    /**
     * 회원 목록 조회 및 페이징 계산
     */
    public Map<String, Object> getAllMembersByPage(MemberSearchDto dto) {
        int offset = (dto.getPage() - 1) * dto.getSize();
        int blockLimit = 5;

        // Mapper 명칭: selectAllMembersByPage, selectMemberCountBySearch
        List<Member> list = memberMapper.selectAllMembersByPage(dto, offset);
        int total = memberMapper.selectMemberCountBySearch(dto);

        int totalPages = (total > 0) ? (int) Math.ceil((double) total / dto.getSize()) : 1;

        int blockNumber = (dto.getPage() - 1) / blockLimit;
        int startPage = (blockNumber * blockLimit) + 1;
        int endPage = Math.min((startPage + blockLimit - 1), totalPages);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("totalPages", totalPages);
        result.put("startPage", startPage);
        result.put("endPage", endPage);

        return result;
    }

    @Transactional
    public void updateMember(Member member) {
        // Mapper 명칭: updateMember
        memberMapper.updateMember(member);
    }

    @Transactional
    public void deleteMember(Long memberId) {
        // Mapper 명칭: deleteMember
        memberMapper.deleteMember(memberId);
    }
}