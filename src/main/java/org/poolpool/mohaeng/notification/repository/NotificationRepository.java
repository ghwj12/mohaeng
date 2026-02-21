package org.poolpool.mohaeng.notification.repository;

import org.poolpool.mohaeng.notification.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    Page<NotificationEntity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    long countByUserId(Long userId);

    int deleteByNotificationIdAndUserId(Long notificationId, Long userId);

    int deleteByUserId(Long userId);
}