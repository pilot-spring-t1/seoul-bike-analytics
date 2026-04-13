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

	/**
	 * 게시글 목록 조회 (페이징 처리)
	 */
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

	/**
	 * 게시글 상세 조회
	 */
	@Transactional(readOnly = true)
	public BoardDto getBoardById(Long boardId) {
		return boardMapper.selectBoardById(boardId);
	}

	/**
	 * 조회수 증가
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
	 * 추천 기능 (유저당 1회 제한 로직 통합)
	 * @return true: 추천 성공, false: 이미 추천함
	 */
	@Transactional
	public boolean addLike(Long boardId, Long memberId) {
		// 1. 해당 유저가 이 게시글을 이미 추천했는지 확인 (board_likes 테이블 조회)
		if (boardMapper.checkLike(boardId, memberId) > 0) {
			return false; // 이미 추천 기록이 있음
		}

		// 2. 추천 이력 삽입
		boardMapper.insertLike(boardId, memberId);

		// 3. 게시글 테이블의 총 추천 수(LIKE_COUNT) 증가
		boardMapper.updateLikeCount(boardId);

		return true;
	}

	/**
	 * 카테고리별 게시글 수 조회
	 */
	@Transactional(readOnly = true)
	public Map<String, Object> getCategoryCounts() {
		Map<String, Object> counts = boardMapper.selectCategoryCounts();
		if (counts == null)
			counts = new HashMap<>();

		counts.putIfAbsent("NOTICE_CNT", 0);
		counts.putIfAbsent("SUGGESTION_CNT", 0);
		return counts;
	}

	/**
	 * 댓글 및 답글 등록 (1단계 답글만 허용)
	 */
	@Transactional
	public void registerComment(Long boardId, Long parentId, String content, String writer) {
		if (parentId != null) {
			CommentDto parentComment = boardMapper.selectCommentById(parentId);
			// 부모 댓글이 이미 답글(parentId가 존재)인 경우 에러 처리
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
		return boardMapper.selectCommentsByBoardId(boardId);
	}

	/**
	 * 댓글 수정
	 */
	@Transactional
	public void modifyComment(Long commentId, String content) {
		boardMapper.updateComment(commentId, content);
	}

	/**
	 * 댓글 작성자 조회 (권한 체크용)
	 */
	@Transactional(readOnly = true)
	public String getCommentWriter(Long commentId) {
		CommentDto comment = boardMapper.selectCommentById(commentId);
		return (comment != null) ? comment.getWriter() : null;
	}

	/**
	 * 댓글 삭제
	 */
	@Transactional
	public void removeComment(Long commentId) {
		boardMapper.deleteComment(commentId);
	}
}