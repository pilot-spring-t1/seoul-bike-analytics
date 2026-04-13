package com.metanet.seoulbike.common.log.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.metanet.seoulbike.common.log.dto.LogDto;
import com.metanet.seoulbike.common.log.dto.LogSearchDto;
import com.metanet.seoulbike.common.log.repository.ILogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LogService {

    // Mapper 대신 Interface 기반 Repository 주입
    private final ILogRepository logRepository;

    public Map<String, Object> getLogList(LogSearchDto searchDto) {
        Map<String, Object> result = new HashMap<>();
        searchDto.calculateOffset();

        // 저장소(Repository)에서 데이터 요청
        List<LogDto> list = logRepository.findAll(searchDto);
        int totalLogCount = logRepository.countAll(searchDto);

        int totalPages = (totalLogCount > 0) ? (int) Math.ceil((double) totalLogCount / searchDto.getSize()) : 1;

        // --- 페이지 블록 계산 (이부분은 순수 비즈니스 로직) ---
        int groupLimit = 5;
        int currentPage = searchDto.getPage();
        int groupNumber = (currentPage - 1) / groupLimit;

        int startPage = (groupNumber * groupLimit) + 1;
        int endPage = startPage + groupLimit - 1;

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

    public LogDto getLogById(Long logId) {
        return logRepository.findById(logId);
    }

    @Transactional
    public void createLog(LogDto logDto) {
        logRepository.save(logDto);
    }
}