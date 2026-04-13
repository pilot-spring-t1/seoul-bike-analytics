package com.metanet.seoulbike.file.service;

import com.metanet.seoulbike.file.repository.IFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final IFileRepository fileRepository;

    public String uploadFile(MultipartFile file, String relativePath, String fileName) {
        return fileRepository.save(file, relativePath, fileName);
    }

    public Resource loadFile(String storedFilePath) {
        return fileRepository.load(storedFilePath);
    }

    public boolean existsFile(String storedFilePath) {
        return fileRepository.exists(storedFilePath);
    }

    public void deleteFile(String storedFilePath) {
        fileRepository.delete(storedFilePath);
    }
}