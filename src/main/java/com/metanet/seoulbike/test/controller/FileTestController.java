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
			return "서버에 물리 파일이 존재하지 않습니다.";

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
	    // 공공데이터 특성상 EUC-KR 인코딩 사용
	    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "EUC-KR"))) {
	        List<SeoulBikeDto> batchList = new ArrayList<>();
	        String line;
	        br.readLine(); // CSV 헤더 스킵

	        while ((line = br.readLine()) != null) {
	            // 1. 단순 split 대신, 따옴표 안의 쉼표는 무시하는 정규식 사용 (인덱스 밀림 방지)
	            String[] t = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
	            
	            // 데이터가 부족한 행은 스킵
	            if (t.length < 11) continue; 
	            
	            // 2. 모든 요소의 쌍따옴표 및 앞뒤 공백 일괄 제거
	            for (int i = 0; i < t.length; i++) {
	                if (t[i] != null) {
	                    t[i] = t[i].replace("\"", "").trim();
	                }
	            }

	            try {
	                SeoulBikeDto dto = new SeoulBikeDto();
	                // 문자열 데이터 세팅 (t[0]~t[5])
	                dto.setRentalDate(t[0]);
	                dto.setRentalOfficeNo(t[1]);
	                dto.setRentalOfficeName(t[2]);
	                dto.setRentalCode(t[3]);
	                dto.setGender(t[4]);
	                dto.setAgeGroup(t[5]); // 여기서 "20대"가 정상 저장됨

	                // 3. 숫자 데이터 파싱 (인덱스 t[6]~t[10])
	                // 데이터가 비어있거나(\N 등) 형식이 다를 경우를 위해 trim() 필수
	                dto.setNumOfUses(Integer.parseInt(t[6].isEmpty() ? "0" : t[6]));
	                dto.setExerciseAmount(Double.parseDouble(t[7].isEmpty() ? "0" : t[7]));
	                dto.setCarbonAmount(Double.parseDouble(t[8].isEmpty() ? "0" : t[8]));
	                dto.setDistance(Double.parseDouble(t[9].isEmpty() ? "0" : t[9]));
	                dto.setUsageMinute(Integer.parseInt(t[10].isEmpty() ? "0" : t[10]));

	                batchList.add(dto);
	                count++;
	            } catch (NumberFormatException e) {
	                // 특정 행의 숫자 파싱 에러 시 해당 행만 스킵하고 로그 출력 (전체 중단 방지)
	                System.err.println("[Row " + count + "] 파싱 실패: " + e.getMessage());
	                continue;
	            }

	            // 4. 1,000건 단위 Batch Insert (성능 최적화)
	            if (batchList.size() >= 1000) {
	                seoulBikeMapper.insertBikeBatch(batchList);
	                batchList.clear();
	            }
	        }
	        // 남은 잔여 데이터 처리
	        if (!batchList.isEmpty()) {
	            seoulBikeMapper.insertBikeBatch(batchList);
	        }
	    }
	    return count;
	}
}