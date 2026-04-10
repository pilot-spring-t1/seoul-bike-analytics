package com.metanet.seoulbike.common.log.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.metanet.seoulbike.common.log.dto.LogDto;
import com.metanet.seoulbike.common.log.dto.LogSearchDto;

@Mapper
public interface LogMapper {

	// 1. 로그 기록 저장 (Aspect에서 사용)
	void insertLog(LogDto logDto);

	// 2. 로그 목록 검색 조회 (관리자 페이지용)
	List<LogDto> selectLogList(LogSearchDto searchDto);

	// 3. 전체 로그 개수 조회 (페이징 계산용)
	int selectLogCount(LogSearchDto searchDto);

	// 4. 로그 상세 정보 조회 (상세 보기용)
	LogDto selectLogById(Long logId);
}