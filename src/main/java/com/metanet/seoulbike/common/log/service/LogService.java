package com.metanet.seoulbike.common.log.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.metanet.seoulbike.common.log.dto.LogDto;
import com.metanet.seoulbike.common.log.dto.LogSearchDto;
import com.metanet.seoulbike.common.log.mapper.LogMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 읽기 전용 모드
public class LogService {

	private final LogMapper logMapper;

	/**
	 * 시스템 로그 목록 조회 (페이징 및 검색 필터링)
	 * 
	 * @param searchDto 검색 조건 및 페이징 정보
	 * @return 로그 리스트, 전체 개수, 전체 페이지 수를 포함한 Map
	 */
	public Map<String, Object> selectLogList(LogSearchDto searchDto) {
		Map<String, Object> result = new HashMap<>();

		// 1. MyBatis 쿼리용 오프셋 계산 (LogSearchDto 내 메서드 활용 가능)
		searchDto.calculateOffset();

		// 2. 검색 조건에 따른 데이터 및 카운트 조회 (Mapper 명칭 일치)
		List<LogDto> list = logMapper.selectLogList(searchDto);
		int total = logMapper.selectLogCount(searchDto);

		// 3. 전체 페이지 수 계산
		int totalPages = (total > 0) ? (int) Math.ceil((double) total / searchDto.getSize()) : 1;

		// 4. 컨트롤러로 전달할 결과 구성
		result.put("list", list);
		result.put("total", total);
		result.put("totalPages", totalPages);

		return result;
	}

	/**
	 * 로그 상세 정보 조회 (상세 보기 팝업이나 모달용)
	 */
	public LogDto selectLogById(Long logId) {
		return logMapper.selectLogById(logId);
	}

	/**
	 * 로그 저장 (LogAspect에서 호출) 조회용 서비스와 분리하여 트랜잭션을 적용합니다.
	 */
	@Transactional
	public void insertLog(LogDto logDto) {
		logMapper.insertLog(logDto);
	}
}