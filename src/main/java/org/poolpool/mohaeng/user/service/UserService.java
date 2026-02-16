package org.poolpool.mohaeng.user.service;

public interface UserService {

	//이메일 중복 확인
	int existsByEmail(String email);
}
