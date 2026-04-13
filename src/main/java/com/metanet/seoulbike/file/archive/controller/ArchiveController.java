package com.metanet.seoulbike.file.archive.controller;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import com.metanet.seoulbike.file.archive.dto.ArchiveDto;
import com.metanet.seoulbike.file.archive.dto.ArchiveSearchDto;
import com.metanet.seoulbike.file.archive.service.ArchiveService;
import com.metanet.seoulbike.file.service.FileStorageService;
import com.metanet.seoulbike.member.model.Member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/archive")
@RequiredArgsConstructor
public class ArchiveController {

    private final ArchiveService archiveService;
    private final FileStorageService fileStorageService;

    /**
     * 1. 아카이브 목록 조회
     */
    @GetMapping("/list")
    public String list(@ModelAttribute("searchDto") ArchiveSearchDto searchDto, Model model, Authentication auth) {
        Map<String, Object> result = archiveService.getArchiveList(searchDto);
        
        model.addAttribute("list", result.get("list"));
        model.addAttribute("totalPages", result.get("totalPages"));

        // 사용자 커스텀 로직 유지
        if (auth != null && auth.getPrincipal() instanceof Member) {
            Member member = (Member) auth.getPrincipal();
            model.addAttribute("userName", member.getLoginId());
            model.addAttribute("memberId", member.getMemberId());
        } else {
            model.addAttribute("userName", "Guest");
        }
        
        return "archive/archive-list";
    }

    /**
     * 2. 아카이브 자료 등록 폼
     */
    @GetMapping("/upload-form")
    @PreAuthorize("hasRole('ADMIN')")
    public String registerForm(Model model, Authentication auth) {
        model.addAttribute("archive", new ArchiveDto());
        if (auth != null) {
            model.addAttribute("userName", auth.getName());
        } else {
            model.addAttribute("userName", "Guest");
        }
        return "archive/archive-upload";
    }

    /**
     * 3. 아카이브 자료 등록 실행
     */
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public String register(
            @RequestParam("uploadFile") MultipartFile file,
            @RequestParam("archiveTitle") String archiveTitle,
            @RequestParam(value = "archiveDesc", required = false) String archiveDesc,
            Authentication auth,
            RedirectAttributes rttr) {
        
        try {
            if (file.isEmpty()) {
                rttr.addFlashAttribute("error", "파일은 필수입니다.");
                // 폼 경로에 맞게 리다이렉트 주소 확인 필요 (기존 코드가 /write라면 수정 권장)
                return "redirect:/archive/upload-form"; 
            }
            
            // 서비스 메서드 명칭 확인: uploadArchive
            archiveService.uploadArchive(file, archiveTitle, archiveDesc, auth.getName());
            rttr.addFlashAttribute("message", "자료가 성공적으로 등록되었습니다.");
            
        } catch (Exception e) {
            log.error("[ArchiveController] 자료 등록 실패: {}", e.getMessage());
            rttr.addFlashAttribute("error", "등록 중 오류가 발생했습니다.");
        }
        
        return "redirect:/archive/list";
    }

    /**
     * 4. 아카이브 파일 다운로드
     */
    @GetMapping("/download/{archiveId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource> download(@PathVariable Long archiveId) {
        try {
            // 서비스 메서드 명칭 확인: getArchiveById
            ArchiveDto dto = archiveService.getArchiveById(archiveId);
            Resource resource = fileStorageService.loadFile(dto.getFilePath());

            String encodedFileName = UriUtils.encode(dto.getFileName(), StandardCharsets.UTF_8);
            
            String contentType = Files.probeContentType(Paths.get(dto.getFilePath()));
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(dto.getFileSize()))
                    .body(resource);

        } catch (Exception e) {
            log.error("[ArchiveController] 다운로드 실패. archiveId: {}", archiveId, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 5. 아카이브 자료 삭제
     */
    @PostMapping("/delete/{archiveId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long archiveId, RedirectAttributes rttr) {
        try {
            archiveService.deleteArchive(archiveId);
            rttr.addFlashAttribute("message", "자료가 삭제되었습니다.");
        } catch (Exception e) {
            log.error("[ArchiveController] 삭제 실패. archiveId: {}", archiveId, e);
            rttr.addFlashAttribute("error", "삭제 처리 중 오류가 발생했습니다.");
        }
        return "redirect:/archive/list";
    }
}