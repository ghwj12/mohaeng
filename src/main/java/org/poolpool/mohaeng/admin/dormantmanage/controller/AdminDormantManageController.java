package org.poolpool.mohaeng.admin.dormantmanage.controller;

import org.poolpool.mohaeng.admin.dormantmanage.dto.DormantUserDto;
import org.poolpool.mohaeng.admin.dormantmanage.service.AdminDormantManageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/admin/dormantmanage")
@RequiredArgsConstructor
public class AdminDormantManageController {

	private final AdminDormantManageService adminDormantManageService;
	
	//휴면 계정 관리 조회
	@GetMapping("/getDormantUsers")
	public ResponseEntity<Page<DormantUserDto>> getDormantUsers(@PageableDefault(size = 10) Pageable pageable) {
		//휴면 계정 관리 프로시저 호출
		adminDormantManageService.callDormantUserProc();
		
		//휴면 계정 관리 조회
		Page<DormantUserDto> dormantUsers = adminDormantManageService.findDormantUsers(pageable);

		return ResponseEntity.ok(dormantUsers);
	}
}
