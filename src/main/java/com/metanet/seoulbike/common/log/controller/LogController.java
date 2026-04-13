package com.metanet.seoulbike.common.log.controller;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;

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
     * 로그 목록 조회 (getList)
     */
    @GetMapping("/list")
    public String getList(@ModelAttribute("searchDto") LogSearchDto searchDto, Authentication auth, Model model) {
        Map<String, Object> result = logService.getLogList(searchDto);
        model.addAllAttributes(result);
        
        // 일관된 userName 처리 (삼항 연산자로 깔끔하게)
        model.addAttribute("userName", (auth != null) ? auth.getName() : "Guest");
        
        return "logs/log-list";
    }

    /**
     * 로그 상세 조회 (getDetail)
     */
    @GetMapping("/view/{id}")
    public String getDetail(@PathVariable("id") Long logId, Authentication auth, Model model) {
        model.addAttribute("log", logService.getLogById(logId)); // getLogById -> getLog
        model.addAttribute("userName", (auth != null) ? auth.getName() : "Guest");

        return "logs/log-view";
    }
}