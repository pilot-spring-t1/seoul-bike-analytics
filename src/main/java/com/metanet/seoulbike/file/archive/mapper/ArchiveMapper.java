package com.metanet.seoulbike.file.archive.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.metanet.seoulbike.file.archive.dto.ArchiveDto;
import com.metanet.seoulbike.file.archive.dto.ArchiveSearchDto;

@Mapper
public interface ArchiveMapper {
    int insertArchive(ArchiveDto dto);

    // 검색 및 페이징 포함 목록
    List<ArchiveDto> selectArchiveList(ArchiveSearchDto searchDto);

    // 검색된 결과의 총 개수 (페이징 계산용)
    int selectArchiveCount(ArchiveSearchDto searchDto);

    ArchiveDto selectArchiveById(Long archiveId);
    int deleteArchive(Long archiveId);
}