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
        model.addAttribute("userName", "관리자");
        return "dashboard/user-dashboard";
    }

    @GetMapping("/summary")
    public String summaryAnalysisPage() {
        return "analysis/summary";
    }

    @GetMapping("/detail")
    public String detailAnalysisPage() {
        return "analysis/detail";
    }
    
    @GetMapping("/data-center")
    public String dataSharePage() {
    	return "shared/data-center";
    }

    @GetMapping("/notifications")
    public String notificationPage() {
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
    @GetMapping("/monthly-usage")
    public String monthlyUsagePage() {
        return "monthly-usage";
    }
}
