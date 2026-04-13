package com.metanet.seoulbike.common.log.repository;

import com.metanet.seoulbike.common.log.dto.LogDto;
import com.metanet.seoulbike.common.log.dto.LogSearchDto;
import com.metanet.seoulbike.common.log.mapper.LogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LogRepository implements ILogRepository {

    private final LogMapper logMapper;

    @Override
    public List<LogDto> findAll(LogSearchDto searchDto) {
        return logMapper.selectLogList(searchDto);
    }

    @Override
    public int countAll(LogSearchDto searchDto) {
        return logMapper.selectLogCount(searchDto);
    }

    @Override
    public LogDto findById(Long logId) {
        return logMapper.selectLogById(logId);
    }

    @Override
    public void save(LogDto logDto) {
        logMapper.insertLog(logDto);
    }
}