package com.metanet.seoulbike.file.attachment.controller;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import com.metanet.seoulbike.file.attachment.dto.FileAttachmentDto;
import com.metanet.seoulbike.file.attachment.service.FileAttachmentService;
import com.metanet.seoulbike.file.service.FileStorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/attachments")
@RequiredArgsConstructor
public class FileAttachmentController {

    private final FileAttachmentService fileAttachmentService;
    private final FileStorageService fileStorageService;

    
    // 1. 업로드
    @PostMapping("/upload")
    public ResponseEntity<FileAttachmentDto> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("boardId") Long boardId,
            Authentication auth) {
        try {
            FileAttachmentDto dto = fileAttachmentService.uploadFile(file, boardId, auth.getName());
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error("[FileAttachmentController] 업로드 실패: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    
    // 2. 다운로드
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> download(@PathVariable Long fileId) {
        try {
            FileAttachmentDto dto = fileAttachmentService.selectFileById(fileId);
            Resource resource = fileStorageService.loadFile(dto.getFilePath());

            String encodedFileName = UriUtils.encode(dto.getFileName(), StandardCharsets.UTF_8);
            String contentType = Files.probeContentType(Paths.get(dto.getFilePath()));
            if (contentType == null) contentType = "application/octet-stream";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + encodedFileName + "\"")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(dto.getFileSize()))
                    .body(resource);

        } catch (Exception e) {
            log.error("[FileAttachmentController] 다운로드 실패. fileId: {}", fileId, e);
            return ResponseEntity.notFound().build();
        }
    }

    	
    // 3. 삭제
    @PostMapping("/delete/{fileId}")
    public ResponseEntity<Void> delete(@PathVariable Long fileId) {
        try {
            fileAttachmentService.deleteFile(fileId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("[FileAttachmentController] 삭제 실패. fileId: {}", fileId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}