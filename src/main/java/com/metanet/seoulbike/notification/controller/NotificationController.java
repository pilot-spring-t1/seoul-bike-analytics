package com.metanet.seoulbike.notification.controller;

// 이 import 구문들이 정확히 들어가 있는지 확인하세요!
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;

@Controller
public class NotificationController {

    @GetMapping("/notifications")
    public String notificationPage(Authentication auth, Model model) {
        if (auth != null) {
            model.addAttribute("userName", auth.getName());
        } else {
            model.addAttribute("userName", "Guest");
        }
        
        return "notification/notifications";
    }
}