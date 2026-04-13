package com.metanet.seoulbike.file.archive.repository;

import com.metanet.seoulbike.file.archive.dto.ArchiveDto;
import com.metanet.seoulbike.file.archive.dto.ArchiveSearchDto;
import java.util.List;

public interface IArchiveRepository {
    // 아카이브 메타데이터 저장
    void save(ArchiveDto dto);
    
    // 목록 조회를 위한 카운트
    int countAll(ArchiveSearchDto searchDto);
    
    // 목록 조회
    List<ArchiveDto> findAll(ArchiveSearchDto searchDto);
    
    // 단건 상세 조회
    ArchiveDto findById(Long archiveId);
    
    // 아카이브 삭제
    void delete(Long archiveId);
}