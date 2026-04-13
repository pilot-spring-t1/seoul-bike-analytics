package com.metanet.seoulbike.common.log.dto;

import lombok.Data;

@Data
public class LogSearchDto {
	// 페이징 관련 필드
	private int page = 1; // 현재 페이지 번호 (기본값 1)
	private int size = 15; // 한 페이지당 출력할 로그 개수 (기본값 15)
	private int offset; // MyBatis 쿼리에서 건너뛸 행의 수 (Service에서 계산)

	// 검색 조건 필드
	private String searchType; // 검색 유형 (예: loginId, requestUri, methodName)
	private String keyword; // 검색어 (사용자 아이디나 URI 경로 등)
	private String logLevel; // 로그 레벨 필터 (INFO, ERROR)

	/**
	 * 페이징 처리를 위한 오프셋 계산 메서드 Service 계층에서 호출하여 쿼리 실행 전 offset 값을 세팅한다
	 */
	public void calculateOffset() {
		this.offset = (this.page - 1) * this.size;
	}
}