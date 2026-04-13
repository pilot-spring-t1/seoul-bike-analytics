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
	private int likeCount;

	// 첨부파일 처리를 위한 필드
	private List<Long> fileIds;
}