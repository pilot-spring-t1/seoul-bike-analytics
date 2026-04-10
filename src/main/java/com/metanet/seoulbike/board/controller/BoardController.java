package com.metanet.seoulbike.board.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
import com.metanet.seoulbike.file.dto.FileDto;
import com.metanet.seoulbike.file.mapper.FileMapper;
import com.metanet.seoulbike.file.service.FileService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/boards")
public class AdminBoardController {

    private final BoardService boardService;
    private final FileService fileService;
    private final FileMapper fileMapper;

    /**
     * 1. 공지사항 리스트 (board-notice.html)
     */
    @GetMapping("/notice")
    public String noticeList(@ModelAttribute("searchDto") BoardSearchDto searchDto, Model model) {
        searchDto.setCategory("NOTICE");
        Map<String, Object> result = boardService.getBoardList(searchDto);
        model.addAttribute("list", result.get("list"));
        model.addAttribute("totalPages", result.get("totalPages"));
        return "boards/board-notice";
    }

    /**
     * 2. 건의사항 리스트 (board-suggestion.html)
     */
    @GetMapping("/suggestion")
    public String suggestionList(@ModelAttribute("searchDto") BoardSearchDto searchDto, Model model) {
        searchDto.setCategory("SUGGESTION");
        Map<String, Object> result = boardService.getBoardList(searchDto);
        model.addAttribute("list", result.get("list"));
        model.addAttribute("totalPages", result.get("totalPages"));
        return "boards/board-suggestion";
    }

    /**
     * 3. 상세 페이지
     */
    @GetMapping("/view/{id}")
    public String view(@PathVariable("id") Long id, Model model) {
        boardService.addViewCount(id);
        model.addAttribute("board", boardService.getBoardById(id));
        model.addAttribute("files", fileMapper.getFilesByBoardId(id));
        model.addAttribute("comments", boardService.getComments(id));
        return "boards/board-view";
    }

    /**
     * 4. 등록 폼
     */
    @GetMapping("/write")
    public String writeForm(@RequestParam(value = "category", defaultValue = "NOTICE") String category, Model model) {
        BoardDto boardDto = new BoardDto();
        boardDto.setCategory(category);
        model.addAttribute("board", boardDto);
        return "boards/board-write";
    }

    /**
     * 5. 등록 실행
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
        return "redirect:/boards/" + boardDto.getCategory().toLowerCase();
    }

    /**
     * 6. 수정 폼 (기존 정보 불러오기)
     */
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("board", boardService.getBoardById(id));
        model.addAttribute("files", fileMapper.getFilesByBoardId(id)); // 기존 파일 목록도 필요시 전달
        model.addAttribute("isEdit", true); 
        return "boards/board-write";
    }

    /**
     * 7. 수정 실행 (추가된 핵심 로직)
     */
    @PostMapping("/modify")
    public String modify(@ModelAttribute BoardDto boardDto,
            @RequestParam(value = "uploadFiles", required = false) List<MultipartFile> files,
            RedirectAttributes rttr) throws IOException {

        // 게시글 업데이트
        boardService.modifyBoard(boardDto);

        // 새 파일 업로드 처리
        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    fileService.uploadFile(file, boardDto.getWriter(), boardDto.getBoardId());
                }
            }
        }
        
        rttr.addFlashAttribute("message", "수정되었습니다.");
        // 수정 후 상세 페이지로 리다이렉트
        return "redirect:/boards/view/" + boardDto.getBoardId();
    }

    /**
     * 8. 삭제 실행
     */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, @RequestParam String category) {
        // 실물 파일 및 DB 파일 정보 삭제
        List<FileDto> files = fileMapper.getFilesByBoardId(id);
        for (FileDto file : files) {
            fileService.deleteFile((long) file.getFileId());
        }
        // 게시글 삭제
        boardService.removeBoard(id);
        
        // 해당 카테고리 목록으로 이동
        return "redirect:/boards/" + category.toLowerCase();
    }

    /**
     * 9. 추천 API
     */
    @PostMapping("/like/{id}")
    public String like(@PathVariable Long id) {
        boardService.addLikeCount(id);
        return "redirect:/boards/view/" + id;
    }
}