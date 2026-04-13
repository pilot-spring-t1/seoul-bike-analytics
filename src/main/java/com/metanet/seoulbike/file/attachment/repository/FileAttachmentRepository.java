package com.metanet.seoulbike.file.attachment.repository;

import com.metanet.seoulbike.file.attachment.dto.FileAttachmentDto;
import com.metanet.seoulbike.file.attachment.mapper.FileAttachmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FileAttachmentRepository implements IFileAttachmentRepository {

    private final FileAttachmentMapper fileAttachmentMapper;

    @Override
    public void save(FileAttachmentDto dto) {
        fileAttachmentMapper.insertFile(dto);
    }

    @Override
    public FileAttachmentDto findById(Long fileId) {
        return fileAttachmentMapper.selectFileById(fileId);
    }

    @Override
    public List<FileAttachmentDto> findByBoardId(Long boardId) {
        return fileAttachmentMapper.selectFilesByBoardId(boardId);
    }

    @Override
    public void delete(Long fileId) {
        fileAttachmentMapper.deleteFile(fileId);
    }
}