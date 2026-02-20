package org.poolpool.mohaeng.user.controller;

import org.poolpool.mohaeng.auth.dto.request.LoginRequest;
import org.poolpool.mohaeng.common.api.ApiResponse;
import org.poolpool.mohaeng.user.dto.UserDto;
import org.poolpool.mohaeng.user.service.UserService;
import org.poolpool.mohaeng.user.type.SignupType;
import org.poolpool.mohaeng.user.type.UserStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	
	//이메일(아이디) 중복 확인
	@PostMapping("/checkId")
    public ResponseEntity<ApiResponse<String>> checkEmail(@RequestBody LoginRequest req) {
        int result = userService.existsByEmail(req.userId());	// 0일 경우 사용 가능
        
        return ResponseEntity.ok(ApiResponse.ok("이메일 중복 확인 완료", (result == 0) ? "ok" : "dup"));
    }
	
	//일반 회원가입(개인/업체)
	@PostMapping("/createUser")
    public ResponseEntity<ApiResponse<Void>> signUp(
    		@ModelAttribute @Valid UserDto user) {

		int result = userService.insertUser(user);
        
        if (result > 0) {
            return ResponseEntity.status(201).body(ApiResponse.ok("회원 가입 성공", null));
        }
        return ResponseEntity.status(500).body(ApiResponse.fail("회원 가입 실패", null));
    }
	
	//이메일(아이디) 찾기
	@PostMapping("/searchId")
    public ResponseEntity<ApiResponse<String>> findEmail(@RequestBody LoginRequest req) {
		UserDto user = userService.findByNameAndPhone(req.name(), req.phone());
		if (user == null) {
            return ResponseEntity.status(404).body(ApiResponse.fail("회원 정보를 찾을 수 없습니다.", null));
        } else if(user.getSignupType() == SignupType.GOOGLE) {
        	return ResponseEntity.status(404).body(ApiResponse.fail("Social User", "구글 계정과 연동하여 가입되어 있습니다."));
        } else if(user.getUserStatus() == UserStatus.WITHDRAWAL) {
        	return ResponseEntity.status(404).body(ApiResponse.fail("Withdrawal User", "탈퇴된 계정입니다."));
        }
        return ResponseEntity.ok(ApiResponse.ok("이메일 찾기 성공!", user.getEmail()));
	}
	
	//개인정보 조회
	@GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDto>> getProfile(@PathVariable("userId") String userId) {
		UserDto user = userService.findById(userId);
        if (user == null) {
            return ResponseEntity.status(404).body(ApiResponse.fail("회원 정보를 찾을 수 없습니다.", null));
        }
        return ResponseEntity.ok(ApiResponse.ok("회원 조회 성공", user));
    }
	
	//개인정보 수정
	@PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @PathVariable("userId") String userId,
            @ModelAttribute UserDto user,
            @RequestParam(name = "deletePhoto", defaultValue = "false") boolean deletePhoto,
            @RequestParam(name = "newPhoto", required = false) MultipartFile photo
    ) {
        user.setUserId(Long.valueOf(userId));
        
        try {
            userService.patchUser(user, deletePhoto, photo);
            return ResponseEntity.ok(ApiResponse.ok("회원 정보 수정 성공", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(ApiResponse.fail(e.getMessage(), null));
        } catch (RuntimeException e) {
            log.error("회원 사진 업로드 실패", e);
            return ResponseEntity.status(500).body(ApiResponse.fail(e.getMessage(), null));
        } catch (Exception e) {
            log.error("회원 정보 수정 실패", e);
            return ResponseEntity.status(500).body(ApiResponse.fail("회원 정보 수정 실패", null));
        }

    }
	
	//회원 탈퇴
	@PatchMapping(value = "/withdrawal")
	public ResponseEntity<ApiResponse<Void>> withdrawal(@ModelAttribute UserDto user) {

		userService.patchWithdrawal(user);
        
		return ResponseEntity.ok(ApiResponse.ok("회원 탈퇴 성공", null));
    }
}
