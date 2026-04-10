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

		// 1. 처음 진입 시 페이지 번호가 없으면 1페이지로 고정
		if (searchDto.getPage() < 1) {
			searchDto.setPage(1);
		}

		// 2. 서비스 호출하여 로그 데이터 및 페이징 정보 수집
		// 내부적으로 selectLogList와 selectLogCount가 실행됨
		Map<String, Object> result = logService.selectLogList(searchDto);

		// 3. 뷰(Thymeleaf)로 데이터 전달
		model.addAttribute("list", result.get("list"));
		model.addAttribute("total", result.get("total"));
		model.addAttribute("totalPages", result.get("totalPages"));

		// @ModelAttribute("searchDto") 덕분에 searchDto는 자동으로 모델에 담김
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