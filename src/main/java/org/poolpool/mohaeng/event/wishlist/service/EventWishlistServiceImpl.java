package org.poolpool.mohaeng.event.wishlist.service;

import org.poolpool.mohaeng.common.api.PageResponse;
import org.poolpool.mohaeng.event.wishlist.dto.WishlistCreateRequestDto;
import org.poolpool.mohaeng.event.wishlist.dto.WishlistItemDto;
import org.poolpool.mohaeng.event.wishlist.dto.WishlistToggleRequestDto;
import org.poolpool.mohaeng.event.wishlist.entity.EventWishlistEntity;
import org.poolpool.mohaeng.event.wishlist.exception.WishlistAlreadyExistsException;
import org.poolpool.mohaeng.event.wishlist.exception.WishlistNotFoundOrForbiddenException;
import org.poolpool.mohaeng.event.wishlist.repository.EventWishlistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EventWishlistServiceImpl implements EventWishlistService {

    private final EventWishlistRepository wishlistRepository;
    private final Logger log = LoggerFactory.getLogger(EventWishlistServiceImpl.class);

    public EventWishlistServiceImpl(EventWishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<WishlistItemDto> getList(long userId, Pageable pageable) {
        Page<EventWishlistEntity> pageResult =
                wishlistRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        List<WishlistItemDto> content = pageResult.getContent()
                .stream()
                .map(WishlistItemDto::fromEntity)
                .toList();

        // PageResponse는 1-base로 맞춤
        return new PageResponse<>(
                content,
                pageResult.getNumber() + 1,
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    @Override
    public long add(long userId, WishlistCreateRequestDto request) {
        long eventId = request.getEventId();

        if (wishlistRepository.existsByUserIdAndEventId(userId, eventId)) {
            throw new WishlistAlreadyExistsException(userId, eventId);
        }

        EventWishlistEntity w = new EventWishlistEntity();
        w.setUserId(userId);
        w.setEventId(eventId);
        w.setNotificationEnabled(true);

        try {
            EventWishlistEntity saved = wishlistRepository.save(w);
            log.info("wishlist add userId={} eventId={} wishId={}", userId, eventId, saved.getWishId());
            return saved.getWishId();
        } catch (DataIntegrityViolationException e) {
            // 동시성/DB 유니크 제약 등으로 insert 실패해도 "중복"으로 통일
            throw new WishlistAlreadyExistsException(userId, eventId);
        }
    }

    @Override
    public void remove(long userId, long wishId) {
        int deleted = wishlistRepository.deleteByWishIdAndUserId(wishId, userId);
        if (deleted == 0) {
            throw new WishlistNotFoundOrForbiddenException(wishId);
        }
        log.info("wishlist remove userId={} wishId={}", userId, wishId);
    }

    @Override
    public WishlistItemDto toggleNotification(long userId, long wishId, WishlistToggleRequestDto request) {
        int updated = wishlistRepository.updateNotificationEnabledByWishIdAndUserId(
                wishId, userId, request.isEnabled()
        );
        if (updated == 0) {
            throw new WishlistNotFoundOrForbiddenException(wishId);
        }

        EventWishlistEntity latest = wishlistRepository.findByWishIdAndUserId(wishId, userId)
                .orElseThrow(() -> new WishlistNotFoundOrForbiddenException(wishId));

        log.info("wishlist toggle userId={} wishId={} enabled={}", userId, wishId, latest.isNotificationEnabled());
        return WishlistItemDto.fromEntity(latest);
    }
}