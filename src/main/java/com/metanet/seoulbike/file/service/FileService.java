package com.metanet.seoulbike.file.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.metanet.seoulbike.file.dto.FileDto;
import com.metanet.seoulbike.file.mapper.FileMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    private FileMapper fileMapper;

    /**
     * 1. 파일 업로드 (UUID.확장자 방식으로 저장)
     */
    @Transactional(rollbackFor = Exception.class)
    public FileDto uploadFile(MultipartFile file, String uploaderId, Long boardId) throws IOException {
        if (file == null || file.isEmpty()) return null;

        // 폴더 생성
        File folder = new File(uploadDir);
        if (!folder.exists()) folder.mkdirs();
        String absolutePath = folder.getAbsolutePath();

        // 확장자 추출 및 저장 파일명 생성
        String orgName = file.getOriginalFilename();
        String extension = "";
        if (orgName != null && orgName.contains(".")) {
            extension = orgName.substring(orgName.lastIndexOf("."));
        }
        
        // 실제 서버 저장명: UUID + .확장자 (예: 654f1fe0... .csv)
        String saveName = UUID.randomUUID().toString() + extension;
        
        File targetFile = new File(absolutePath, saveName);

        try {
            // [실행] 물리 파일 저장 (대용량 전송)
            file.transferTo(targetFile);
            
            // [기록] DB 저장
            FileDto fileDto = new FileDto();
            fileDto.setBoardId(boardId);
            // 중요: DB의 FILE_UUID 컬럼에 확장자가 포함된 saveName을 통째로 넣습니다.
            fileDto.setFileUuid(saveName); 
            fileDto.setFileName(orgName);
            fileDto.setFilePath(absolutePath);
            fileDto.setFileSize(file.getSize());
            fileDto.setUploaderId(uploaderId);

            fileMapper.insertFile(fileDto);
            log.info("파일 업로드 성공: {} -> {}", orgName, saveName);
            return fileDto;

        } catch (IOException e) {
            log.error("물리 파일 저장 중 에러 발생 (롤백 진행): {}", orgName);
            throw e; 
        }
    }

    /**
     * 2. 파일 정보 조회 (DB + 실물 검증)
     */
    @Transactional(readOnly = true)
    public FileDto getFileById(Long fileId) throws FileNotFoundException {
        FileDto fileDto = fileMapper.getFileById(fileId);
        if (fileDto == null) {
            throw new FileNotFoundException("DB에 파일 정보가 없습니다. ID: " + fileId);
        }

        // 경로 조합 (저장명 자체가 fileUuid에 들어있으므로 Paths.get이 정확히 파일을 가리킴)
        Path path = Paths.get(fileDto.getFilePath(), fileDto.getFileUuid()).normalize();
        File file = path.toFile();

        if (!file.exists() || !file.canRead()) {
            log.error("파일 실물 누락: {}", file.getAbsolutePath());
            throw new FileNotFoundException("서버에서 물리 파일을 찾을 수 없습니다.");
        }

        return fileDto;
    }

    /**
     * 3. 파일 삭제
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(Long fileId) {
        FileDto fileDto = fileMapper.getFileById(fileId);
        if (fileDto == null) return;

        Path path = Paths.get(fileDto.getFilePath(), fileDto.getFileUuid()).normalize();
        File file = path.toFile();

        if (file.exists()) {
            if (file.delete()) {
                log.info("물리 파일 삭제 완료: {}", file.getAbsolutePath());
            } else {
                log.error("물리 파일 삭제 실패: {}", file.getAbsolutePath());
            }
        }
        fileMapper.deleteFile(fileId);
    }
}