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

    /**
     * 아카이브 업로드
     */
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

    /**
     * 아카이브 목록 조회 (검색 및 페이징 적용)
     * @return 리스트와 전체 페이지 수를 포함한 Map
     */
    @Transactional(readOnly = true)
    public Map<String, Object> selectArchiveList(ArchiveSearchDto searchDto) {
        Map<String, Object> result = new HashMap<>();

        // 1. 검색 조건에 맞는 전체 데이터 개수 조회
        int totalCount = archiveMapper.selectArchiveCount(searchDto);

        // 2. 전체 페이지 수 계산 (예: 25개 데이터 / 페이지당 10개 = 3페이지)
        int totalPages = 0;
        if (totalCount > 0) {
            totalPages = (int) Math.ceil((double) totalCount / searchDto.getRecordSize());
        }

        // 3. 현재 페이지에 해당하는 리스트 조회
        List<ArchiveDto> list = archiveMapper.selectArchiveList(searchDto);

        result.put("list", list);
        result.put("totalPages", totalPages);
        result.put("totalCount", totalCount);

        return result;
    }

    /**
     * 아카이브 단건 조회
     */
    @Transactional(readOnly = true)
    public ArchiveDto selectArchiveById(Long archiveId) {
        ArchiveDto dto = archiveMapper.selectArchiveById(archiveId);
        if (dto == null) {
            throw new RuntimeException("아카이브를 찾을 수 없습니다. ID: " + archiveId);
        }
        return dto;
    }

    /**
     * 아카이브 삭제
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteArchive(Long archiveId) {
        ArchiveDto dto = archiveMapper.selectArchiveById(archiveId);
        if (dto == null) return;

        try {
            // DB 삭제 성공 시에만 파일 삭제를 시도하거나, 
            // 순서를 바꿔서 파일 먼저 삭제 후 DB 삭제 (프로젝트 정책에 따라 결정)
            archiveMapper.deleteArchive(archiveId);
            fileStorageService.deleteFile(dto.getFilePath());
            
        } catch (Exception e) {
            log.error("[ArchiveService] 삭제 실패: {}", dto.getFileName(), e);
            throw new RuntimeException("아카이브 삭제 실패", e);
        }
    }
}