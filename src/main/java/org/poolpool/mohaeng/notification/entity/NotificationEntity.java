package org.poolpool.mohaeng.notification.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long notiTypeId;

    private Long eventId;
    private Long reportId;

    // 중복방지 키/추가 상태 저장 용도
    @Column(length = 50)
    private String status1;

    @Column(length = 50)
    private String status2;

    private LocalDateTime eventBeforeAt;
    private LocalDateTime eventDayAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}