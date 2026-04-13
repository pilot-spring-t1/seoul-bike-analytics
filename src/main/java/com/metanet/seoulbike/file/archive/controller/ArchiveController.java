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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import com.metanet.seoulbike.file.archive.dto.ArchiveDto;
import com.metanet.seoulbike.file.archive.dto.ArchiveSearchDto;
import com.metanet.seoulbike.file.archive.service.ArchiveService;
import com.metanet.seoulbike.file.service.FileStorageService;

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
     * 1. 목록 조회 (getList)
     */
    @GetMapping("/list")
    public String getList(@ModelAttribute("searchDto") ArchiveSearchDto searchDto, Model model, Authentication auth) {
        Map<String, Object> result = archiveService.getArchiveList(searchDto);
        
        model.addAttribute("list", result.get("list"));
        model.addAttribute("totalPages", result.get("totalPages"));
        model.addAttribute("userName", (auth != null) ? auth.getName() : "Guest");
        
        return "archive/archive-list";
    }

    /**
     * 2. 등록 폼 (createForm)
     */
    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createForm(Model model, Authentication auth) {
        model.addAttribute("archive", new ArchiveDto());
        model.addAttribute("userName", (auth != null) ? auth.getName() : "Guest");
        return "archive/archive-write";
    }

    /**
     * 3. 등록 실행 (upload)
     */
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public String upload(
            @RequestParam("uploadFile") MultipartFile file,
            @RequestParam("archiveTitle") String archiveTitle,
            @RequestParam(value = "archiveDesc", required = false) String archiveDesc,
            Authentication auth,
            RedirectAttributes rttr) {
        
        try {
            if (file.isEmpty()) {
                rttr.addFlashAttribute("error", "파일은 필수입니다.");
                return "redirect:/archive/create";
            }
            
            // 서비스 메서드명을 createArchive 혹은 uploadArchive 중 선택한 규칙에 맞춥니다.
            archiveService.createArchive(file, archiveTitle, archiveDesc, auth.getName());
            rttr.addFlashAttribute("message", "자료가 성공적으로 등록되었습니다.");
            
        } catch (Exception e) {
            log.error("[ArchiveController] 자료 등록 실패: {}", e.getMessage());
            rttr.addFlashAttribute("error", "등록 중 오류가 발생했습니다.");
        }
        
        return "redirect:/archive/list";
    }

    /**
     * 4. 파일 다운로드 (download)
     */
    @GetMapping("/download/{archiveId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource> download(@PathVariable Long archiveId) {
        try {
            // 상세 조회는 get으로 통일
            ArchiveDto dto = archiveService.getArchive(archiveId);
            Resource resource = fileStorageService.loadFile(dto.getFilePath());

            String encodedFileName = UriUtils.encode(dto.getFileName(), StandardCharsets.UTF_8);
            String contentType = Files.probeContentType(Paths.get(dto.getFilePath()));
            if (contentType == null) contentType = "application/octet-stream";

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
     * 5. 삭제 실행 (delete)
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