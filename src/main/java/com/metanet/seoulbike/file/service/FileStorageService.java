package com.metanet.seoulbike.file.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileStorageService {

    @Value("${file.storage.root-path}")
    private String rootPath;

    // 첨부파일용 (MultipartFile → 디스크)
    public String uploadFile(MultipartFile file, String relativePath, String fileName) {
        Path dirPath = Paths.get(rootPath, relativePath);
        try {
            Files.createDirectories(dirPath);
            Path filePath = dirPath.resolve(fileName);
            file.transferTo(filePath);
            log.info("[FileStorage] 저장 완료: {}", filePath);
            return filePath.toString();
        } catch (IOException e) {
            log.error("[FileStorage] 저장 실패: {}", fileName, e);
            throw new RuntimeException("파일 저장 실패: " + fileName, e);
        }
    }

    // 아카이브용 (byte[] → 디스크)
    public String writeFile(byte[] data, String relativePath, String fileName) {
        Path dirPath = Paths.get(rootPath, relativePath);
        try {
            Files.createDirectories(dirPath);
            Path filePath = dirPath.resolve(fileName);
            Files.write(filePath, data);
            log.info("[FileStorage] 저장 완료: {}", filePath);
            return filePath.toString();
        } catch (IOException e) {
            log.error("[FileStorage] 저장 실패: {}", fileName, e);
            throw new RuntimeException("파일 저장 실패: " + fileName, e);
        }
    }

    // 다운로드용
    public Resource loadFile(String storedFilePath) {
        try {
            Path filePath = Paths.get(storedFilePath);
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                log.warn("[FileStorage] 파일 없음 또는 읽기 불가: {}", storedFilePath);
                throw new RuntimeException("파일을 찾을 수 없습니다: " + storedFilePath);
            }
            return resource;
        } catch (MalformedURLException e) {
            log.error("[FileStorage] 경로 오류: {}", storedFilePath, e);
            throw new RuntimeException("파일 경로 오류: " + storedFilePath, e);
        }
    }

    // 물리 파일 존재 여부 확인
    public boolean existsFile(String storedFilePath) {
        return Files.exists(Paths.get(storedFilePath));
    }

    // 삭제
    public void deleteFile(String storedFilePath) {
        try {
            boolean deleted = Files.deleteIfExists(Paths.get(storedFilePath));
            if (deleted) {
                log.info("[FileStorage] 삭제 완료: {}", storedFilePath);
            } else {
                log.warn("[FileStorage] 삭제 대상 없음 (이미 없는 파일): {}", storedFilePath);
            }
        } catch (IOException e) {
            log.error("[FileStorage] 삭제 실패: {}", storedFilePath, e);
            throw new RuntimeException("파일 삭제 실패: " + storedFilePath, e);
        }
    }
}