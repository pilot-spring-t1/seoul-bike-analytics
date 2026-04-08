package com.metanet.seoulbike.test.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.metanet.seoulbike.test.dto.FileDto;

import java.util.List;

@Mapper
public interface FileMapper {
    // 파일 정보 저장 (INSERT)
    int insertFile(FileDto fileDto);

    // 전체 파일 목록 조회
    List<FileDto> findAllFiles();

    // 특정 파일 정보 조회
    FileDto findFileById(int fileId);
}