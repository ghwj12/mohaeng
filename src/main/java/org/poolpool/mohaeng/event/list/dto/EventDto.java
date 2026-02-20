package org.poolpool.mohaeng.event.list.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.poolpool.mohaeng.event.list.entity.EventEntity;
import org.poolpool.mohaeng.event.list.entity.FileEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventDto {
    private Long eventId;
    private String title;
    private EventCategoryDto category;
    private String description;
    private String simpleExplain;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate startRecruit;
    private LocalDate endRecruit;
    private LocalDate boothStartRecruit;
    private LocalDate boothEndRecruit;
    private Boolean hasBooth;
    private Boolean hasFacility;
    private EventRegionDto region;
    private Integer price;
    private Integer capacity;
    private Integer views;
    private String eventStatus;
    private String lotNumberAdr;
    private String detailAdr;
    private String zipCode;
    private String topicIds;
    private String hashtagIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // --- 추가된 파일 관련 필드 ---
    private String thumbnail;              // 프로필 사진 (파일명 1개)
    private List<String> detailImagePaths;  // 상세페이지 사진들 (여러 개)
    private List<String> boothFilePaths;    // 부스 관련 파일들 (여러 개)

    // Entity -> DTO 변환
    public static EventDto fromEntity(EventEntity entity) {
        if (entity == null) return null;

     // 1. 상세 페이지 사진 필터링 (fileType이 'DETAIL'인 것)
        List<String> details = (entity.getEventFiles() == null) ? List.of() : 
            entity.getEventFiles().stream()
                .filter(f -> "DETAIL".equals(f.getFileType()))
                .map(FileEntity::getRenameFileName) // 저장된 파일명(경로) 가져오기
                .toList();

        // 2. 부스 관련 파일 필터링 (fileType이 'BOOTH'인 것)
        List<String> booths = (entity.getEventFiles() == null) ? List.of() : 
            entity.getEventFiles().stream()
                .filter(f -> "BOOTH".equals(f.getFileType()))
                .map(FileEntity::getRenameFileName)
                .toList();

        return EventDto.builder()
                .eventId(entity.getEventId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .simpleExplain(entity.getSimpleExplain())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .startRecruit(entity.getStartRecruit())
                .endRecruit(entity.getEndRecruit())
                .boothStartRecruit(entity.getBoothStartRecruit())
                .boothEndRecruit(entity.getBoothEndRecruit())
                .hasBooth(entity.getHasBooth())
                .hasFacility(entity.getHasFacility())
                .price(entity.getPrice())
                .capacity(entity.getCapacity())
                .views(entity.getViews())
                .eventStatus(entity.getEventStatus())
                .lotNumberAdr(entity.getLotNumberAdr())
                .detailAdr(entity.getDetailAdr())
                .zipCode(entity.getZipCode())
                .topicIds(entity.getTopicIds())
                .hashtagIds(entity.getHashtagIds())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .category(EventCategoryDto.fromEntity(entity.getCategory()))
                .region(EventRegionDto.fromEntity(entity.getRegion()))
                // --- 파일 매핑 ---
                .thumbnail(entity.getThumbnail()) // 단일 프로필
                .detailImagePaths(details)        // 다중 상세 사진
                .boothFilePaths(booths)           // 다중 부스 파일
                .build();
    }

    public EventEntity toEntity() {
        return EventEntity.builder()
                .eventId(this.eventId)
                .title(this.title)
                .description(this.description)
                .simpleExplain(this.simpleExplain)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .startRecruit(this.startRecruit)      // 추가
                .endRecruit(this.endRecruit)          // 추가
                .boothStartRecruit(this.boothStartRecruit) // 추가
                .boothEndRecruit(this.boothEndRecruit)     // 추가
                .hasBooth(this.hasBooth)
                .hasFacility(this.hasFacility)
                .price(this.price)
                .capacity(this.capacity)
                .thumbnail(this.thumbnail)
                .eventStatus(this.eventStatus)
                .lotNumberAdr(this.lotNumberAdr)      // 추가
                .detailAdr(this.detailAdr)            // 추가
                .zipCode(this.zipCode)                // 추가
                .topicIds(this.topicIds)              // 추가
                .hashtagIds(this.hashtagIds)          // 추가
                // 연관 엔티티의 경우, 각 DTO에 toEntity가 있다면 아래처럼 연결합니다.
                .category(this.category != null ? this.category.toEntity() : null)
                .region(this.region != null ? this.region.toEntity() : null)
                // 👇 추가: 위에서 만들었던 현재 시간 강제 삽입 로직
                .createdAt(this.createdAt != null ? this.createdAt : LocalDateTime.now())
                // 👇 핵심: 상태를 계산해서 Entity에 넣어줍니다!
                .eventStatus(calculateEventStatus())
                // 👇 추가: 조회수가 없으면 기본값 0 (또는 1) 세팅
                .views(this.views != null ? this.views : 0)
                .build();
    }
    
    private String calculateEventStatus() {
        LocalDate today = LocalDate.now();

        // 2. 행사 종료 (오늘이 행사 종료일보다 뒤일 때)
        if (this.endDate != null && today.isAfter(this.endDate)) {
            return "행사종료";
        }
        
        // 3. 행사 중 (오늘이 시작일~종료일 사이일 때)
        if (this.startDate != null && this.endDate != null &&
            !today.isBefore(this.startDate) && !today.isAfter(this.endDate)) {
            return "행사중";
        }
        
        // 4. 행사 참여 마감 (행사 모집은 끝났는데, 아직 행사는 시작 안 한 경우)
        if (this.endRecruit != null && today.isAfter(this.endRecruit)) {
            return "행사참여마감";
        }
        
        // 5. 행사 참여 모집 중 (오늘이 모집 시작일~마감일 사이일 때)
        if (this.startRecruit != null && this.endRecruit != null &&
            !today.isBefore(this.startRecruit) && !today.isAfter(this.endRecruit)) {
            return "행사참여모집중";
        }
        
        // 6. 부스 모집 마감 (부스 모집은 끝났고, 아직 행사 모집은 안 한 경우)
        if (this.boothEndRecruit != null && today.isAfter(this.boothEndRecruit)) {
            return "부스모집마감";
        }
        
        // 7. 부스 모집 중 (오늘이 부스 모집 시작일~마감일 사이일 때)
        if (this.boothStartRecruit != null && this.boothEndRecruit != null &&
            !today.isBefore(this.boothStartRecruit) && !today.isAfter(this.boothEndRecruit)) {
            return "부스모집중";
        }

        // 8. 그 외 (아직 부스 모집도, 행사 모집도 시작 안 한 머나먼 미래의 행사)
        return "행사예정";
    }
}
