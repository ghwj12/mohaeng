package org.poolpool.mohaeng.event.participation.dto;

import org.poolpool.mohaeng.event.participation.entity.EventParticipationEntity;
import java.time.LocalDateTime;

public class EventParticipationDto {
    private Long pctId;
    private Long eventId;
    private LocalDateTime pctDate;
    private String pctStatus;
    private Long userId;

    private String pctGender;
    private String pctJob;
    private String pctRoot;
    private String pctGroup;
    private String pctRank;
    private String pctIntroduce;

    public static EventParticipationDto fromEntity(EventParticipationEntity e) {
        EventParticipationDto d = new EventParticipationDto();
        d.pctId = e.getPctId();
        d.eventId = e.getEventId();
        d.pctDate = e.getPctDate();
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
        e.setUserId(this.userId);
        e.setPctGender(this.pctGender);
        e.setPctJob(this.pctJob);
        e.setPctRoot(this.pctRoot);
        e.setPctGroup(this.pctGroup);
        e.setPctRank(this.pctRank);
        e.setPctIntroduce(this.pctIntroduce);
        return e;
    }

    // getter/setter
    public Long getPctId() { return pctId; }
    public void setPctId(Long pctId) { this.pctId = pctId; }
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public LocalDateTime getPctDate() { return pctDate; }
    public void setPctDate(LocalDateTime pctDate) { this.pctDate = pctDate; }
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
}

