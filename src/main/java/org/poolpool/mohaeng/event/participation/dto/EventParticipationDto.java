package org.poolpool.mohaeng.event.participation.dto;

import java.time.LocalDate; // LocalDateTime 대신 LocalDate 임포트

import org.poolpool.mohaeng.event.participation.entity.EventParticipationEntity;

public class EventParticipationDto {
    private Long pctId;
    private Long eventId;
    private LocalDate pctDate; // 📍 1. 필드 타입 (날짜만)
    private String pctStatus;
    private Long userId;

    private String pctGender;
    private String pctJob;
    private String pctRoot;
    private String pctGroup;
    private String pctRank;
    private String pctIntroduce;
    private String pctAgeGroup; // 📍 나이대 필드 추가 (누락 확인용)

    public static EventParticipationDto fromEntity(EventParticipationEntity e) {
        EventParticipationDto d = new EventParticipationDto();
        d.pctId = e.getPctId();
        d.eventId = e.getEventId();
        
        // 📍 2. 엔티티(LocalDateTime) -> DTO(LocalDate) 변환
        if (e.getPctDate() != null) {
            d.pctDate = e.getPctDate().toLocalDate(); 
        }
        
        d.pctStatus = e.getPctStatus();
        d.userId = e.getUserId();
        d.pctGender = e.getPctGender();
        d.pctJob = e.getPctJob();
        d.pctRoot = e.getPctRoot();
        d.pctGroup = e.getPctGroup();
        d.pctRank = e.getPctRank();
        d.pctIntroduce = e.getPctIntroduce();
        return d;
    }

    public EventParticipationEntity toEntity() {
        EventParticipationEntity e = new EventParticipationEntity();
        e.setPctId(this.pctId);
        e.setEventId(this.eventId);
        
        // 📍 3. DTO(LocalDate) -> 엔티티(LocalDateTime) 변환
        if (this.pctDate != null) {
            e.setPctDate(this.pctDate.atStartOfDay());
        }
        
        e.setUserId(this.userId);
        e.setPctGender(this.pctGender);
        e.setPctJob(this.pctJob);
        e.setPctRoot(this.pctRoot);
        e.setPctGroup(this.pctGroup);
        e.setPctRank(this.pctRank);
        e.setPctIntroduce(this.pctIntroduce);
        e.setPctStatus(this.pctStatus);
        e.setPctAgeGroup(this.pctAgeGroup); 

        if (this.pctDate != null) {
            e.setPctDate(this.pctDate.atStartOfDay());
        }
        
        return e;
    }

    // 📍 4. Getter/Setter 타입도 LocalDate로 통일
    public LocalDate getPctDate() { return pctDate; }
    public void setPctDate(LocalDate pctDate) { this.pctDate = pctDate; }

    // 나머지 getter/setter...
    public Long getPctId() { return pctId; }
    public void setPctId(Long pctId) { this.pctId = pctId; }
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public String getPctStatus() { return pctStatus; }
    public void setPctStatus(String pctStatus) { this.pctStatus = pctStatus; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getPctGender() { return pctGender; }
    public void setPctGender(String pctGender) { this.pctGender = pctGender; }
    public String getPctJob() { return pctJob; }
    public void setPctJob(String pctJob) { this.pctJob = pctJob; }
    public String getPctRoot() { return pctRoot; }
    public void setPctRoot(String pctRoot) { this.pctRoot = pctRoot; }
    public String getPctGroup() { return pctGroup; }
    public void setPctGroup(String pctGroup) { this.pctGroup = pctGroup; }
    public String getPctRank() { return pctRank; }
    public void setPctRank(String pctRank) { this.pctRank = pctRank; }
    public String getPctIntroduce() { return pctIntroduce; }
    public void setPctIntroduce(String pctIntroduce) { this.pctIntroduce = pctIntroduce; }
    public String getPctAgeGroup() { return pctAgeGroup; }
    public void setPctAgeGroup(String pctAgeGroup) { this.pctAgeGroup = pctAgeGroup; }
}
