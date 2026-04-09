package com.metanet.seoulbike.stats.dto;

import lombok.Data;

@Data
public class SeoulBikeDto {
	private String rentalDate; // 날짜는 우선 String으로 읽어서 처리
	private String rentalOfficeNo;
	private String rentalOfficeName;
	private String rentalCode;
	private String gender;
	private String ageGroup;
	private int numOfUses;
	private double exerciseAmount;
	private double carbonAmount;
	private double distance;
	private int usageMinute;
}