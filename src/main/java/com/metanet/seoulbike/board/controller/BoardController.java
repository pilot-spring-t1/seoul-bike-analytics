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
        
        if (auth != null && auth.getPrincipal() instanceof Member) {
            Member member = (Member) auth.getPrincipal();
            model.addAttribute("userName", member.getLoginId());
            model.addAttribute("memberId", member.getMemberId());
        } else {
            model.addAttribute("userName", "Guest");
        }
        
        return category.equals("NOTICE") ? "boards/board-notice" : "boards/board-suggestion";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Authentication auth, Model model) {
        boardService.increaseViewCount(id);
        model.addAttribute("board", boardService.getBoard(id));
        model.addAttribute("files", fileAttachmentService.getFileListByBoardId(id));
        model.addAttribute("comments", boardService.getCommentList(id));
        
        if (auth != null && auth.getPrincipal() instanceof Member) {
            Member member = (Member) auth.getPrincipal();
            model.addAttribute("userName", member.getLoginId());
            model.addAttribute("memberId", member.getMemberId());
        } else {
            model.addAttribute("userName", "Guest");
        }
        return "boards/board-view";
    }

    @GetMapping("/write")
    @PreAuthorize("isAuthenticated()")
    public String writeForm(@RequestParam(defaultValue = "SUGGESTION") String category, Model model, Authentication auth) {
        if ("NOTICE".equals(category) && !auth.getAuthorities().toString().contains("ROLE_ADMIN")) {
            return "redirect:/boards/suggestion";
        }
        BoardDto dto = new BoardDto();
        dto.setCategory(category);
        model.addAttribute("board", dto);
        
        if (auth != null && auth.getPrincipal() instanceof Member) {
            Member member = (Member) auth.getPrincipal();
            model.addAttribute("userName", member.getLoginId());
            model.addAttribute("memberId", member.getMemberId());
        } else {
            model.addAttribute("userName", "Guest");
        }
        return "boards/board-write";
    }

    @PostMapping("/register")
    @PreAuthorize("isAuthenticated()")
    public String register(@ModelAttribute BoardDto dto,
                           @RequestParam(value = "uploadFiles", required = false) List<MultipartFile> files, 
                           Authentication auth) {
        if ("NOTICE".equals(dto.getCategory()) && !auth.getAuthorities().toString().contains("ROLE_ADMIN")) {
            return "redirect:/error/403";
        }
        dto.setWriter(auth.getName());
        boardService.createBoard(dto);
        handleFileUpload(files, dto.getBoardId(), auth.getName());
        return "redirect:/boards/" + dto.getCategory().toLowerCase();
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN') or @boardService.getBoard(#id).writer == authentication.name")
    public String editForm(@PathVariable("id") Long id, Model model, Authentication auth) {
        // 1. 게시글 정보를 가져와 변수에 할당
        BoardDto board = boardService.getBoard(id);
        model.addAttribute("board", board);
        
        // 2. 파일 목록 조회
        model.addAttribute("files", fileAttachmentService.getFileListByBoardId(id));
        
        // 3. 사이드바 활성화를 위한 activeMenu 추가
        if (board != null && board.getCategory() != null) {
            model.addAttribute("activeMenu", board.getCategory().toLowerCase());
        }
        
        // 4. 사용자 정보 처리
        if (auth != null && auth.getPrincipal() instanceof Member) {
            Member member = (Member) auth.getPrincipal();
            model.addAttribute("userName", member.getLoginId());
            model.addAttribute("memberId", member.getMemberId());
        } else {
            model.addAttribute("userName", "Guest");
        }
        
        return "boards/board-write";
    }

    @PostMapping("/modify")
    @PreAuthorize("hasRole('ADMIN') or @boardService.getBoard(#dto.boardId).writer == authentication.name")
    public String modify(@ModelAttribute BoardDto dto,
                         @RequestParam(value = "deleteFileIds", required = false) List<Long> deleteFileIds,
                         @RequestParam(value = "uploadFiles", required = false) List<MultipartFile> files, 
                         Authentication auth, RedirectAttributes rttr) {

        if (deleteFileIds != null) {
            deleteFileIds.forEach(fileId -> fileAttachmentService.deleteFile(fileId));
        }
        boardService.updateBoard(dto);
        handleFileUpload(files, dto.getBoardId(), auth.getName());

        rttr.addFlashAttribute("message", "수정되었습니다.");
        return "redirect:/boards/view/" + dto.getBoardId();
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN') or @boardService.getBoard(#id).writer == authentication.name")
    public String delete(@PathVariable("id") Long id, @RequestParam("category") String category) {
        boardService.deleteBoard(id);
        return "redirect:/boards/" + category.toLowerCase();
    }

    @PostMapping("/like/{id}")
    @PreAuthorize("isAuthenticated()")
    public String like(@PathVariable("id") Long id, Authentication auth, RedirectAttributes rttr) {
        Member member = (Member) auth.getPrincipal();
        boolean isLiked = boardService.createLike(id, member.getMemberId());
        
        if (!isLiked) {
            rttr.addFlashAttribute("error", "이미 추천한 게시물입니다.");
        } else {
            rttr.addFlashAttribute("message", "추천되었습니다.");
        }
        return "redirect:/boards/view/" + id;
    }

    @PostMapping("/comment/register")
    @PreAuthorize("isAuthenticated()")
    public String registerComment(@RequestParam("boardId") Long boardId, 
                                  @RequestParam(value = "parentId", required = false) Long parentId,
                                  @RequestParam("content") String content, 
                                  Authentication auth) {
        boardService.createComment(boardId, parentId, content, auth.getName());
        return "redirect:/boards/view/" + boardId;
    }

    @PostMapping("/comment/modify")
    @PreAuthorize("hasRole('ADMIN') or @boardService.getCommentWriter(#commentId) == authentication.name")
    public String modifyComment(@RequestParam("commentId") Long commentId, 
                                @RequestParam("boardId") Long boardId, 
                                @RequestParam("content") String content) {
        boardService.updateComment(commentId, content);
        return "redirect:/boards/view/" + boardId;
    }

    @PostMapping("/comment/delete")
    @PreAuthorize("hasRole('ADMIN') or @boardService.getCommentWriter(#commentId) == authentication.name")
    public String deleteComment(@RequestParam("commentId") Long commentId, 
                                @RequestParam("boardId") Long boardId) {
        boardService.deleteComment(commentId);
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