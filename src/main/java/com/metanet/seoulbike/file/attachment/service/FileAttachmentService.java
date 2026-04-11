package com.metanet.seoulbike.file.attachment.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.metanet.seoulbike.file.attachment.dto.FileAttachmentDto;
import com.metanet.seoulbike.file.attachment.mapper.FileAttachmentMapper;
import com.metanet.seoulbike.file.service.FileStorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileAttachmentService {

    private final FileStorageService fileStorageService;
    private final FileAttachmentMapper fileAttachmentMapper;

    /**
     * 파일 업로드 — 디스크 저장 후 DB insert
     * 디스크 저장 성공 / DB 실패 시 → 디스크 파일 롤백 처리
     */
    @Transactional(rollbackFor = Exception.class)
    public FileAttachmentDto uploadFile(MultipartFile file, Long boardId, String uploaderId) {
        if (file == null || file.isEmpty()) return null;

        String orgName = file.getOriginalFilename();
        String extension = "";
        if (orgName != null && orgName.contains(".")) {
            extension = orgName.substring(orgName.lastIndexOf("."));
        }

        String fileUuid = UUID.randomUUID().toString() + extension;
        String relativePath = "attachments/" + boardId;
        String savedPath = null;

        try {
            // 1. 디스크 저장
            savedPath = fileStorageService.uploadFile(file, relativePath, fileUuid);

            // 2. DB insert
            FileAttachmentDto dto = new FileAttachmentDto();
            dto.setBoardId(boardId);
            dto.setFileUuid(fileUuid);
            dto.setFileName(orgName);
            dto.setFilePath(savedPath);
            dto.setFileSize(file.getSize());
            dto.setUploaderId(uploaderId);

            fileAttachmentMapper.insertFile(dto);
            log.info("[FileAttachment] 업로드 완료: {} -> {}", orgName, fileUuid);
            return dto;

        } catch (Exception e) {
            // DB insert 실패 시 디스크에 저장된 파일 롤백
            if (savedPath != null) {
                fileStorageService.deleteFile(savedPath);
                log.warn("[FileAttachment] DB insert 실패로 디스크 파일 롤백: {}", savedPath);
            }
            throw new RuntimeException("파일 업로드 실패: " + orgName, e);
        }
    }

    /**
     * 파일 단건 조회
     * DB에는 있는데 실제 파일이 없는 경우 경고 로그
     */
    @Transactional(readOnly = true)
    public FileAttachmentDto selectFileById(Long fileId) {
        FileAttachmentDto dto = fileAttachmentMapper.selectFileById(fileId);
        if (dto == null) {
            throw new RuntimeException("파일 정보를 찾을 수 없습니다. ID: " + fileId);
        }

        // DB ✅ / 디스크 ❌ 불일치 체크
        if (!fileStorageService.existsFile(dto.getFilePath())) {
            log.warn("[FileAttachment] DB에는 있으나 실제 파일 없음: {}", dto.getFilePath());
            throw new RuntimeException("실제 파일을 찾을 수 없습니다: " + dto.getFileName());
        }

        return dto;
    }

    /**
     * 게시글별 파일 목록 조회
     */
    @Transactional(readOnly = true)
    public List<FileAttachmentDto> selectFilesByBoardId(Long boardId) {
        return fileAttachmentMapper.selectFilesByBoardId(boardId);
    }

    /**
     * 파일 삭제 — DB 먼저 삭제 후 디스크 삭제
     * DB 삭제 실패 시 → 예외 던져서 롤백
     * 디스크 삭제 실패 시 → 고아 파일로 남음, 로그만 기록
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(Long fileId) {
        FileAttachmentDto dto = fileAttachmentMapper.selectFileById(fileId);
        if (dto == null) {
            log.warn("[FileAttachment] 삭제 대상 없음. ID: {}", fileId);
            return;
        }

        try {
            // 1. DB 삭제 먼저 (트랜잭션 보호)
            fileAttachmentMapper.deleteFile(fileId);

            // 2. 디스크 삭제 (실패해도 DB는 이미 커밋됨 → 고아파일 로그)
            fileStorageService.deleteFile(dto.getFilePath());
            log.info("[FileAttachment] 삭제 완료: {}", dto.getFileName());

        } catch (Exception e) {
            log.error("[FileAttachment] 삭제 실패: {}", dto.getFileName(), e);
            throw new RuntimeException("파일 삭제 실패: " + dto.getFileName(), e);
        }
    }
}