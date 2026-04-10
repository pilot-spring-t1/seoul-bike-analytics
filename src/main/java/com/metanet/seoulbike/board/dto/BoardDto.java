package com.metanet.seoulbike.board.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BoardDto {
	private Long boardId;
	private String category; // NOTICE, SUGGESTION
	private String title;
	private String content;
	private String writer;
	private LocalDateTime createdAt;
	private int viewCount;
	private int likeCount; // 추천수 (기능제안 투표용) 추가

	// 첨부파일 처리를 위한 필드 (필요시)
	private List<Long> fileIds;
}