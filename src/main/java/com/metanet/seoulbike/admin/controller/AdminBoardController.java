package com.metanet.seoulbike.admin.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.metanet.seoulbike.board.dto.BoardDto;
import com.metanet.seoulbike.board.dto.BoardSearchDto;
import com.metanet.seoulbike.board.service.BoardService;
import com.metanet.seoulbike.file.mapper.FileMapper;
import com.metanet.seoulbike.file.service.FileService;

@Controller
@RequestMapping("/admin/boards")
public class AdminBoardController {

	@Autowired
	private BoardService boardService;
	@Autowired
	private FileService fileService;
	@Autowired
	private FileMapper fileMapper;

	/**
	 * 1. 게시글 목록
	 */
	@GetMapping("/list")
	public String list(@ModelAttribute("searchDto") BoardSearchDto searchDto, Model model) {
		Map<String, Object> result = boardService.getBoardList(searchDto);
		model.addAttribute("list", result.get("list"));
		model.addAttribute("totalPages", result.get("totalPages"));
		model.addAttribute("summary", boardService.getCategoryCounts());
		return "admin-board";
	}

	/**
	 * 2. 검색 처리 (POST to GET Redirect)
	 */
	@PostMapping("/list")
	public String processSearch(@ModelAttribute BoardSearchDto searchDto, RedirectAttributes rttr) {
		rttr.addFlashAttribute("searchDto", searchDto);
		return "redirect:/admin/boards/list";
	}

	/**
	 * 3. 상세 페이지 (게시글 + 파일 + 댓글)
	 */
	@GetMapping("/view/{id}")
	public String view(@PathVariable("id") Long id, Model model) {
		model.addAttribute("board", boardService.getBoardById(id));
		model.addAttribute("files", fileMapper.getFilesByBoardId(id));
		model.addAttribute("comments", boardService.getComments(id));
		return "admin-board-view";
	}

	/**
	 * 4. 게시글 등록 폼
	 */
	@GetMapping("/write")
	public String writeForm(Model model) {
		model.addAttribute("board", new BoardDto());
		return "admin-board-write";
	}

	/**
	 * 5. 게시글 등록 실행
	 */
	@PostMapping("/register")
	public String register(@ModelAttribute BoardDto boardDto,
			@RequestParam(value = "uploadFiles", required = false) List<MultipartFile> files) throws IOException {
		boardDto.setWriter("ADMIN");
		boardService.registerBoard(boardDto);
		if (files != null) {
			for (MultipartFile file : files) {
				if (!file.isEmpty()) {
					fileService.uploadFile(file, boardDto.getWriter(), boardDto.getBoardId());
				}
			}
		}
		return "redirect:/admin/boards/list";
	}

	/**
	 * 6. 게시글 수정 폼 (추가 필요) 수정 버튼 클릭 시 기존 내용을 채운 폼으로 이동
	 */
	@GetMapping("/edit/{id}")
	public String editForm(@PathVariable("id") Long id, Model model) {
		model.addAttribute("board", boardService.getBoardById(id));
		return "admin-board-write"; // 등록 폼과 수정 폼을 같이 쓰는 경우
	}

	/**
	 * 7. 게시글 수정 실행 (추가 필요)
	 */
	@PostMapping("/modify")
	public String modify(@ModelAttribute BoardDto boardDto,
			@RequestParam(value = "uploadFiles", required = false) List<MultipartFile> files) throws IOException {

		// 게시글 정보 업데이트
		boardService.modifyBoard(boardDto);

		// 새 파일이 업로드된 경우 처리
		if (files != null) {
			for (MultipartFile file : files) {
				if (!file.isEmpty()) {
					fileService.uploadFile(file, boardDto.getWriter(), boardDto.getBoardId());
				}
			}
		}
		return "redirect:/admin/boards/view/" + boardDto.getBoardId();
	}

	/**
	 * 6. 게시글 삭제 실행
	 */
	@PostMapping("/delete/{id}")
	public String delete(@PathVariable("id") Long id) {
		boardService.removeBoard(id);
		return "redirect:/admin/boards/list";
	}

	/**
	 * 7. 게시글 추천 API
	 */
	@PostMapping("/like/{id}")
	public String like(@PathVariable Long id) {
		boardService.addLikeCount(id);
		return "redirect:/admin/boards/view/" + id;
	}

	// ==========================================
	// 여기서부터 댓글/답글 관련 API
	// ==========================================

	/**
	 * 8. 댓글/답글 등록
	 */
	@PostMapping("/comment/register")
	public String registerComment(@RequestParam Long boardId, @RequestParam(required = false) Long parentId,
			@RequestParam String content, RedirectAttributes rttr) {
		try {
			boardService.registerComment(boardId, parentId, content, "ADMIN");
		} catch (RuntimeException e) {
			rttr.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/admin/boards/view/" + boardId;
	}

	/**
	 * 9. 댓글 수정 실행 (추가된 기능)
	 */
	@PostMapping("/comment/modify")
	public String modifyComment(@RequestParam Long commentId, @RequestParam Long boardId,
			@RequestParam String content) {
		// BoardService에 modifyComment(Long commentId, String content) 메서드가 구현되어 있어야 함
		boardService.modifyComment(commentId, content);
		return "redirect:/admin/boards/view/" + boardId;
	}

	/**
	 * 10. 댓글 삭제 실행 (추가된 기능)
	 */
	@PostMapping("/comment/delete")
	public String deleteComment(@RequestParam Long commentId, @RequestParam Long boardId) {
		// BoardService에 removeComment(Long commentId) 메서드가 구현되어 있어야 함
		boardService.removeComment(commentId);
		return "redirect:/admin/boards/view/" + boardId;
	}
}