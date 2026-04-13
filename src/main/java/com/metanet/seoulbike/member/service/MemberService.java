package com.metanet.seoulbike.member.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.metanet.seoulbike.auth.JwtTokenProvider;
import com.metanet.seoulbike.member.dto.MemberSearchDto;
import com.metanet.seoulbike.member.model.Member;
import com.metanet.seoulbike.member.repository.IMemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // @Autowired 대신 생성자 주입 사용
public class MemberService {

    private final IMemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     */
    @Transactional
    public void signUp(Member member) {
        if (memberRepository.findByLoginId(member.getLoginId()) != null) {
            throw new RuntimeException("이미 존재하는 아이디입니다");
        }
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        member.setRole("ROLE_USER");
        memberRepository.save(member);
    }

    /**
     * 로그인
     */
    @Transactional
    public String login(Map<String, String> user) {
        Member member = memberRepository.findByLoginId(user.get("loginId"));

        if (member == null || !passwordEncoder.matches(user.get("password"), member.getPassword())) {
            throw new RuntimeException("아이디 또는 비밀번호가 일치하지 않습니다");
        }
        
        member.setLastLoginAt(LocalDate.now());
        memberRepository.update(member); 
        return jwtTokenProvider.generateToken(member);
    }

    /**
     * 회원 상세 조회
     */
    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId);
    }

    /**
     * 회원 목록 조회 및 페이징 계산
     */
    public Map<String, Object> getAllMembersByPage(MemberSearchDto dto) {
        int offset = (dto.getPage() - 1) * dto.getSize();
        int blockLimit = 5;

        List<Member> list = memberRepository.findAllByPage(dto, offset);
        int total = memberRepository.countBySearch(dto);

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
        memberRepository.update(member);
    }

    @Transactional
    public void deleteMember(Long memberId) {
        memberRepository.delete(memberId);
    }
}