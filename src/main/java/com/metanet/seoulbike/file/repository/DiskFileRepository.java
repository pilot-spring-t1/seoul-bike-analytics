package com.metanet.seoulbike.file.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Repository
public class DiskFileRepository implements IFileRepository {

    @Value("${file.storage.root-path}")
    private String rootPath;

    @Override
    public String save(MultipartFile file, String relativePath, String fileName) {
        Path dirPath = Paths.get(rootPath, relativePath);
        try {
            Files.createDirectories(dirPath);
            Path filePath = dirPath.resolve(fileName);
            file.transferTo(filePath);
            log.info("[DiskFile] 저장 완료: {}", filePath);
            return filePath.toString();
        } catch (IOException e) {
            log.error("[DiskFile] 저장 실패: {}", fileName, e);
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

    @Override
    public Resource load(String storedFilePath) {
        try {
            Path filePath = Paths.get(storedFilePath);
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("파일 읽기 불가");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new RuntimeException("파일 경로 오류", e);
        }
    }

    @Override
    public boolean exists(String storedFilePath) {
        return Files.exists(Paths.get(storedFilePath));
    }

    @Override
    public void delete(String storedFilePath) {
        try {
            Files.deleteIfExists(Paths.get(storedFilePath));
            log.info("[DiskFile] 삭제 완료: {}", storedFilePath);
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 실패", e);
        }
    }
}