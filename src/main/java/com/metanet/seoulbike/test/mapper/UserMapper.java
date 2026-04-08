package com.metanet.seoulbike.test.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.metanet.seoulbike.test.dto.UserDto;

@Mapper
public interface UserMapper {
	int checkDbConnection(); // DB 연결 확인용 숫자 반환
	List<UserDto> findAllUsers(); // 전체 유저 조회
	
	UserDto findUserById(String userId);     // 특정 아이디로 조회
    int insertUser(UserDto userDto);         // 회원 가입 (삽입)
    int updateUser(UserDto userDto);         // 정보 수정
    int deleteUser(String userId);           // 회원 탈퇴 (삭제)
}