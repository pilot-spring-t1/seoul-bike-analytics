package com.metanet.seoulbike.file.archive.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ArchiveSearchDto {
    private int page = 1;           // 현재 페이지 번호
    private int recordSize = 10;     // 페이지당 출력할 레코드 수
    private String keyword;         // 검색어

    // Oracle OFFSET 계산 (시작 위치)
    public int getOffset() {
        return (page - 1) * recordSize;
    }
}