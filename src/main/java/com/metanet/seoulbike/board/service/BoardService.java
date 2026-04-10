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

@Service
public class BoardService {

	@Autowired
	private BoardMapper boardMapper;

	/**
	 * 게시글 목록 조회 및 페이징 정보 계산
	 */
	@Transactional(readOnly = true)
	public Map<String, Object> getBoardList(BoardSearchDto dto) {
		// 페이징 처리를 위한 시작점(offset) 계산
		int offset = (dto.getPage() - 1) * dto.getSize();

		List<BoardDto> list = boardMapper.getBoardList(dto, offset);
		int total = boardMapper.getBoardCount(dto);

		// 전체 페이지 수 계산 (나머지가 있을 경우 올림)
		int totalPages = (int) Math.ceil((double) total / dto.getSize());

		Map<String, Object> result = new HashMap<>();
		result.put("list", list);
		result.put("total", total);
		result.put("totalPages", totalPages);

		return result;
	}

	/**
	 * 게시글 상세 조회 (조회수 자동 증가)
	 */
	@Transactional
	public BoardDto getBoardById(Long boardId) {
		// 상세 조회 시 조회수 1 증가
		boardMapper.updateViewCount(boardId);
		return boardMapper.getBoardById(boardId);
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
	 * 카테고리별 게시글 건수 집계 (NOTICE_CNT, SUGGESTION_CNT)
	 */
	@Transactional(readOnly = true)
	public Map<String, Object> getCategoryCounts() {
		Map<String, Object> counts = boardMapper.getCategoryCounts();

		// MyBatis에서 데이터가 없을 경우를 대비한 방어 코드 및 타입 안정성 강화
		if (counts == null) {
			counts = new HashMap<>();
		}

		// key값이 없을 경우 0으로 초기화하여 NullPointerException 방지
		counts.putIfAbsent("NOTICE_CNT", 0);
		counts.putIfAbsent("SUGGESTION_CNT", 0);

		return counts;
	}

	/**
	 * 댓글 및 답글 등록
	 * 
	 * @param parentId 원댓글의 ID (일반 댓글일 경우 null)
	 */
	@Transactional
	public void registerComment(Long boardId, Long parentId, String content, String writer) {
		// [비즈니스 로직] 답글의 깊이 제한 (답글에는 답글을 달 수 없음)
		if (parentId != null) {
			CommentDto parentComment = boardMapper.getCommentById(parentId);

			// 부모 댓글 자체가 이미 parentId를 가지고 있다면 depth가 2 이상인 것으로 판단
			if (parentComment != null && parentComment.getParentId() != null) {
				throw new RuntimeException("답글의 답글(Depth 2)은 등록할 수 없습니다.");
			}
		}

		boardMapper.insertComment(boardId, parentId, content, writer);
	}

	/**
	 * 특정 게시글의 전체 댓글 목록 조회 CommentDto를 사용하여 LocalDateTime 변환 이슈 해결
	 */
	@Transactional(readOnly = true)
	public List<CommentDto> getComments(Long boardId) {
		return boardMapper.getCommentsByBoardId(boardId);
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