package com.metanet.seoulbike.file.archive.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ArchiveDto {
    private Long archiveId;
    private String archiveTitle;
    private String archiveDesc;
    private String fileUuid;
    private String fileName;
    private String filePath;
    private long fileSize;
    private LocalDateTime uploadDate;
    private String uploaderId;
}