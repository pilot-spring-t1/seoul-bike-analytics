package com.metanet.seoulbike.board.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.metanet.seoulbike.board.dto.BoardDto;
import com.metanet.seoulbike.board.dto.BoardSearchDto;
import com.metanet.seoulbike.board.dto.CommentDto;
import com.metanet.seoulbike.board.mapper.BoardMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardMapper boardMapper;

    // 1. 목록 조회 (Read)
    @Transactional(readOnly = true)
    public Map<String, Object> getBoardList(BoardSearchDto dto) {
        int offset = (dto.getPage() - 1) * dto.getSize();
        List<BoardDto> list = boardMapper.selectBoardList(dto, offset);
        int total = boardMapper.selectBoardCount(dto);
        int totalPages = (total > 0) ? (int) Math.ceil((double) total / dto.getSize()) : 1;

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("totalPages", totalPages);
        return result;
    }

    // 2. 단건 조회 (Read)
    @Transactional(readOnly = true)
    public BoardDto getBoard(Long boardId) {
        return boardMapper.selectBoardById(boardId);
    }

    // 3. 등록 (Create)
    @Transactional
    public void createBoard(BoardDto dto) {
        boardMapper.insertBoard(dto);
    }

    // 4. 수정 (Update)
    @Transactional
    public void updateBoard(BoardDto dto) {
        boardMapper.updateBoard(dto);
    }

    // 5. 삭제 (Delete)
    @Transactional
    public void deleteBoard(Long boardId) {
        boardMapper.deleteBoard(boardId);
    }

    // 6. 조회수 증가 (Action)
    @Transactional
    public void increaseViewCount(Long boardId) {
        boardMapper.updateViewCount(boardId);
    }

    // 7. 추천 (Create Like)
    @Transactional
    public boolean createLike(Long boardId, Long memberId) {
        if (boardMapper.checkLike(boardId, memberId) > 0) return false;
        boardMapper.insertLike(boardId, memberId);
        boardMapper.updateLikeCount(boardId);
        return true;
    }

    // --- 댓글 CRUD ---

    @Transactional
    public void createComment(Long boardId, Long parentId, String content, String writer) {
        if (parentId != null) {
            CommentDto parent = boardMapper.selectCommentById(parentId);
            if (parent != null && parent.getParentId() != null) {
                throw new RuntimeException("답글의 답글은 등록할 수 없습니다.");
            }
        }
        boardMapper.insertComment(boardId, parentId, content, writer);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentList(Long boardId) {
        return boardMapper.selectCommentsByBoardId(boardId);
    }

    @Transactional
    public void updateComment(Long commentId, String content) {
        boardMapper.updateComment(commentId, content);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        boardMapper.deleteComment(commentId);
    }

    @Transactional(readOnly = true)
    public String getCommentWriter(Long commentId) {
        CommentDto comment = boardMapper.selectCommentById(commentId);
        return (comment != null) ? comment.getWriter() : null;
    }
}