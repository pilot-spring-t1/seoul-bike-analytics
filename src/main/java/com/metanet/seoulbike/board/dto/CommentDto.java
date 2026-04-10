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
    private Long commentId;    // COMMENT_ID
    private Long boardId;      // BOARD_ID
    private Long parentId;     // PARENT_ID (null이면 원댓글, 값이 있으면 답글)
    private String content;    // CONTENT
    private String writer;     // WRITER
    
    // Oracle TIMESTAMP와 매핑될 Java 8 날짜 타입
    private LocalDateTime createdAt; // CREATED_AT
    
    // 필요 시 작성자 이름이나 프로필 사진 경로 등 추가 가능
}