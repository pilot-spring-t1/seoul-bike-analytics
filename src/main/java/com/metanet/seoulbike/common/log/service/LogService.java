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
@Transactional(readOnly = true)
public class LogService {

	private final LogMapper logMapper;

	/**
	 * 시스템 로그 목록 조회 (페이징 및 검색 필터링)
	 * 
	 * @param searchDto 검색 조건 및 페이징 정보
	 * @return 로그 리스트, 전체 개수, 전체 페이지 수를 포함한 Map
	 */
	public Map<String, Object> getLogList(LogSearchDto searchDto) {
	    Map<String, Object> result = new HashMap<>();
	    searchDto.calculateOffset();

	    List<LogDto> list = logMapper.selectLogList(searchDto);
	    int totalLogCount = logMapper.selectLogCount(searchDto);

	    int totalPages;

	    if (totalLogCount > 0) {
	        // 로그 데이터가 1개라도 있는 경우: 올림 계산
	        totalPages = (int) Math.ceil((double) totalLogCount / searchDto.getSize());
	    } else {
	        // 로그 데이터가 하나도 없는 경우: 기본적으로 1페이지로 설정
	        totalPages = 1;
	    }

	    // --- 페이지 블록 계산  ---
	    int groupLimit = 5;
	    int currentPage = searchDto.getPage();

	    // 현재 페이지가 몇 번째 그룹인지 계산 (1~5페이지는 0그룹, 6~10페이지는 1그룹)
	    // currentPage - 1 을 함으로써 0-based index 로 바꾼다.
	    int groupNumber = (currentPage - 1) / groupLimit;

	    // 그룹 번호를 바탕으로 startPage 결정
	    int startPage = (groupNumber * groupLimit) + 1;
	    
	    // startPage를 바탕으로 endPage 결정
	    int endPage = startPage + groupLimit - 1;

	    // endPage 가 전체 페이지보다 크다면, 전체 페이지 번호로 제한	
	    if (endPage > totalPages) {
	        endPage = totalPages;
	    }

	    result.put("list", list);
	    result.put("total", totalLogCount);
	    result.put("totalPages", totalPages);
	    result.put("startPage", startPage);
	    result.put("endPage", endPage);

	    return result;
	}

	/**
	 * 로그 상세 정보 조회
	 */
	public LogDto getLogById(Long logId) {
		return logMapper.selectLogById(logId);
	}

	/**
	 * 로그 저장
	 */
	@Transactional
	public void insertLog(LogDto logDto) {
		logMapper.insertLog(logDto);
	}
}