package com.metanet.seoulbike.common.log.controller;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
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
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/logs")
public class LogController {

	private final LogService logService;

	/**
	 * 시스템 로그 목록 조회 및 검색 URI
	 */
	@GetMapping("/list")
	public String list(@ModelAttribute("searchDto") LogSearchDto searchDto, Model model) {
	    // 서비스 호출 한 번으로 모든 계산된 데이터를 가져옴
	    Map<String, Object> result = logService.getLogList(searchDto);
	    
	    model.addAllAttributes(result);
	    return "logs/log-list";
	}

	/**
	 * 로그 상세 정보 보기 (parameterData 및 errorMsg)
	 */
	@GetMapping("/view/{id}")
	public String view(@PathVariable("id") Long logId, Model model) {
		// selectLogById 호출
		LogDto logDetail = logService.getLogById(logId);
		model.addAttribute("log", logDetail);

		return "logs/log-view"; // 로그 상세 페이지 (상세 팝업 등)
	}
}