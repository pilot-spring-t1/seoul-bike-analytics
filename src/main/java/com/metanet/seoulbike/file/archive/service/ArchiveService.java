package com.metanet.seoulbike.file.archive.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.metanet.seoulbike.file.archive.dto.ArchiveDto;
import com.metanet.seoulbike.file.archive.dto.ArchiveSearchDto;
import com.metanet.seoulbike.file.archive.repository.IArchiveRepository;
import com.metanet.seoulbike.file.service.FileStorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArchiveService {

    private final FileStorageService fileStorageService;
    private final IArchiveRepository archiveRepository; // 인터페이스 주입

    @Transactional(rollbackFor = Exception.class)
    public ArchiveDto uploadArchive(MultipartFile file, String archiveTitle, String archiveDesc, String uploaderId) {
        if (file == null || file.isEmpty()) return null;

        String orgName = file.getOriginalFilename();
        String extension = (orgName != null && orgName.contains(".")) 
                           ? orgName.substring(orgName.lastIndexOf(".")) : "";

        String fileUuid = UUID.randomUUID().toString() + extension;
        String relativePath = "archive";
        String savedPath = null;

        try {
            // 1. 파일 시스템에 저장
            savedPath = fileStorageService.uploadFile(file, relativePath, fileUuid);

            // 2. DB 저장용 DTO 구성
            ArchiveDto dto = new ArchiveDto();
            dto.setArchiveTitle(archiveTitle);
            dto.setArchiveDesc(archiveDesc);
            dto.setFileUuid(fileUuid);
            dto.setFileName(orgName);
            dto.setFilePath(savedPath);
            dto.setFileSize(file.getSize());
            dto.setUploaderId(uploaderId);

            // 3. 레포지토리를 통한 DB 저장
            archiveRepository.save(dto);
            return dto;

        } catch (Exception e) {
            // 실패 시 파일 삭제 (보상 트랜잭션)
            if (savedPath != null) {
                fileStorageService.deleteFile(savedPath);
            }
            throw new RuntimeException("아카이브 업로드 실패: " + orgName, e);
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getArchiveList(ArchiveSearchDto searchDto) {
        Map<String, Object> result = new HashMap<>();

        int totalCount = archiveRepository.countAll(searchDto);
        int totalPages = (totalCount > 0) 
                         ? (int) Math.ceil((double) totalCount / searchDto.getRecordSize()) : 0;

        List<ArchiveDto> list = archiveRepository.findAll(searchDto);

        result.put("list", list);
        result.put("totalPages", totalPages);
        result.put("totalCount", totalCount);

        return result;
    }

    @Transactional(readOnly = true)
    public ArchiveDto getArchiveById(Long archiveId) {
        ArchiveDto dto = archiveRepository.findById(archiveId);
        if (dto == null) {
            throw new RuntimeException("아카이브를 찾을 수 없습니다. ID: " + archiveId);
        }
        return dto;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteArchive(Long archiveId) {
        ArchiveDto dto = archiveRepository.findById(archiveId);
        if (dto == null) return;

        try {
            // 1. DB 삭제
            archiveRepository.delete(archiveId);
            // 2. 실제 파일 삭제
            fileStorageService.deleteFile(dto.getFilePath());
            
        } catch (Exception e) {
            log.error("[ArchiveService] 삭제 실패: {}", dto.getFileName(), e);
            throw new RuntimeException("아카이브 삭제 실패", e);
        }
    }
}