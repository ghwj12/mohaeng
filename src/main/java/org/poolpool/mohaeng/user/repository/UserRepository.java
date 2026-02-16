package org.poolpool.mohaeng.user.repository;

import java.util.Optional;

import org.poolpool.mohaeng.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
	boolean existsByEmail(String email);

	Optional<UserEntity> findByEmail(String email);
}
