package com.metanet.seoulbike.file.attachment.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.metanet.seoulbike.file.attachment.dto.FileAttachmentDto;

@Mapper
public interface FileAttachmentMapper {

    // 파일 등록
    int insertFile(FileAttachmentDto dto);

    // 게시글별 파일 목록 조회
    List<FileAttachmentDto> selectFilesByBoardId(Long boardId);

    // 파일 단건 조회
    FileAttachmentDto selectFileById(Long fileId);

    // 파일 삭제
    int deleteFile(Long fileId);
}