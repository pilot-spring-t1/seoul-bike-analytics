package com.metanet.seoulbike.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FileDto {
    private int fileId;           // FILE_ID (PK)
    private String fileUuid;      // FILE_UUID (고유이름)
    private String fileName;      // FILE_NAME (원본이름)
    private String filePath;      // FILE_PATH (저장경로)
    private long fileSize;        // FILE_SIZE (파일크기, 대용량 대비 long 권장)
    private LocalDateTime uploadDate; // UPLOAD_DATE (등록일)
    private String uploaderId;    // UPLOADER_ID (등록자)
}