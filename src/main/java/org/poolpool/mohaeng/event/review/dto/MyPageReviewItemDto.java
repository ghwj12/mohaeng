package org.poolpool.mohaeng.event.review.dto;

import java.time.LocalDateTime;

import org.poolpool.mohaeng.event.review.entity.ReviewEntity;

public class MyPageReviewItemDto {
  private Long reviewId;
  private Long eventId;
  private String eventTitle;
  private double avgRating;
  private String summary;
  private LocalDateTime createdAt;

  public static MyPageReviewItemDto fromEntity(ReviewEntity e) {
    MyPageReviewItemDto dto = new MyPageReviewItemDto();
    dto.reviewId = e.getReviewId();
    dto.eventId = e.getEvent().getEventId();           // ✅ EventEntity 필드명 맞게 수정
    dto.eventTitle = e.getEvent().getTitle();          // ✅ EventEntity 필드명 맞게 수정
    dto.avgRating = (e.getRatingContent() + e.getRatingProgress() + e.getRatingMood()) / 3.0;

    String c = e.getContent() == null ? "" : e.getContent();
    dto.summary = c.length() <= 30 ? c : c.substring(0, 30) + "...";
    dto.createdAt = e.getCreatedAt();
    return dto;
  }

  public Long getReviewId() { return reviewId; }
  public Long getEventId() { return eventId; }
  public String getEventTitle() { return eventTitle; }
  public double getAvgRating() { return avgRating; }
  public String getSummary() { return summary; }
  public LocalDateTime getCreatedAt() { return createdAt; }
}
