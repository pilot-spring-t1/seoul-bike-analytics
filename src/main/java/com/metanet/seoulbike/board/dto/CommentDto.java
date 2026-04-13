package com.metanet.seoulbike.board.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long commentId;
    private Long boardId;
    private Long parentId;
    private String content;
    private String writer;
    private LocalDateTime createdAt;
    
    // 추가: 계층의 깊이를 나타내는 필드 (0: 원댓글, 1: 답글, 2: 대대댓글...)
    private int depth; 
}