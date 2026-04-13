package com.metanet.seoulbike.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.metanet.seoulbike.dashboard.dto.DashboardSummaryDto;
import com.metanet.seoulbike.dashboard.service.BikeAnalysisService;
import com.metanet.seoulbike.member.model.Member;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
	
	@Autowired
	private BikeAnalysisService bikeAnalysisService;

    @GetMapping
    public String dashboardPage(Model model, Authentication auth) {
        DashboardSummaryDto summary = bikeAnalysisService.getDashboardSummary();
		/*
		 * model.addAttribute("memberId", 1L); // 테스트용 model.addAttribute("summary",
		 * summary); model.addAttribute("userName", "관리자");
		 * model.addAttribute("memberId", 1L); // 테스트용 if (auth != null) {
		 * model.addAttribute("userName", auth.getName()); } else {
		 * model.addAttribute("userName", "Guest"); }
		 */
        if (auth != null && auth.getPrincipal() instanceof Member) {
            Member member = (Member) auth.getPrincipal();
            model.addAttribute("userName", member.getLoginId());
            model.addAttribute("memberId", member.getMemberId());
        } else {
            model.addAttribute("userName", "Guest");
        }

        model.addAttribute("summary", summary);
        return "dashboard/user-dashboard";
    }

    @GetMapping("/summary")
    public String summaryAnalysisPage() {
        return "analysis/summary";
    }

    @GetMapping("/detail")
    public String detailAnalysisPage(Model model, Authentication auth) {
		/*
		 * model.addAttribute("memberId", 1L); // 테스트용 if (auth != null) {
		 * model.addAttribute("userName", auth.getName()); } else {
		 * model.addAttribute("userName", "Guest"); }
		 */
    	if (auth != null && auth.getPrincipal() instanceof Member) {
            Member member = (Member) auth.getPrincipal();
            model.addAttribute("userName", member.getLoginId());
            model.addAttribute("memberId", member.getMemberId());
        } else {
            model.addAttribute("userName", "Guest");
        }
    	
        return "analysis/detail";
    }

    @GetMapping("/notifications")
    public String notificationPage(Model model, Authentication auth) {

        if (auth != null && auth.getPrincipal() instanceof Member) {
            Member member = (Member) auth.getPrincipal();
            model.addAttribute("userName", member.getLoginId());
            model.addAttribute("memberId", member.getMemberId());
        } else {
            model.addAttribute("userName", "Guest");
        }

        return "notification/notifications";
    }
    
    @GetMapping("/admin/notifications")
    public String adminNotificationPage(Model model, Authentication auth) {
    	if (auth != null && auth.getPrincipal() instanceof Member) {
            Member member = (Member) auth.getPrincipal();
            model.addAttribute("userName", member.getLoginId());
            model.addAttribute("memberId", member.getMemberId());
        } else {
            model.addAttribute("userName", "Guest");
        }
        return "notification/admin-notifications";
    }

    @GetMapping("/error/403")
    public String error403Page() {
        return "error/403";
    }

    @GetMapping("/error/404")
    public String error404Page() {
        return "error/404";
    }
    
    // 사용자 dashboard
	/*
	 * @GetMapping("/websocket-test") public String websocketTestPage() { return
	 * "notification/websocket-test"; }
	 * 
	 * @GetMapping("/memory-monitor") public String memoryMonitorPage() { return
	 * "notification/memory-monitor"; }
	 * 
	 * @GetMapping("/monthly-usage") public String monthlyUsagePage() { return
	 * "monthly-usage"; }
	 */
}
