package com.metanet.seoulbike.test.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.metanet.seoulbike.mapper.SeoulBikeMapper;
import com.metanet.seoulbike.test.dto.FileDto;
import com.metanet.seoulbike.test.dto.SeoulBikeDto;
import com.metanet.seoulbike.test.mapper.FileMapper;

@RestController
@RequestMapping("/test/file")
public class FileTestController {

	@Value("${file.upload-dir}")
	private String uploadDir; // application.properties의 "./uploads/" 값을 가져옴

	@Autowired
	private FileMapper fileMapper;
	
	@Autowired
	private SeoulBikeMapper seoulBikeMapper;

	@PostMapping("/upload")
	public String uploadFile(@RequestParam("file") MultipartFile file,
			@RequestParam(value = "uploader", defaultValue = "SYSTEM") String uploader) {
		if (file.isEmpty())
			return "파일이 비어있습니다.";

		try {
			// 1. 상대 경로를 절대 경로로 변환 (OS 독립적인 물리 경로 확보)
			File folder = new File(uploadDir);
			if (!folder.exists()) {
				folder.mkdirs(); // 폴더가 없으면 생성
			}
			String absolutePath = folder.getAbsolutePath(); // 실제 서버상의 전체 경로 추출

			// 2. 파일명 처리 (UUID 생성)
			String orgName = file.getOriginalFilename();
			String uuid = UUID.randomUUID().toString();
			String saveName = uuid + "_" + orgName;

			// File.separator를 사용하여 윈도우(\)와 맥/리눅스(/) 모두 대응
			File targetFile = new File(absolutePath + File.separator + saveName);

			// 3. 로컬 저장소에 실제 파일 쓰기
			file.transferTo(targetFile);

			// 4. DB 저장을 위한 DTO 세팅
			FileDto fileDto = new FileDto();
			fileDto.setFileUuid(uuid);
			fileDto.setFileName(orgName);
			// DB에는 나중에 찾기 쉽게 절대 경로를 저장하거나, 관리 규칙에 따른 경로 저장
			fileDto.setFilePath(absolutePath + File.separator);
			fileDto.setFileSize(file.getSize());
			fileDto.setUploaderId(uploader);

			// 5. DB INSERT
			int result = fileMapper.insertFile(fileDto);

			return result > 0 ? "업로드 성공! [ID: " + fileDto.getFileId() + ", 경로: " + absolutePath + "]" : "DB 기록 실패";

		} catch (IOException e) {
			return "파일 시스템 오류 (경로 권한 등 확인): " + e.getMessage();
		} catch (Exception e) {
			return "기타 오류: " + e.getMessage();
		}
	}

	@PostMapping("/refresh-db/{fileId}")
	public String refreshDbFromFile(@PathVariable int fileId) {
		// 1. DB에서 파일 정보 조회
		FileDto fileInfo = fileMapper.findFileById(fileId);
		if (fileInfo == null)
			return "해당 파일 정보를 찾을 수 없습니다.";

		// 2. 물리 파일 경로 확인
		String fullPath = fileInfo.getFilePath() + fileInfo.getFileUuid() + "_" + fileInfo.getFileName();
		File csvFile = new File(fullPath);
		if (!csvFile.exists())
			return "서비에 물리 파일이 존재하지 않습니다.";

		// 3. 파싱 및 Batch Insert 실행
		try {
			long startTime = System.currentTimeMillis();
			int totalRows = parseAndInsertCsv(csvFile);
			long endTime = System.currentTimeMillis();

			return String.format("DB 갱신 완료! 총 %d건 삽입 (소요시간: %dms)", totalRows, (endTime - startTime));
		} catch (Exception e) {
			return "데이터 갱신 실패: " + e.getMessage();
		}
	}
	
	private int parseAndInsertCsv(File file) throws Exception {
        int count = 0;
        // 공공데이터 CSV는 보통 EUC-KR이 많으므로 인코딩 주의
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "EUC-KR"))) {
            List<SeoulBikeDto> batchList = new ArrayList<>();
            String line;
            br.readLine(); // 헤더(컬럼명) 한 줄 건너뛰기

            while ((line = br.readLine()) != null) {
                String[] t = line.split(",");
                if (t.length < 11) continue; // 데이터가 온전하지 않으면 스킵

                SeoulBikeDto dto = new SeoulBikeDto();
                dto.setRentalDate(t[0].trim());
                dto.setRentalOfficeNo(t[1].trim());
                dto.setRentalOfficeName(t[2].trim());
                dto.setRentalCode(t[3].trim());
                dto.setGender(t[4].trim());
                dto.setAgeGroup(t[5].trim());
                dto.setNumOfUses(Integer.parseInt(t[6].trim()));
                dto.setExerciseAmount(Double.parseDouble(t[7].trim()));
                dto.setCarbonAmount(Double.parseDouble(t[8].trim()));
                dto.setDistance(Double.parseDouble(t[9].trim()));
                dto.setUsageMinute(Integer.parseInt(t[10].trim()));

                batchList.add(dto);
                count++;

                // 1,000건 단위로 묶어서 DB 전송 (성능 최적화)
                if (batchList.size() >= 1000) {
                    seoulBikeMapper.insertBikeBatch(batchList);
                    batchList.clear();
                }
            }
            // 남은 데이터 최종 삽입
            if (!batchList.isEmpty()) {
            	seoulBikeMapper.insertBikeBatch(batchList);
            }
        }
        return count;
    }
}