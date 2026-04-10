package com.metanet.seoulbike.common.log.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.metanet.seoulbike.common.log.dto.LogDto;
import com.metanet.seoulbike.common.log.dto.LogSearchDto;
import com.metanet.seoulbike.common.log.service.LogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/logs")
public class LogController {

	private final LogService logService;

	/**
	 * 시스템 로그 목록 조회 및 검색 URI: /admin/logs/list
	 */
	@GetMapping("/list")
	public String list(@ModelAttribute("searchDto") LogSearchDto searchDto, Model model) {
	    Map<String, Object> result = logService.selectLogList(searchDto);
	    
	    int totalPages = (int) result.get("totalPages");
	    int currentPage = searchDto.getPage();
	    
	    // 한 번에 보여줄 페이지 번호 개수 (예: 1~5, 6~10)
	    int blockLimit = 5; 
	    int startPage = (((int)(Math.ceil((double)currentPage / blockLimit))) - 1) * blockLimit + 1;
	    int endPage = Math.min((startPage + blockLimit - 1), totalPages);

	    model.addAllAttributes(result);
	    model.addAttribute("startPage", startPage);
	    model.addAttribute("endPage", endPage);
	    model.addAttribute("memberId", 1L); // 테스트용
	    
	    return "admin/log-list";
	}

	/**
	 * 로그 상세 정보 보기 (parameterData 및 errorMsg 확인용) URI: /admin/logs/view/{id}
	 */
	@GetMapping("/view/{id}")
	public String view(@PathVariable("id") Long logId, Model model) {
		// selectLogById 호출
		LogDto logDetail = logService.selectLogById(logId);
		model.addAttribute("log", logDetail);

		return "admin/log-view"; // 로그 상세 페이지 (상세 팝업 등)
	}
}