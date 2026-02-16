package org.poolpool.mohaeng.user.service;

import org.poolpool.mohaeng.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

	private final UserRepository userRepository;

	@Override
	public int existsByEmail(String email) {
		return userRepository.existsByEmail(email) == true ? 1 : 0;
	}
}
