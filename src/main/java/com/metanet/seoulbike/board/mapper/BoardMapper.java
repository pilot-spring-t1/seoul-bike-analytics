package com.metanet.seoulbike.board.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.metanet.seoulbike.board.dto.BoardDto;
import com.metanet.seoulbike.board.dto.BoardSearchDto;
import com.metanet.seoulbike.board.dto.CommentDto;

@Mapper
public interface BoardMapper {

	// 게시글 관련
	List<BoardDto> getBoardList(@Param("dto") BoardSearchDto dto, @Param("offset") int offset);

	int getBoardCount(@Param("dto") BoardSearchDto dto);

	BoardDto getBoardById(Long boardId);

	int insertBoard(BoardDto boardDto);

	int updateBoard(BoardDto boardDto);

	int deleteBoard(Long boardId);

	Map<String, Object> getCategoryCounts();

	int updateViewCount(Long boardId);

	int updateLikeCount(Long boardId);

	// 댓글 관련 (반환 타입 수정)
	int insertComment(@Param("boardId") Long boardId, @Param("parentId") Long parentId,
			@Param("content") String content, @Param("writer") String writer);

	List<CommentDto> getCommentsByBoardId(Long boardId); // Map -> CommentDto 변경

	CommentDto getCommentById(Long commentId); // Map -> CommentDto 변경
	
	int updateComment(@Param("commentId") Long commentId, @Param("content") String content);
	int deleteComment(Long commentId);
	
}