package com.metanet.seoulbike.common.log.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LogDto {
    private Long logId;
    private String logLevel;
    private String methodName;
    private String requestUri;
    private String httpMethod;
    private Long executionTime;
    private String parameterData;
    private String errorMsg;
    private String accessIp;
    private String loginId;
    private LocalDateTime logDate;
}