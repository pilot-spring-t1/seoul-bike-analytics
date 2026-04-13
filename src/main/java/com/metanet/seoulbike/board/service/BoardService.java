package com.metanet.seoulbike.board.service;

import com.metanet.seoulbike.board.dto.*;
import com.metanet.seoulbike.board.repository.IBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final IBoardRepository boardRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> getBoardList(BoardSearchDto dto) {
        int offset = (dto.getPage() - 1) * dto.getSize();
        
        List<BoardDto> list = boardRepository.findAll(dto, offset);
        int total = boardRepository.countAll(dto);
        int totalPages = (total > 0) ? (int) Math.ceil((double) total / dto.getSize()) : 1;

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("totalPages", totalPages);
        return result;
    }

    @Transactional(readOnly = true)
    public BoardDto getBoard(Long boardId) {
        return boardRepository.findById(boardId);
    }

    @Transactional
    public void createBoard(BoardDto dto) {
        boardRepository.save(dto);
    }

    @Transactional
    public void updateBoard(BoardDto dto) {
        boardRepository.update(dto);
    }

    @Transactional
    public void deleteBoard(Long boardId) {
        boardRepository.delete(boardId);
    }

    @Transactional
    public void increaseViewCount(Long boardId) {
        boardRepository.incrementViewCount(boardId);
    }

    @Transactional
    public boolean createLike(Long boardId, Long memberId) {
        if (boardRepository.checkLikeExists(boardId, memberId) > 0) return false;
        boardRepository.saveLike(boardId, memberId);
        boardRepository.updateLikeCount(boardId);
        return true;
    }

    @Transactional
    public void createComment(Long boardId, Long parentId, String content, String writer) {
        if (parentId != null) {
            CommentDto parent = boardRepository.findCommentById(parentId);
            if (parent != null && parent.getParentId() != null) {
                throw new RuntimeException("답글의 답글은 등록할 수 없습니다.");
            }
        }
        boardRepository.saveComment(boardId, parentId, content, writer);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentList(Long boardId) {
        return boardRepository.findCommentsByBoardId(boardId);
    }

    @Transactional
    public void updateComment(Long commentId, String content) {
        boardRepository.updateComment(commentId, content);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        boardRepository.deleteComment(commentId);
    }

    @Transactional(readOnly = true)
    public String getCommentWriter(Long commentId) {
        CommentDto comment = boardRepository.findCommentById(commentId);
        return (comment != null) ? comment.getWriter() : null;
    }
}