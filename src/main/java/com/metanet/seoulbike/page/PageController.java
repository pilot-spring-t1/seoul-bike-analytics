package com.metanet.seoulbike.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @GetMapping("/dashboard")
    public String userDashboardPage() {
        return "dashboard/user-dashboard";
    }

    @GetMapping("/analysis/summary")
    public String summaryAnalysisPage() {
        return "analysis/summary";
    }

    @GetMapping("/analysis/detail")
    public String detailAnalysisPage() {
        return "analysis/detail";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboardPage() {
        return "admin/admin-dashboard";
    }

    @GetMapping("/admin/data")
    public String dataManagePage() {
        return "admin/data-manage";
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
