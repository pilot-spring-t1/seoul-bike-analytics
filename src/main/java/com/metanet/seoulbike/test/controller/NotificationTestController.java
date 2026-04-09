package com.metanet.seoulbike.test.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.metanet.seoulbike.test.dto.NotificationDto;
import com.metanet.seoulbike.test.mapper.NotificationMapper;

@RestController
@RequestMapping("/test/noti")
public class NotificationTestController {

	@Autowired
	private NotificationMapper notiMapper;

	// 1. 알림 발송 테스트 (POST)
	@PostMapping("/send")
	public String sendNoti(@RequestBody NotificationDto notiDto) {
		// DB 컬럼이 MEMBER_ID로 바뀌었으므로, JSON 요청 시에도 memberId를 담아 보내야 합니다.
		int result = notiMapper.insertNotification(notiDto);
		return result > 0 ? "알림 발송 성공" : "발송 실패";
	}

	// 2. 특정 회원 알림 확인 (GET)
	// 경로 변수를 userNo에서 memberId로 변경
	@GetMapping("/list/{memberId}")
	public List<NotificationDto> getMyNoti(@PathVariable Long memberId) {
		return notiMapper.findByMemberId(memberId);
	}

	// 3. 알림 읽음 처리 (POST)
	@PostMapping("/read/{notiId}")
	public String readNoti(@PathVariable Long notiId) {
		int result = notiMapper.markAsRead(notiId);
		return result > 0 ? "읽음 처리 완료" : "처리 실패";
	}
}