package com.metanet.seoulbike.board.repository;

import com.metanet.seoulbike.board.dto.*;
import java.util.List;

public interface IBoardRepository {
    // 게시글 관련 데이터 처리
    List<BoardDto> findAll(BoardSearchDto dto, int offset);
    int countAll(BoardSearchDto dto);
    BoardDto findById(Long boardId);
    void save(BoardDto dto);
    void update(BoardDto dto);
    void delete(Long boardId);
    void incrementViewCount(Long boardId);

    // 추천 관련 데이터 처리
    int checkLikeExists(Long boardId, Long memberId);
    void saveLike(Long boardId, Long memberId);
    void updateLikeCount(Long boardId);

    // 댓글 관련 데이터 처리
    void saveComment(Long boardId, Long parentId, String content, String writer);
    List<CommentDto> findCommentsByBoardId(Long boardId);
    CommentDto findCommentById(Long commentId);
    void updateComment(Long commentId, String content);
    void deleteComment(Long commentId);
}