package com.metanet.seoulbike.board.repository;

import com.metanet.seoulbike.board.dto.*;
import com.metanet.seoulbike.board.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardRepository implements IBoardRepository {

    private final BoardMapper boardMapper; // 실제 MyBatis 매퍼

    @Override
    public List<BoardDto> findAll(BoardSearchDto dto, int offset) {
        return boardMapper.selectBoardList(dto, offset);
    }

    @Override
    public int countAll(BoardSearchDto dto) {
        return boardMapper.selectBoardCount(dto);
    }

    @Override
    public BoardDto findById(Long boardId) {
        return boardMapper.selectBoardById(boardId);
    }

    @Override
    public void save(BoardDto dto) {
        boardMapper.insertBoard(dto);
    }

    @Override
    public void update(BoardDto dto) {
        boardMapper.updateBoard(dto);
    }

    @Override
    public void delete(Long boardId) {
        boardMapper.deleteBoard(boardId);
    }

    @Override
    public void incrementViewCount(Long boardId) {
        boardMapper.updateViewCount(boardId);
    }

    @Override
    public int checkLikeExists(Long boardId, Long memberId) {
        return boardMapper.checkLike(boardId, memberId);
    }

    @Override
    public void saveLike(Long boardId, Long memberId) {
        boardMapper.insertLike(boardId, memberId);
    }

    @Override
    public void updateLikeCount(Long boardId) {
        boardMapper.updateLikeCount(boardId);
    }

    @Override
    public void saveComment(Long boardId, Long parentId, String content, String writer) {
        boardMapper.insertComment(boardId, parentId, content, writer);
    }

    @Override
    public List<CommentDto> findCommentsByBoardId(Long boardId) {
        return boardMapper.selectCommentsByBoardId(boardId);
    }

    @Override
    public CommentDto findCommentById(Long commentId) {
        return boardMapper.selectCommentById(commentId);
    }

    @Override
    public void updateComment(Long commentId, String content) {
        boardMapper.updateComment(commentId, content);
    }

    @Override
    public void deleteComment(Long commentId) {
        boardMapper.deleteComment(commentId);
    }
}