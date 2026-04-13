package com.metanet.seoulbike;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@MapperScan(basePackages = "com.metanet.seoulbike", annotationClass = org.apache.ibatis.annotations.Mapper.class)
public class SeoulBikeAnalyticsApplication {
	public static void main(String[] args) {
		SpringApplication.run(SeoulBikeAnalyticsApplication.class, args);
	}

}
