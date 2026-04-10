package com.metanet.seoulbike.admin.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.metanet.seoulbike.member.dto.MemberSearchDto;
import com.metanet.seoulbike.member.service.MemberService;

@Controller
@RequestMapping("/admin/members")
public class AdminMemberController {

	@Autowired
	private MemberService memberService;

	@GetMapping("/search")
    public String searchMembers(@ModelAttribute("searchDto") MemberSearchDto searchDto, Model model) {
        
        // 서비스 호출
        Map<String, Object> result = memberService.getMemberListByPage(searchDto);

        int total = (int) result.get("total");
        int totalPages = (int) Math.ceil((double) total / searchDto.getSize());

        model.addAttribute("list", result.get("list"));
        model.addAttribute("total", total);
        model.addAttribute("totalPages", totalPages);

        return "admin-users";
    }
	
	@PostMapping("/search")
    public String processSearch(@ModelAttribute MemberSearchDto searchDto, RedirectAttributes rttr) {
        // 검색 조건을 RedirectAttributes에 담아서 GET으로 리다이렉트
        // 이렇게 하면 주소창에 파라미터가 남지 않고 내부적으로 전달 가능
        rttr.addFlashAttribute("searchDto", searchDto);
        return "redirect:/admin/members/search";
    }

	// 수정 페이지 이동
	@GetMapping("/edit/{memberId}")
	public String editMemberForm(@PathVariable("memberId") Long memberId, Model model) {
		MemberDto member = memberService.getMemberById(memberId);
		model.addAttribute("member", member);
		return "admin-member-edit";
	}

	// 실제 수정 처리
	@PostMapping("/update")
	public String updateMember(@ModelAttribute MemberDto memberDto) {
		memberService.updateMember(memberDto);
		// 수정 후 목록 페이지로 돌아가기 (Redirect)
		return "redirect:/admin/members/search";
	}

	@GetMapping("/delete/{memberId}")
	public String deleteMember(@PathVariable("memberId") Long memberId) {
	    memberService.deleteMember(memberId);
	    
	    return "redirect:/admin/members/search";
	}
}