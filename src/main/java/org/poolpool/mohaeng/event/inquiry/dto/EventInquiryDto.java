package org.poolpool.mohaeng.event.inquiry.dto;

import org.poolpool.mohaeng.event.inquiry.entity.EventInquiryEntity;

import java.time.LocalDateTime;

public class EventInquiryDto {

    private Long inqId;
    private Long eventId;
    private Long userId;

    // ✅ 질문 작성자 이름(USERS.NAME) - mypage 조회 시 join/projection에서 채움
    private String userName;

    private String content;

    private String replyContent;
    private Long replyId;
    private LocalDateTime replyDate;

    private String status;
    private LocalDateTime createdAt;

    // ✅ 마이페이지에서 행사 정보까지 같이 내려주기 위한 필드
    private String eventTitle;      // EVENT.TITLE
    private String eventThumbnail;  // EVENT.THUMBNAIL

    public EventInquiryDto() {}

    /** ✅ JPQL constructor expression용 생성자 */
    public EventInquiryDto(
            Long inqId,
            Long eventId,
            Long userId,
            String userName,
            String content,
            String replyContent,
            Long replyId,
            LocalDateTime replyDate,
            String status,
            LocalDateTime createdAt,
            String eventTitle,
            String eventThumbnail
    ) {
        this.inqId = inqId;
        this.eventId = eventId;
        this.userId = userId;
        this.userName = userName;
        this.content = content;
        this.replyContent = replyContent;
        this.replyId = replyId;
        this.replyDate = replyDate;
        this.status = status;
        this.createdAt = createdAt;
        this.eventTitle = eventTitle;
        this.eventThumbnail = eventThumbnail;
    }

    // Entity -> DTO
    public static EventInquiryDto fromEntity(EventInquiryEntity e) {
        EventInquiryDto d = new EventInquiryDto();
        d.inqId = e.getInqId();
        d.eventId = e.getEventId();
        d.userId = e.getUserId();
        // userName / eventTitle / eventThumbnail 은 join/projection에서 채우는 것을 권장
        d.content = e.getContent();
        d.replyContent = e.getReplyContent();
        d.replyId = e.getReplyId();
        d.replyDate = e.getReplyDate();
        d.status = e.getStatus();
        d.createdAt = e.getCreatedAt();
        return d;
    }

    // DTO -> Entity (등록/수정용 기본 필드만)
    public EventInquiryEntity toEntity() {
        EventInquiryEntity e = new EventInquiryEntity();
        e.setInqId(this.inqId); // 수정 시 사용, 등록 시 null이면 auto-increment
        e.setEventId(this.eventId);
        e.setUserId(this.userId);
        e.setContent(this.content);
        return e;
    }

    // ===== Getter / Setter =====
    public Long getInqId() { return inqId; }
    public void setInqId(Long inqId) { this.inqId = inqId; }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getReplyContent() { return replyContent; }
    public void setReplyContent(String replyContent) { this.replyContent = replyContent; }

    public Long getReplyId() { return replyId; }
    public void setReplyId(Long replyId) { this.replyId = replyId; }

    public LocalDateTime getReplyDate() { return replyDate; }
    public void setReplyDate(LocalDateTime replyDate) { this.replyDate = replyDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getEventTitle() { return eventTitle; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }

    public String getEventThumbnail() { return eventThumbnail; }
    public void setEventThumbnail(String eventThumbnail) { this.eventThumbnail = eventThumbnail; }
}