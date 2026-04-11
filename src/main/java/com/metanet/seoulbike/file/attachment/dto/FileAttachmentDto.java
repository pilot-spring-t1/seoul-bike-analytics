package com.metanet.seoulbike.file.attachment.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class FileAttachmentDto {
	private Long fileId;
	private Long boardId;
	private String fileUuid;
	private String fileName;
	private String filePath;
	private long fileSize;
	private LocalDateTime uploadDate;
	private String uploaderId;
}