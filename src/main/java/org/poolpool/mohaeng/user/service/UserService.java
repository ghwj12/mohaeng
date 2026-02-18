package org.poolpool.mohaeng.user.service;

import org.poolpool.mohaeng.user.dto.UserDto;

public interface UserService {

	//이메일 중복 확인
	int existsByEmail(String email);
	
	//일반 회원가입(개인/업체)
	int insertUser(UserDto user);

	//이메일 찾기
	UserDto findByNameAndPhone(String name, String phone);

	//개인정보 조회
	UserDto selectUser(String userId);
	
	
}
