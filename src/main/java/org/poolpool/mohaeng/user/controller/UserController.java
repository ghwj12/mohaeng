package org.poolpool.mohaeng.user.controller;

import org.poolpool.mohaeng.common.api.ApiResponse;
import org.poolpool.mohaeng.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	
	// 이메일 중복 확인
	@PostMapping("/checkId")
    public ResponseEntity<ApiResponse<String>> checkId(@RequestParam("email") String email) {
        int result = userService.existsByEmail(email);	// 0일 경우 사용 가능
        
        return ResponseEntity.ok(ApiResponse.ok("이메일 중복 확인 완료", (result == 0) ? "ok" : "dup"));
    }
}
