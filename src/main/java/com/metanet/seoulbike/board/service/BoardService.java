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

	@Transactional(readOnly = true)
	public BoardDto getBoardById(Long boardId) {
		return boardMapper.selectBoardById(boardId);
	}

	@Transactional
	public void addViewCount(Long boardId) {
		boardMapper.updateViewCount(boardId);
	}

	@Transactional
	public void registerBoard(BoardDto dto) {
		boardMapper.insertBoard(dto);
	}

	@Transactional
	public void modifyBoard(BoardDto dto) {
		boardMapper.updateBoard(dto);
	}

	@Transactional
	public void removeBoard(Long boardId) {
		boardMapper.deleteBoard(boardId);
	}

	@Transactional
	public void addLikeCount(Long boardId) {
		boardMapper.updateLikeCount(boardId);
	}

	@Transactional(readOnly = true)
	public Map<String, Object> getCategoryCounts() {
		Map<String, Object> counts = boardMapper.selectCategoryCounts();
		if (counts == null)
			counts = new HashMap<>();

		counts.putIfAbsent("NOTICE_CNT", 0);
		counts.putIfAbsent("SUGGESTION_CNT", 0);
		return counts;
	}

	@Transactional
	public void registerComment(Long boardId, Long parentId, String content, String writer) {
		if (parentId != null) {
			CommentDto parentComment = boardMapper.selectCommentById(parentId);
			if (parentComment != null && parentComment.getParentId() != null) {
				throw new RuntimeException("답글의 답글(Depth 2)은 등록할 수 없습니다.");
			}
		}
		boardMapper.insertComment(boardId, parentId, content, writer);
	}

	@Transactional(readOnly = true)
	public List<CommentDto> getComments(Long boardId) {
		return boardMapper.selectCommentsByBoardId(boardId);
	}

	@Transactional
	public void modifyComment(Long commentId, String content) {
		boardMapper.updateComment(commentId, content);
	}

	@Transactional
	public void removeComment(Long commentId) {
		boardMapper.deleteComment(commentId);
	}
}