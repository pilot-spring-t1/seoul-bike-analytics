package com.metanet.seoulbike.board.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
import org.springframework.web.servlet.support.RequestContextUtils;

import com.metanet.seoulbike.board.dto.BoardDto;
import com.metanet.seoulbike.board.dto.BoardSearchDto;
import com.metanet.seoulbike.board.service.BoardService;
import com.metanet.seoulbike.file.dto.FileDto;
import com.metanet.seoulbike.file.mapper.FileMapper;
import com.metanet.seoulbike.file.service.FileService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {

	private final BoardService boardService;
	private final FileService fileService;
	private final FileMapper fileMapper;

	// =========================
	// 1 & 2. 공지사항 / 건의사항 리스트
	// =========================
	@GetMapping({ "/notice", "/suggestion" })
	public String list(@ModelAttribute("searchDto") BoardSearchDto searchDto, Model model, HttpServletRequest request) {

		// FlashAttribute로 넘어온 검색 데이터(searchDto)가 있으면 적용 (새로고침 시 비워짐)
		Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
		if (flashMap != null && flashMap.containsKey("searchDto")) {
			searchDto = (BoardSearchDto) flashMap.get("searchDto");
		}

		// URI에 따라 카테고리 자동 설정
		String uri = request.getRequestURI();
		String category = uri.contains("notice") ? "NOTICE" : "SUGGESTION";
		searchDto.setCategory(category);

		Map<String, Object> result = boardService.getBoardList(searchDto);
		model.addAttribute("list", result.get("list"));
		model.addAttribute("totalPages", result.get("totalPages"));
		model.addAttribute("searchDto", searchDto);

		return category.equals("NOTICE") ? "boards/board-notice" : "boards/board-suggestion";
	}

	// =========================
	// 3. 상세
	// =========================
	@GetMapping("/view/{id}")
	public String view(@PathVariable Long id, Model model) {
		boardService.addViewCount(id);
		model.addAttribute("board", boardService.getBoardById(id));
		model.addAttribute("files", fileMapper.getFilesByBoardId(id));
		model.addAttribute("comments", boardService.getComments(id));
		return "boards/board-view";
	}

	// =========================
	// 4. 등록 폼
	// =========================
	@GetMapping("/write")
	@PreAuthorize("(#category == 'NOTICE' and hasRole('ADMIN')) or (#category == 'SUGGESTION')")
	public String writeForm(@RequestParam(defaultValue = "NOTICE") String category, Model model) {
		BoardDto dto = new BoardDto();
		dto.setCategory(category);
		model.addAttribute("board", dto);
		return "boards/board-write";
	}

	// =========================
	// 5. 등록 실행
	// =========================
	@PostMapping("/register")
	@PreAuthorize("(#dto.category == 'NOTICE' and hasRole('ADMIN')) or (#dto.category == 'SUGGESTION')")
	public String register(@ModelAttribute BoardDto dto,
			@RequestParam(value = "uploadFiles", required = false) List<MultipartFile> files, Authentication auth)
			throws IOException {

		dto.setWriter(auth.getName());
		boardService.registerBoard(dto);

		if (files != null) {
			for (MultipartFile file : files) {
				if (!file.isEmpty()) {
					fileService.uploadFile(file, dto.getWriter(), dto.getBoardId());
				}
			}
		}
		return "redirect:/boards/" + dto.getCategory().toLowerCase();
	}

	// =========================
	// 6. 수정 폼
	// =========================
	@GetMapping("/edit/{id}")
	@PreAuthorize("hasRole('ADMIN') or @boardService.getBoardById(#id).writer == authentication.name")
	public String editForm(@PathVariable Long id, Model model) {
		model.addAttribute("board", boardService.getBoardById(id));
		model.addAttribute("files", fileMapper.getFilesByBoardId(id));
		model.addAttribute("isEdit", true);
		return "boards/board-write";
	}

	// =========================
	// 7. 수정 실행
	// =========================
	@PostMapping("/modify")
	@PreAuthorize("hasRole('ADMIN') or @boardService.getBoardById(#dto.boardId).writer == authentication.name")
	public String modify(@ModelAttribute BoardDto dto,
			@RequestParam(value = "uploadFiles", required = false) List<MultipartFile> files, Authentication auth,
			RedirectAttributes rttr) throws IOException {

		boardService.modifyBoard(dto);

		if (files != null) {
			for (MultipartFile file : files) {
				if (!file.isEmpty()) {
					fileService.uploadFile(file, auth.getName(), dto.getBoardId());
				}
			}
		}
		rttr.addFlashAttribute("message", "수정되었습니다.");
		return "redirect:/boards/view/" + dto.getBoardId();
	}

	// =========================
	// 8. 삭제
	// =========================
	@PostMapping("/delete/{id}")
	@PreAuthorize("hasRole('ADMIN') or @boardService.getBoardById(#id).writer == authentication.name")
	public String delete(@PathVariable Long id, @RequestParam String category) {
		List<FileDto> files = fileMapper.getFilesByBoardId(id);
		for (FileDto file : files) {
			fileService.deleteFile((long) file.getFileId());
		}
		boardService.removeBoard(id);
		return "redirect:/boards/" + category.toLowerCase();
	}

	// =========================
	// 9. 검색 (FlashAttribute 적용으로 새로고침 시 초기화)
	// =========================
	@PostMapping("/search")
	public String search(@ModelAttribute BoardSearchDto searchDto, RedirectAttributes rttr) {
		String category = searchDto.getCategory().toLowerCase();
		rttr.addAttribute("page", 1);

		// 검색어를 URL이 아닌 세션(FlashMap)에 잠시 저장
		if (searchDto.getKeyword() != null && !searchDto.getKeyword().isEmpty()) {
			rttr.addFlashAttribute("searchDto", searchDto);
		}
		return "redirect:/boards/" + category;
	}

	// =========================
	// 10. 추천
	// =========================
	@PostMapping("/like/{id}")
	public String like(@PathVariable Long id) {
		boardService.addLikeCount(id);
		return "redirect:/boards/view/" + id;
	}
}