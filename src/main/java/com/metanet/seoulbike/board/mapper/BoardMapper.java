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
	List<BoardDto> selectBoardList(@Param("dto") BoardSearchDto dto, @Param("offset") int offset);

	int selectBoardCount(@Param("dto") BoardSearchDto dto);

	BoardDto selectBoardById(Long boardId);

	int insertBoard(BoardDto boardDto);

	int updateBoard(BoardDto boardDto);

	int deleteBoard(Long boardId);

	Map<String, Object> selectCategoryCounts();

	int updateViewCount(Long boardId);

	int updateLikeCount(Long boardId);

	// 댓글 관련
	int insertComment(@Param("boardId") Long boardId, @Param("parentId") Long parentId,
			@Param("content") String content, @Param("writer") String writer);

	List<CommentDto> selectCommentsByBoardId(Long boardId);

	CommentDto selectCommentById(Long commentId);

	int updateComment(@Param("commentId") Long commentId, @Param("content") String content);

	int deleteComment(Long commentId);
}