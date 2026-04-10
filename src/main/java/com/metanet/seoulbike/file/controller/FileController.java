package com.metanet.seoulbike.file.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

import com.metanet.seoulbike.file.dto.FileDto;
import com.metanet.seoulbike.file.service.FileService;

@RestController
public class FileController {

	@Autowired
	private FileService fileService;

	@GetMapping("/download/{fileId}")
	public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) throws IOException {

		// Service에서 검증된 정보를 가져옴 (실물 없으면 여기서 Exception 발생)
		FileDto fileDto = fileService.getFileById(fileId);

		Path path = Paths.get(fileDto.getFilePath(), fileDto.getFileUuid()).normalize();
		Resource resource = new UrlResource(path.toUri());

		String encodedFileName = UriUtils.encode(fileDto.getFileName(), StandardCharsets.UTF_8);
		String contentType = Files.probeContentType(path);
		if (contentType == null)
			contentType = "application/octet-stream";

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
				.header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileDto.getFileSize())).body(resource);
	}
}