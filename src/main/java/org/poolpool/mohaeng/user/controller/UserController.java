package org.poolpool.mohaeng.user.controller;

import org.poolpool.mohaeng.common.api.ApiResponse;
import org.poolpool.mohaeng.user.dto.UserDto;
import org.poolpool.mohaeng.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
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
	
	//이메일 중복 확인
	@PostMapping("/checkId")
    public ResponseEntity<ApiResponse<String>> checkId(@RequestParam("email") String email) {
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
}
