package org.poolpool.mohaeng.user.repository;

import java.util.Optional;

import org.poolpool.mohaeng.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
	
	//이메일 중복 조회
	boolean existsByEmail(String email);

	//로그인 시 이메일로 회원 정보 조회
	Optional<UserEntity> findByEmail(String email);

	//이메일 찾기
	UserEntity findByNameAndPhone(String name, String phone);

	
	
}
