package com.metanet.seoulbike.file.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.metanet.seoulbike.file.dto.FileDto;

@Mapper
public interface FileMapper {
    // 1. 파일 정보 저장
    int insertFile(FileDto fileDto);

    // 2. 특정 게시글에 속한 파일 목록 조회
    List<FileDto> getFilesByBoardId(Long boardId);

    // 3. 파일 상세 정보 조회 (다운로드용)
    FileDto getFileById(Long fileId);

    // 4. 파일 삭제
    int deleteFile(Long fileId);
}