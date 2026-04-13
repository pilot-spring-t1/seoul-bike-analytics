package com.metanet.seoulbike.board.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.metanet.seoulbike.board.dto.BoardDto;
import com.metanet.seoulbike.board.dto.BoardSearchDto;
import com.metanet.seoulbike.board.service.BoardService;
import com.metanet.seoulbike.file.attachment.service.FileAttachmentService;
import com.metanet.seoulbike.member.model.Member;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;
    private final FileAttachmentService fileAttachmentService;

    // 1. 리스트 조회 (공지사항/건의사항 통합)
    @GetMapping({ "/notice", "/suggestion" })
    public String list(@ModelAttribute("searchDto") BoardSearchDto searchDto, Model model, Authentication auth, HttpServletRequest request) {
        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
        if (flashMap != null && flashMap.containsKey("searchDto")) {
            searchDto = (BoardSearchDto) flashMap.get("searchDto");
        }
        
        String uri = request.getRequestURI();
        String category = uri.contains("notice") ? "NOTICE" : "SUGGESTION";
        searchDto.setCategory(category);

        Map<String, Object> result = boardService.getBoardList(searchDto);
        model.addAttribute("list", result.get("list"));
        model.addAttribute("totalPages", result.get("totalPages"));
        model.addAttribute("searchDto", searchDto);
        model.addAttribute("activeMenu", category.toLowerCase());
        if (auth != null) {
            model.addAttribute("userName", auth.getName());
        } else {
            model.addAttribute("userName", "Guest");
        }
        
        return category.equals("NOTICE") ? "boards/board-notice" : "boards/board-suggestion";
    }

    // 2. 상세 보기
    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Authentication auth, Model model) {
        boardService.addViewCount(id);
        model.addAttribute("board", boardService.getBoardById(id));
        model.addAttribute("files", fileAttachmentService.selectFilesByBoardId(id));
        model.addAttribute("comments", boardService.getComments(id));
        if (auth != null) {
            model.addAttribute("userName", auth.getName());
        } else {
            model.addAttribute("userName", "Guest");
        }
        return "boards/board-view";
    }

    // 3. 등록 폼
    @GetMapping("/write")
    @PreAuthorize("isAuthenticated()")
    public String writeForm(@RequestParam(defaultValue = "SUGGESTION") String category, Model model, Authentication auth) {
        if ("NOTICE".equals(category) && !auth.getAuthorities().toString().contains("ROLE_ADMIN")) {
            return "redirect:/boards/suggestion";
        }
        BoardDto dto = new BoardDto();
        dto.setCategory(category);
        model.addAttribute("board", dto);
        if (auth != null) {
            model.addAttribute("userName", auth.getName());
        } else {
            model.addAttribute("userName", "Guest");
        }
        return "boards/board-write";
    }

    // 4. 등록 실행
    @PostMapping("/register")
    @PreAuthorize("isAuthenticated()")
    public String register(@ModelAttribute BoardDto dto,
                           @RequestParam(value = "uploadFiles", required = false) List<MultipartFile> files, 
                           Authentication auth) {
        if ("NOTICE".equals(dto.getCategory()) && !auth.getAuthorities().toString().contains("ROLE_ADMIN")) {
            return "redirect:/error/403";
        }
        dto.setWriter(auth.getName());
        boardService.registerBoard(dto);
        handleFileUpload(files, dto.getBoardId(), auth.getName());
        return "redirect:/boards/" + dto.getCategory().toLowerCase();
    }

    // 5. 수정 폼
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN') or @boardService.getBoardById(#id).writer == authentication.name")
    public String editForm(@PathVariable("id") Long id, Model model, Authentication auth) {
        model.addAttribute("board", boardService.getBoardById(id));
        model.addAttribute("files", fileAttachmentService.selectFilesByBoardId(id));
        if (auth != null) {
            model.addAttribute("userName", auth.getName());
        } else {
            model.addAttribute("userName", "Guest");
        }
        return "boards/board-write";
    }

    // 6. 수정 실행
    @PostMapping("/modify")
    @PreAuthorize("hasRole('ADMIN') or @boardService.getBoardById(#dto.boardId).writer == authentication.name")
    public String modify(@ModelAttribute BoardDto dto,
                         @RequestParam(value = "deleteFileIds", required = false) List<Long> deleteFileIds,
                         @RequestParam(value = "uploadFiles", required = false) List<MultipartFile> files, 
                         Authentication auth, RedirectAttributes rttr) {

        if (deleteFileIds != null) {
            deleteFileIds.forEach(fileId -> fileAttachmentService.deleteFile(fileId));
        }
        boardService.modifyBoard(dto);
        handleFileUpload(files, dto.getBoardId(), auth.getName());

        rttr.addFlashAttribute("message", "수정되었습니다.");
        return "redirect:/boards/view/" + dto.getBoardId();
    }

    // 7. 삭제 실행
    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN') or @boardService.getBoardById(#id).writer == authentication.name")
    public String delete(@PathVariable("id") Long id, @RequestParam("category") String category) {
        boardService.removeBoard(id);
        return "redirect:/boards/" + category.toLowerCase();
    }

    // 8. 게시글 추천 (중복 방지 로직 적용)
    @PostMapping("/like/{id}")
    @PreAuthorize("isAuthenticated()")
    public String like(@PathVariable("id") Long id, Authentication auth, RedirectAttributes rttr) {
        // JwtTokenProvider에서 Principal에 Member 객체를 담았으므로 캐스팅하여 PK 추출
        Member member = (Member) auth.getPrincipal();
        
        boolean isLiked = boardService.addLike(id, member.getMemberId());
        
        if (!isLiked) {
            rttr.addFlashAttribute("error", "이미 추천한 게시물입니다.");
        } else {
            rttr.addFlashAttribute("message", "추천되었습니다.");
        }
        
        return "redirect:/boards/view/" + id;
    }

    // 9. 댓글 등록
    @PostMapping("/comment/register")
    @PreAuthorize("isAuthenticated()")
    public String registerComment(@RequestParam("boardId") Long boardId, 
                                  @RequestParam(value = "parentId", required = false) Long parentId,
                                  @RequestParam("content") String content, 
                                  Authentication auth) {
        boardService.registerComment(boardId, parentId, content, auth.getName());
        return "redirect:/boards/view/" + boardId;
    }

    // 10. 댓글 수정
    @PostMapping("/comment/modify")
    @PreAuthorize("hasRole('ADMIN') or @boardService.getCommentWriter(#commentId) == authentication.name")
    public String modifyComment(@RequestParam("commentId") Long commentId, 
                                @RequestParam("boardId") Long boardId, 
                                @RequestParam("content") String content) {
        boardService.modifyComment(commentId, content);
        return "redirect:/boards/view/" + boardId;
    }

    // 11. 댓글 삭제
    @PostMapping("/comment/delete")
    @PreAuthorize("hasRole('ADMIN') or @boardService.getCommentWriter(#commentId) == authentication.name")
    public String deleteComment(@RequestParam("commentId") Long commentId, 
                                @RequestParam("boardId") Long boardId) {
        boardService.removeComment(commentId);
        return "redirect:/boards/view/" + boardId;
    }

    private void handleFileUpload(List<MultipartFile> files, Long boardId, String writer) {
        if (files != null) {
            files.stream().filter(f -> !f.isEmpty()).forEach(file -> {
                try {
                    fileAttachmentService.uploadFile(file, boardId, writer);
                } catch (Exception e) {
                    log.error("파일 업로드 실패: {}", file.getOriginalFilename());
                }
            });
        }
    }
}