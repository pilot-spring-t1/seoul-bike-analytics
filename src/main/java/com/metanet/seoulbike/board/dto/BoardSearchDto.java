package com.metanet.seoulbike.board.dto;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class BoardSearchDto {
    private String category;  // NOTICE, SUGGESTION 필터링
    private String keyword;   // 제목/작성자 검색어
    private int page = 1;     // 현재 페이지 번호 (기본값 1)
    private int size = 10;    // 한 페이지당 보여줄 게시글 수
}