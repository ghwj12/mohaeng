package org.poolpool.mohaeng.user.service;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.poolpool.mohaeng.auth.token.refresh.repository.RefreshTokenRepository;
import org.poolpool.mohaeng.common.config.UploadProperties;
import org.poolpool.mohaeng.common.util.FileNameChange;
import org.poolpool.mohaeng.user.dto.UserDto;
import org.poolpool.mohaeng.user.entity.UserEntity;
import org.poolpool.mohaeng.user.repository.SocialUserRepository;
import org.poolpool.mohaeng.user.repository.UserRepository;
import org.poolpool.mohaeng.user.type.UserStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UploadProperties uploadProperties;
	private final SocialUserRepository socialUserRepository;
	private final RefreshTokenRepository refreshTokenRepository;

	//이메일 중복 확인
	@Override
	public int existsByEmail(String email) {
		
		Optional<UserEntity> existUser = userRepository.findByEmail(email);
		if (existUser != null) {
			if(existUser.get().getUserStatus() != UserStatus.WITHDRAWAL) return 1;
		}
		return 0;
	}

	//일반 회원가입(개인/업체)
	@Override
	@Transactional
	public int insertUser(UserDto user) {
		String email = user.getEmail();
		
		// 탈퇴했던 회원일 경우 기존 데이터 업데이트를 위해 userId 조회 및 세팅
		if(userRepository.existsByEmail(email)) {
			Optional<UserEntity> existUser = userRepository.findByEmail(email);
			if (!existUser.isEmpty()) {
				user.setUserId(existUser.get().getUserId());
				user.setCreatedAt(LocalDateTime.now());
				user.setLastLoginAt(LocalDate.now());
			}
		}
		
		//사용자가 입력한 평문 비밀번호
	    String rawPassword = user.getUserPwd();

	    //BCrypt로 암호화
	    String encodedPassword = passwordEncoder.encode(rawPassword);

	    //DTO에 암호화된 비밀번호로 다시 세팅
	    user.setUserPwd(encodedPassword);
	    
	    //Entity 변환 후 저장	    
		return userRepository.save(user.toEntity()) != null ? 1 : 0;
	    
	}

	//이메일 찾기
	@Override
	public UserDto findByNameAndPhone(String name, String phone) {
		return UserDto.fromEntity(userRepository.findByNameAndPhone(name, phone));
	}

	//개인정보 조회
	@Override
	public UserDto findById(String userId) {
		return UserDto.fromEntity(
		        userRepository.findById(Long.valueOf(userId))
		                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."))
		);
	}

	//개인정보 수정
	@Override
	@Transactional
	public void patchUser(UserDto user, boolean deletePhoto, MultipartFile photo) {
		UserEntity updateUser = userRepository.findById(user.getUserId())
	            .orElseThrow(() -> new IllegalArgumentException("수정할 회원을 찾을 수 없습니다."));

	    // 비밀번호 변경
	    if (user.getUserPwd() != null && !user.getUserPwd().isBlank()) {
	    	updateUser.setUserPwd(passwordEncoder.encode(user.getUserPwd()));
	    }

	    // 프로필 사진 처리
	    boolean hasNewPhoto = (photo != null && !photo.isEmpty());
	    if (deletePhoto || hasNewPhoto) {
	        if (updateUser.getProfileImg() != null) {
	            File old = uploadProperties.photoDir().resolve(updateUser.getProfileImg()).toFile();
	            if (old.exists()) old.delete();
	        }
	        updateUser.setProfileImg(null);
	    }

	    if (hasNewPhoto) {
	        String original = photo.getOriginalFilename();
	        String rename = FileNameChange.change(original, FileNameChange.RenameStrategy.DATETIME_UUID);
	        File saveDir = uploadProperties.photoDir().toFile();
	        if (!saveDir.exists()) saveDir.mkdirs();
	        try {
				photo.transferTo(new File(saveDir, rename));
			} catch (Exception e) {
				throw new RuntimeException("사진 업로드 실패", e);
			} 
	        updateUser.setProfileImg(rename);
	    }

	    //전화번호 변경
	    if (user.getPhone() != null) updateUser.setPhone(user.getPhone());
		
	}

	//회원 탈퇴
	@Override
	@Transactional
	public void patchWithdrawal(UserDto user) {
		Long userId = user.getUserId();
		
		boolean socialYn = socialUserRepository.existsByUser_UserId(userId);
		if(socialYn) socialUserRepository.deleteByUser_UserId(userId);
		
		UserEntity updateUser = userRepository.findById(userId)
	            .orElseThrow(() -> new IllegalArgumentException("회원 없음"));
		
		updateUser.setUserStatus(UserStatus.WITHDRAWAL);
		updateUser.setWithReasonId(user.getWithReasonId());
		updateUser.setWithdrawalReason(user.getWithdrawalReason());
		
		refreshTokenRepository.deleteByUserId(userId);
		
	}
}
