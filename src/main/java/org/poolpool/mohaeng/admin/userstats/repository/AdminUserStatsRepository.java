package org.poolpool.mohaeng.admin.userstats.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.poolpool.mohaeng.admin.userstats.dto.UserStatsDto;
import org.poolpool.mohaeng.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdminUserStatsRepository extends JpaRepository<UserEntity, Long> {

	//오늘 방문자 수, 개인 회원 수, 기업 회원 수, 휴면계정 수 조회
	@Query("""
		select new org.poolpool.mohaeng.admin.userstats.dto.UserStatsDto(
		    sum(case when u.lastLoginAt = :today then 1 else 0 end),
		    sum(case when u.userStatus = 'DORMANT' then 1 else 0 end),
		    sum(case when u.userType = 'PERSONAL' and u.userRole = 'USER' then 1 else 0 end),
		    sum(case when u.userType = 'COMPANY' and u.userRole = 'USER' then 1 else 0 end)
		)
		from UserEntity u
		where u.userRole = 'USER'
		""")
	UserStatsDto findUserDashboardStats(@Param("today") LocalDate today);

	// 최근 6개월 월별 회원 수 조회
	@Query("""
		 select new org.poolpool.mohaeng.admin.userstats.dto.UserStatsDto(
		   cast(function('date_format', u.createdAt, '%Y-%m') as string),
		   sum(case when u.userStatus = 'ACTIVE' then 1 else 0 end)
		 )
		 from UserEntity u
		 where u.userRole = 'USER'
		   and u.createdAt >= :startDate
		 group by cast(function('date_format', u.createdAt, '%Y-%m') as string)
		 order by cast(function('date_format', u.createdAt, '%Y-%m') as string)
		""")
	List<UserStatsDto> findMonthlyUsers(@Param("startDate") LocalDateTime startDate);

}
