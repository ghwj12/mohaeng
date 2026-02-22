package org.poolpool.mohaeng.admin.eventStats.service;

import java.time.LocalDate;
import java.util.List;

import org.poolpool.mohaeng.admin.eventStats.dto.AdminEventStatsDto;
import org.poolpool.mohaeng.admin.eventStats.repository.AdminEventStatsRepository;
import org.poolpool.mohaeng.event.list.entity.EventEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminEventStatsServiceImpl implements AdminEventStatsService {

    private final AdminEventStatsRepository repository;

    @Override
    public List<AdminEventStatsDto.EventListResponse> getAllEvent(LocalDate start, LocalDate end, String category, String location, String status) {
        return repository.findAllEventsFiltered(start, end, category, location, status);
    }

    @Override
    public List<AdminEventStatsDto.MonthlyStatsResponse> getEventCountByMonth(int year) {
        return repository.countByMonth(year);
    }

    @Override
    public List<AdminEventStatsDto.CategoryStatsResponse> getEventCountByCategory() {
        return repository.countByCategory();
    }
    
    @Override
    public AdminEventStatsDto.EventAnalysisDetailResponse getEventAnalysis(Long eventId) {
        // 1. 행사 기본 정보 조회
        EventEntity event = repository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("해당 행사를 찾을 수 없습니다."));

        // 2. 통계 지표 조회
        long participantCount = repository.countParticipantsByEventId(eventId);
        long reviewCount = repository.countReviewsByEventId(eventId);
        long wishCount = repository.countWishlistByEventId(eventId);
        
        // 3. 수익 계산 (일반 참여 수익 + 부스 수익)
        long eventPrice = (event.getPrice() != null) ? event.getPrice() : 0;
        long participantRevenue = participantCount * eventPrice; 
        
        Long boothRevenueOpt = repository.sumBoothRevenueByEventId(eventId);
        long boothRevenue = (boothRevenueOpt != null) ? boothRevenueOpt : 0; 
        
        long totalRevenue = participantRevenue + boothRevenue;

        // 4. 성별 참여자 통계 가공
        List<Object[]> genderStats = repository.countGenderByEventId(eventId);
        long maleCount = 0;
        long femaleCount = 0;
        
        for (Object[] stat : genderStats) {
            String gender = (String) stat[0];
            long count = (long) stat[1];
            
            if (gender != null) {
                // 정확히 M과 F로 판단
                if (gender.equalsIgnoreCase("M")) {
                    maleCount += count;
                } else if (gender.equalsIgnoreCase("F")) {
                    femaleCount += count;
                }
            }
        }

        // 5. 주최자 정보 안전하게 매핑
        String hostName = "정보 없음", hostEmail = "정보 없음", hostPhone = "정보 없음";
        if (event.getHost() != null) {
            hostName = event.getHost().getName();
            hostEmail = event.getHost().getEmail();
            hostPhone = event.getHost().getPhone();
        }

        // 6. DTO 조립 및 반환
        return AdminEventStatsDto.EventAnalysisDetailResponse.builder()
                .title(event.getTitle())
                .simpleExplain(event.getSimpleExplain())
                .eventPeriod(event.getStartDate() + " ~ " + event.getEndDate())
                .location(event.getLotNumberAdr())
                .hostName(hostName)
                .hostEmail(hostEmail)
                .hostPhone(hostPhone)
                .hashtags(event.getHashtagIds())
                .viewCount(event.getViews())
                .participantCount(participantCount)
                .reviewCount(reviewCount)
                .wishCount(wishCount)
                .totalRevenue(totalRevenue)
                .maleCount(maleCount)
                .femaleCount(femaleCount)
                .build();
    }
}