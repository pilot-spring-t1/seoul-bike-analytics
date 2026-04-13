package com.metanet.seoulbike.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.metanet.seoulbike.dashboard.dto.DashboardSummaryDto;
import com.metanet.seoulbike.dashboard.service.BikeAnalysisService;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private BikeAnalysisService bikeAnalysisService;

	@GetMapping("/detail")
    public String AdminDetailAnalysisPage() {
        return "analysis/admin-detail";
    }

    @GetMapping("/dashboard")
    public String adminDashboardPage(Model model) {
    	DashboardSummaryDto summary = bikeAnalysisService.getDashboardSummary();
        model.addAttribute("summary", summary);
        model.addAttribute("userName", "관리자");
        return "dashboard/admin-dashboard";
    }

    @GetMapping("/data")
    public String dataManagePage() {
        return "admin/data-manage";
    }
    
    @GetMapping("/contents")
    public String adminContentsPage() {
    	return "admin-contents";
    }
    
    @GetMapping("/notifications")
    public String adminNotificationPage() {
        return "notification/admin-notifications";
    }
}
