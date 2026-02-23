package org.poolpool.mohaeng.admin.userstats.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.poolpool.mohaeng.admin.userstats.dto.UserStatsDto;
import org.poolpool.mohaeng.admin.userstats.service.AdminUserStatsService;
import org.poolpool.mohaeng.common.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/admin/userstats")
@RequiredArgsConstructor
public class AdminUserStatsController {

	private final AdminUserStatsService adminUserStatsService;
	
	//운영 통계 조회
	@GetMapping("/getOperateStats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOperateStats() {
		//오늘 방문자 수, 개인 회원 수, 기업 회원 수, 휴면계정 수 조회
		UserStatsDto dashboardStats = adminUserStatsService.getDashboardStats();
		//최근 6개월 월별 누적 회원 수 조회
		List<UserStatsDto> monthlyUsers = adminUserStatsService.findMonthlyUsers();
		//최근 6개월 휴면계정 조치 동향 조회
		List<UserStatsDto> monthlyDormantHandle = adminUserStatsService.getDormantHandle();
		
		Map<String, Object> map = new HashMap<>();

		map.put("dashboardStats", dashboardStats);
		map.put("monthlyUsers", monthlyUsers);
		map.put("monthlyDormantHandle", monthlyDormantHandle);
        
        return ResponseEntity.ok(ApiResponse.ok("운영 통계 조회 완료", map));
    }
	
}
