package com.metanet.seoulbike.mapper;

import com.metanet.seoulbike.dto.SeoulBikeDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface SeoulBikeMapper {
    
    // 대량의 데이터를 한 번에 넣기 위한 배치 인서트
    int insertBikeBatch(List<SeoulBikeDto> bikeList);

    // 테스트용: 데이터가 잘 들어갔는지 확인하기 위한 전체 조회
    List<SeoulBikeDto> findAllBikeData();

    // 특정 대여소의 데이터만 조회 (분석용 예시)
    List<SeoulBikeDto> findByOfficeName(String officeName);
    
    // 테이블 비우기 (테스트 시 유용)
    void truncateTestTable();
}