package org.poolpool.mohaeng.admin.userstats.service;

import java.util.List;

import org.poolpool.mohaeng.admin.userstats.dto.UserStatsDto;

public interface AdminUserStatsService {

	//오늘 방문자 수, 개인 회원 수, 기업 회원 수, 휴면계정 수 조회
	UserStatsDto getDashboardStats();
	
	//최근 6개월 월별 회원 수 조회
	List<UserStatsDto> findMonthlyUsers();
	
	//최근 6개월 휴면계정 조치 동향 조회
	List<UserStatsDto> getDormantHandle();
	
}
