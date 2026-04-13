package com.metanet.seoulbike.notification.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.metanet.seoulbike.member.model.Member;

import lombok.extern.slf4j.Slf4j; // 로그를 위해 추가

@Slf4j // 로그를 위해 추가
@Controller
public class NotificationController {

    /**
     * 사용자 알림 센터 페이지
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/notifications")
    public String notificationPage(@AuthenticationPrincipal Member member, Model model) {
        
        // 페이지 진입 시 세션 정보 확인 로그
        if (member == null) {
            log.error("=> [VIEW GET] /notifications 진입 실패: 인증 정보(Principal)가 null입니다.");
        } else {
            log.info("=> [VIEW GET] /notifications 진입 - 사용자: {}, MemberId: {}", 
                     member.getName(), member.getMemberId());
        }

        model.addAttribute("userName", member != null ? member.getName() : "Unknown");
        model.addAttribute("memberId", member != null ? member.getMemberId() : null); 
        
        return "notification/notifications";
    }

    /**
     * 관리자 전용 알림 발송 폼 페이지
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/notifications/form")
    public String notificationForm(@AuthenticationPrincipal Member member, Model model) {
        
        log.info("=> [VIEW GET] /notifications/form 진입 - 관리자: {}", 
                 (member != null ? member.getName() : "Unknown"));
        
        model.addAttribute("userName", member != null ? member.getName() : "Admin");
        return "notification/notification-form";
    }
}