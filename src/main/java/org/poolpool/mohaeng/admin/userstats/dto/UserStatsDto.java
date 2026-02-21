package org.poolpool.mohaeng.admin.userstats.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserStatsDto {
	//오늘 방문자 수, 개인 회원 수, 기업 회원 수, 휴면계정 수 조회용
	private Long todayUserCount;	//오늘 방문자 수
	private Long personalUserCount;	//개인 회원 수
	private Long companyUserCount;	//기업 회원 수
	private Long totalDormantCount;	//휴면계정 수
	
	//최근 6개월 월별 누적 회원 수 조회용
    private String period;	//기간
    private Long userCount;	//회원 수
    
	public UserStatsDto(String period, Long userCount) {
		super();
		this.period = period;
		this.userCount = userCount;
	}

	public UserStatsDto(Long todayUserCount, Long totalDormantCount, Long personalUserCount, Long companyUserCount) {
		super();
		this.todayUserCount = todayUserCount;
		this.totalDormantCount = totalDormantCount;
		this.personalUserCount = personalUserCount;
		this.companyUserCount = companyUserCount;
	}
    
}