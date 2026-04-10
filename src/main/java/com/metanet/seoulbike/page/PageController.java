package com.metanet.seoulbike.page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.metanet.seoulbike.stats.dto.DashboardSummaryDto;
import com.metanet.seoulbike.stats.service.BikeAnalysisService;


@Controller
public class PageController {
	
	@Autowired
	private BikeAnalysisService bikeAnalysisService;

    @GetMapping("/dashboard")
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
    public String detailAnalysisPage(Model model) {
    	model.addAttribute("memberId", 1L); // 테스트용
        return "analysis/detail";
    }
    
    @GetMapping("/admin/detail")
    public String AdminDetailAnalysisPage(Model model) {
    	model.addAttribute("memberId", 1L); // 테스트용
        return "analysis/admin-detail";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboardPage(Model model) {
    	DashboardSummaryDto summary = bikeAnalysisService.getDashboardSummary();
        model.addAttribute("summary", summary);
        model.addAttribute("userName", "관리자");
        return "dashboard/admin-dashboard";
    }

    @GetMapping("/admin/data")
    public String dataManagePage() {
        return "admin/data-manage";
    }
    
    @GetMapping("/admin/contents")
    public String adminContentsPage() {
    	return "admin-contents";
    }
    
    @GetMapping("/data-center")
    public String dataSharePage() {
    	return "shared/data-center";
    }
    
    @GetMapping("admin/data-center")
    public String adminDataSharePage() {
    	return "shared/admin-data-center";
    }

    @GetMapping("/notifications")
    public String notificationPage(Model model) {
    	model.addAttribute("memberId", 1L); // 테스트용
        return "notification/notifications";
    }
    
    @GetMapping("/admin/notifications")
    public String adminNotificationPage() {
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
