package com.metanet.seoulbike.file.attachment.repository;

import com.metanet.seoulbike.file.attachment.dto.FileAttachmentDto;
import java.util.List;

public interface IFileAttachmentRepository {
    // 파일 정보 저장
    void save(FileAttachmentDto dto);
    
    // 파일 단건 조회
    FileAttachmentDto findById(Long fileId);
    
    // 게시글별 파일 목록 조회
    List<FileAttachmentDto> findByBoardId(Long boardId);
    
    // 파일 정보 삭제
    void delete(Long fileId);
}