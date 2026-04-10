package com.metanet.seoulbike.board.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.metanet.seoulbike.board.dto.BoardDto;
import com.metanet.seoulbike.board.dto.BoardSearchDto;
import com.metanet.seoulbike.board.dto.CommentDto;
import com.metanet.seoulbike.board.mapper.BoardMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // 생성자 주입을 위한 Lombok 어노테이션
public class BoardService {

    private final BoardMapper boardMapper;

    /**
     * 게시글 목록 조회 및 페이징 정보 계산
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getBoardList(BoardSearchDto dto) {
        // 페이징 처리를 위한 시작점(offset) 계산
        int offset = (dto.getPage() - 1) * dto.getSize();

        List<BoardDto> list = boardMapper.getBoardList(dto, offset);
        int total = boardMapper.getBoardCount(dto);

        // 전체 페이지 수 계산
        int totalPages = (total > 0) ? (int) Math.ceil((double) total / dto.getSize()) : 1;

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("totalPages", totalPages);

        return result;
    }

    /**
     * 게시글 상세 조회
     */
    @Transactional(readOnly = true)
    public BoardDto getBoardById(Long boardId) {
        return boardMapper.getBoardById(boardId);
    }

    /**
     * 조회수 증가 (컨트롤러에서 명시적으로 호출 가능하도록 분리)
     */
    @Transactional
    public void addViewCount(Long boardId) {
        boardMapper.updateViewCount(boardId);
    }

    /**
     * 게시글 등록
     */
    @Transactional
    public void registerBoard(BoardDto dto) {
        boardMapper.insertBoard(dto);
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public void modifyBoard(BoardDto dto) {
        boardMapper.updateBoard(dto);
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public void removeBoard(Long boardId) {
        boardMapper.deleteBoard(boardId);
    }

    /**
     * 추천수(좋아요) 증가
     */
    @Transactional
    public void addLikeCount(Long boardId) {
        boardMapper.updateLikeCount(boardId);
    }

    /**
     * 카테고리별 게시글 건수 집계
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getCategoryCounts() {
        Map<String, Object> counts = boardMapper.getCategoryCounts();
        if (counts == null) counts = new HashMap<>();

        counts.putIfAbsent("NOTICE_CNT", 0);
        counts.putIfAbsent("SUGGESTION_CNT", 0);
        return counts;
    }

    /**
     * 댓글 및 답글 등록
     */
    @Transactional
    public void registerComment(Long boardId, Long parentId, String content, String writer) {
        if (parentId != null) {
            CommentDto parentComment = boardMapper.getCommentById(parentId);
            if (parentComment != null && parentComment.getParentId() != null) {
                throw new RuntimeException("답글의 답글(Depth 2)은 등록할 수 없습니다.");
            }
        }
        boardMapper.insertComment(boardId, parentId, content, writer);
    }

    /**
     * 댓글 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CommentDto> getComments(Long boardId) {
        return boardMapper.getCommentsByBoardId(boardId);
    }
    
    /**
     * 댓글 수정
     */
    @Transactional
    public void modifyComment(Long commentId, String content) {
        boardMapper.updateComment(commentId, content);
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public void removeComment(Long commentId) {
        boardMapper.deleteComment(commentId);
    }
}