package com.metanet.seoulbike.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.metanet.seoulbike.dto.NotificationDto;
import com.metanet.seoulbike.mapper.NotificationMapper;

@RestController
@RequestMapping("/test/noti")
public class NotificationTestController {

	@Autowired
	private NotificationMapper notiMapper;

	// 1. 알림 발송 테스트 (POST)
	@PostMapping("/send")
	public String sendNoti(@RequestBody NotificationDto notiDto) {
		int result = notiMapper.insertNotification(notiDto);
		return result > 0 ? "알림 발송 성공" : "발송 실패";
	}

	// 2. 내 알림 확인 (GET)
	@GetMapping("/list/{userNo}")
	public List<NotificationDto> getMyNoti(@PathVariable int userNo) {
		return notiMapper.findByUserNo(userNo);
	}

	// 3. 알림 읽음 처리 (PATCH/POST)
	@PostMapping("/read/{notiId}")
	public String readNoti(@PathVariable int notiId) {
		int result = notiMapper.markAsRead(notiId);
		return result > 0 ? "읽음 처리 완료" : "처리 실패";
	}
}