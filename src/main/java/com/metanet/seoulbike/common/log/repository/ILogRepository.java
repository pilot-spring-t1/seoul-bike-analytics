package com.metanet.seoulbike.common.log.repository;

import com.metanet.seoulbike.common.log.dto.LogDto;
import com.metanet.seoulbike.common.log.dto.LogSearchDto;
import java.util.List;

public interface ILogRepository {
    // 로그 목록 조회
    List<LogDto> findAll(LogSearchDto searchDto);
    
    // 전체 로그 개수 조회
    int countAll(LogSearchDto searchDto);
    
    // 로그 상세 조회
    LogDto findById(Long logId);
    
    // 로그 저장
    void save(LogDto logDto);
}