package org.poolpool.mohaeng.user.controller;

import org.poolpool.mohaeng.common.api.ApiResponse;
import org.poolpool.mohaeng.user.dto.UserDto;
import org.poolpool.mohaeng.user.service.UserService;
import org.poolpool.mohaeng.user.type.SignupType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ApiResponse<String>> checkEmail(@RequestParam("email") String email) {
        int result = userService.existsByEmail(email);	// 0일 경우 사용 가능
        
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
    public ResponseEntity<ApiResponse<String>> findEmail(@RequestParam("name") String name, @RequestParam("phone") String phone) {
		UserDto user = userService.findByNameAndPhone(name, phone);
		if (user == null) {
            return ResponseEntity.status(404).body(ApiResponse.fail("회원 정보를 찾을 수 없습니다.", null));
        } else if(user.getSignupType() == SignupType.GOOGLE) {
        	return ResponseEntity.status(404).body(ApiResponse.fail("Social User", "구글 계정으로 가입하셨습니다."));
        }
        return ResponseEntity.ok(ApiResponse.ok("이메일 찾기 성공!", user.getEmail()));
	}
	
	//개인정보 조회
	@GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDto>> getProfile(@PathVariable("userId") String userId) {
		UserDto user = userService.selectUser(userId);
        if (user == null) {
            return ResponseEntity.status(404).body(ApiResponse.fail("회원 정보를 찾을 수 없습니다.", null));
        }
        return ResponseEntity.ok(ApiResponse.ok("회원 조회 성공", user));
    }
	
	
}
