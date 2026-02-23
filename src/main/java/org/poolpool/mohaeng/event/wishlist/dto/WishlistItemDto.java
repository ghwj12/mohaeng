package org.poolpool.mohaeng.event.wishlist.dto;

import org.poolpool.mohaeng.event.wishlist.entity.EventWishlistEntity;

import java.time.LocalDateTime;

public class WishlistItemDto {
    private long wishId;
    private long eventId;
    private boolean notificationEnabled;
    private LocalDateTime createdAt;

    public static WishlistItemDto fromEntity(EventWishlistEntity e) {
        WishlistItemDto dto = new WishlistItemDto();
        dto.wishId = e.getWishId();
        dto.eventId = e.getEventId();
        dto.notificationEnabled = e.isNotificationEnabled();
        dto.createdAt = e.getCreatedAt();
        return dto;
    }

    public long getWishId() { return wishId; }
    public long getEventId() { return eventId; }
    public boolean isNotificationEnabled() { return notificationEnabled; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
