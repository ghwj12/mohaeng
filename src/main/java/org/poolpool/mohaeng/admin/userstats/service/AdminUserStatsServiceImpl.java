package org.poolpool.mohaeng.admin.userstats.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.poolpool.mohaeng.admin.userstats.dto.UserStatsDto;
import org.poolpool.mohaeng.admin.userstats.repository.AdminUserStatsRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserStatsServiceImpl implements AdminUserStatsService{

	private final AdminUserStatsRepository adminUserStatsRepository;

	//오늘 방문자 수, 개인 회원 수, 기업 회원 수, 휴면계정 수 조회
	@Override
	public UserStatsDto getDashboardStats() {
		return adminUserStatsRepository.findUserDashboardStats(LocalDate.now());
	}

	//최근 6개월 월별 누적 회원 수 조회
	@Override
	public List<UserStatsDto> findMonthlyUsers() {
		LocalDate now = LocalDate.now();
		
		//6개월 전 월
		LocalDateTime sixMonthsAgo = now.minusMonths(5).withDayOfMonth(1).atStartOfDay();
		//최근 6개월 월별 회원 수 조회
		List<UserStatsDto> monthlyUsers = adminUserStatsRepository.findMonthlyUsers(sixMonthsAgo);
		
		//조회 결과 Map으로 변환
	    Map<String, Long> resultMap = monthlyUsers.stream()
	            .collect(Collectors.toMap(
	                    UserStatsDto::getPeriod,
	                    UserStatsDto::getUserCount));

	    List<UserStatsDto> accMonthlyUsers = new ArrayList<>();
	    long cumulativeUser = 0;

	    //최근 6개월 강제 생성
	    for (int i = 5; i >= 0; i--) {

	        LocalDate targetMonth = now.minusMonths(i);
	        String period = targetMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));

	        Long count = resultMap.getOrDefault(period, 0L);
	        //회원 수 누적
	        cumulativeUser += count;
	        accMonthlyUsers.add(new UserStatsDto(period, cumulativeUser));
	    }

	    return accMonthlyUsers;
		
	}

	//최근 6개월 휴면 계정 조치 동향 조회
	@Override
	public List<UserStatsDto> getDormantHandle() {
		LocalDate now = LocalDate.now();
		
		//6개월 전 월
		LocalDateTime sixMonthsAgo = now.minusMonths(5).withDayOfMonth(1).atStartOfDay();
		
		//안내 메일 전송 수 조회
		List<UserStatsDto> monthlyDormantNotified = adminUserStatsRepository.findMonthlyDormantNotified(sixMonthsAgo);
		//탈퇴 처리 수 조회
		List<UserStatsDto> monthlyDormantWithdrawn = adminUserStatsRepository.findMonthlyDormantWithdrawn(sixMonthsAgo);
		
		//조회 결과 Map으로 변환
		Map<String, Long> notifiedMap =	monthlyDormantNotified.stream()
	                    .collect(Collectors.toMap(
	                            UserStatsDto::getPeriod,
	                            UserStatsDto::getDormantNotifiedCount));

	    Map<String, Long> withdrawnMap = monthlyDormantWithdrawn.stream()
	                    .collect(Collectors.toMap(
	                            UserStatsDto::getPeriod,
	                            UserStatsDto::getDormantWithdrawnCount));
		
	    List<UserStatsDto> result = new ArrayList<>();

	    //최근 6개월 강제 생성
	    for (int i = 5; i >= 0; i--) {

	        LocalDate target = now.minusMonths(i);
	        String period = target.format(
	                DateTimeFormatter.ofPattern("yyyy-MM"));

	        Long notified = notifiedMap.getOrDefault(period, 0L);
	        Long withdrawn = withdrawnMap.getOrDefault(period, 0L);
	        
	        result.add(new UserStatsDto(period, notified, withdrawn));
	    }
	    
		return result;
	}
	
}
