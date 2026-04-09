package com.metanet.seoulbike.common.log.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.metanet.seoulbike.common.log.dto.LogDto;
import com.metanet.seoulbike.common.log.mapper.LogMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final LogMapper logMapper;

    @Around("execution(* com.metanet.seoulbike..controller..*.*(..))")
    public Object recordLog(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attr.getRequest();
        
        LogDto dto = new LogDto();
        dto.setRequestUri(request.getRequestURI());
        dto.setHttpMethod(request.getMethod());
        dto.setMethodName(joinPoint.getSignature().getName());
        dto.setAccessIp(request.getRemoteAddr());
        
        try {
            Object result = joinPoint.proceed();
            dto.setLogLevel("INFO");
            return result;
        } catch (Exception e) {
            dto.setLogLevel("ERROR");
            dto.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            dto.setExecutionTime(System.currentTimeMillis() - start);
            logMapper.insertLog(dto);
        }
    }
}