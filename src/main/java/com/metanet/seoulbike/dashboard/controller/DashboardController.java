package com.metanet.seoulbike.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.metanet.seoulbike.dashboard.dto.DashboardSummaryDto;
import com.metanet.seoulbike.dashboard.service.BikeAnalysisService;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
	
	@Autowired
	private BikeAnalysisService bikeAnalysisService;

    @GetMapping
    public String dashboardPage(Model model) {
        DashboardSummaryDto summary = bikeAnalysisService.getDashboardSummary();
        model.addAttribute("summary", summary);
        return "dashboard/user-dashboard";
    }

    @GetMapping("/summary")
    public String summaryAnalysisPage() {
        return "analysis/summary";
    }

    @GetMapping("/detail")
    public String detailAnalysisPage(Model model) {
        return "analysis/detail";
    }

    @GetMapping("/notifications")
    public String notificationPage(Model model) {
        return "notification/notifications";
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
    @GetMapping("/websocket-test")
    public String websocketTestPage() {
        return "notification/websocket-test";
    }
    
    @GetMapping("/memory-monitor")
    public String memoryMonitorPage() {
        return "notification/memory-monitor";
    }
}
