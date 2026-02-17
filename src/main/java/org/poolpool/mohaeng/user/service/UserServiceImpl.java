package org.poolpool.mohaeng.user.service;

import org.poolpool.mohaeng.user.dto.UserDto;
import org.poolpool.mohaeng.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	//이메일 중복 확인
	@Override
	public int existsByEmail(String email) {
		return userRepository.existsByEmail(email) == true ? 1 : 0;
	}

	//일반 회원가입(개인/업체)
	@Override
	@Transactional
	public int insertUser(UserDto user) {
		//사용자가 입력한 평문 비밀번호
	    String rawPassword = user.getUserPwd();

	    //BCrypt로 암호화
	    String encodedPassword = passwordEncoder.encode(rawPassword);

	    //DTO에 암호화된 비밀번호로 다시 세팅
	    user.setUserPwd(encodedPassword);
	    
	    //Entity 변환 후 저장	    
		return userRepository.save(user.toEntity()) != null ? 1 : 0;
	    
	}
}
