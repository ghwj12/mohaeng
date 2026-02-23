package org.poolpool.mohaeng.notification.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTypeEntity {

    @Id
    // ⚠️ notiTypeId를 1~10으로 "고정"해서 쓸 거면
    // @GeneratedValue를 빼고 data.sql로 1~10을 직접 넣는 방식이 안전함.
    // (지금은 고정 ID를 쓰려면 @GeneratedValue 없이 "수동 ID"가 추천)
    private Long notiTypeId;

    @Column(nullable = false, length = 50)
    private String notiTypeName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String notiTypeContents; // 템플릿: [TITLE], [REASON_CATEGORY] 치환
}