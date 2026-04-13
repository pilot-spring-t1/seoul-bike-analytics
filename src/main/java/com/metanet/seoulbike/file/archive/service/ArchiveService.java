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
import com.metanet.seoulbike.file.archive.mapper.ArchiveMapper;
import com.metanet.seoulbike.file.service.FileStorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArchiveService {

    private final FileStorageService fileStorageService;
    private final ArchiveMapper archiveMapper;

    @Transactional(rollbackFor = Exception.class)
    public ArchiveDto uploadArchive(MultipartFile file, String archiveTitle, String archiveDesc, String uploaderId) {
        if (file == null || file.isEmpty()) return null;

        String orgName = file.getOriginalFilename();
        String extension = "";
        if (orgName != null && orgName.contains(".")) {
            extension = orgName.substring(orgName.lastIndexOf("."));
        }

        String fileUuid = UUID.randomUUID().toString() + extension;
        String relativePath = "archive";
        String savedPath = null;

        try {
            savedPath = fileStorageService.uploadFile(file, relativePath, fileUuid);

            ArchiveDto dto = new ArchiveDto();
            dto.setArchiveTitle(archiveTitle);
            dto.setArchiveDesc(archiveDesc);
            dto.setFileUuid(fileUuid);
            dto.setFileName(orgName);
            dto.setFilePath(savedPath);
            dto.setFileSize(file.getSize());
            dto.setUploaderId(uploaderId);

            archiveMapper.insertArchive(dto);
            return dto;

        } catch (Exception e) {
            if (savedPath != null) {
                fileStorageService.deleteFile(savedPath);
            }
            throw new RuntimeException("아카이브 업로드 실패: " + orgName, e);
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getArchiveList(ArchiveSearchDto searchDto) {
        Map<String, Object> result = new HashMap<>();

        int totalCount = archiveMapper.selectArchiveCount(searchDto);

        int totalPages = 0;
        if (totalCount > 0) {
            totalPages = (int) Math.ceil((double) totalCount / searchDto.getRecordSize());
        }

        List<ArchiveDto> list = archiveMapper.selectArchiveList(searchDto);

        result.put("list", list);
        result.put("totalPages", totalPages);
        result.put("totalCount", totalCount);

        return result;
    }

    @Transactional(readOnly = true)
    public ArchiveDto getArchiveById(Long archiveId) {
        ArchiveDto dto = archiveMapper.selectArchiveById(archiveId);
        if (dto == null) {
            throw new RuntimeException("아카이브를 찾을 수 없습니다. ID: " + archiveId);
        }
        return dto;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteArchive(Long archiveId) {
        ArchiveDto dto = archiveMapper.selectArchiveById(archiveId);
        if (dto == null) return;

        try {
            archiveMapper.deleteArchive(archiveId);
            fileStorageService.deleteFile(dto.getFilePath());
            
        } catch (Exception e) {
            log.error("[ArchiveService] 삭제 실패: {}", dto.getFileName(), e);
            throw new RuntimeException("아카이브 삭제 실패", e);
        }
    }
}