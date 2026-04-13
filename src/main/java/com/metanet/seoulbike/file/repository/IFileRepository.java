package com.metanet.seoulbike.file.repository;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IFileRepository {
    // 파일 물리 저장
    String save(MultipartFile file, String relativePath, String fileName);
    
    // 파일 물리 조회(로드)
    Resource load(String storedFilePath);
    
    // 파일 물리 존재 여부 확인
    boolean exists(String storedFilePath);
    
    // 파일 물리 삭제
    void delete(String storedFilePath);
}