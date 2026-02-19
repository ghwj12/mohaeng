package org.poolpool.mohaeng.event.review.service;

import java.util.ArrayList;
import java.util.Optional;

import org.poolpool.mohaeng.common.api.PageResponse;
import org.poolpool.mohaeng.event.entity.EventEntity;
import org.poolpool.mohaeng.event.review.dto.EventReviewTabItemDto;
import org.poolpool.mohaeng.event.review.dto.MyPageReviewItemDto;
import org.poolpool.mohaeng.event.review.dto.ReviewCreateRequestDto;
import org.poolpool.mohaeng.event.review.dto.ReviewEditRequestDto;
import org.poolpool.mohaeng.event.review.entity.ReviewEntity;
import org.poolpool.mohaeng.event.review.repository.ReviewRepository;
import org.poolpool.mohaeng.user.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class ReviewServiceImpl implements ReviewService {

  private final ReviewRepository reviewRepository;
  private final Logger log = LoggerFactory.getLogger(ReviewServiceImpl.class);

  @PersistenceContext
  private EntityManager em;

  public ReviewServiceImpl(ReviewRepository reviewRepository) {
    this.reviewRepository = reviewRepository;
  }

  private ArrayList<MyPageReviewItemDto> toMyPageList(Page<ReviewEntity> page) {
    ArrayList<MyPageReviewItemDto> list = new ArrayList<>();
    for (ReviewEntity e : page.getContent()) list.add(MyPageReviewItemDto.fromEntity(e));
    return list;
  }

  private ArrayList<EventReviewTabItemDto> toTabList(Page<ReviewEntity> page) {
    ArrayList<EventReviewTabItemDto> list = new ArrayList<>();
    for (ReviewEntity e : page.getContent()) list.add(EventReviewTabItemDto.fromEntity(e));
    return list;
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<MyPageReviewItemDto> selectMyList(long userId, Pageable pageable) {
    Page<ReviewEntity> page = reviewRepository.findByUser_UserIdOrderByCreatedAtDesc(userId, pageable);
    return new PageResponse<>(toMyPageList(page), pageable.getPageNumber(), pageable.getPageSize(),
        page.getTotalElements(), page.getTotalPages());
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<EventReviewTabItemDto> selectEventReviews(long userId, long eventId, Pageable pageable) {
    Page<ReviewEntity> page = reviewRepository
        .findByEvent_EventIdAndUser_UserIdNotOrderByCreatedAtDesc(eventId, userId, pageable);
    return new PageResponse<>(toTabList(page), pageable.getPageNumber(), pageable.getPageSize(),
        page.getTotalElements(), page.getTotalPages());
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<EventReviewTabItemDto> selectMyReviewForEvent(long userId, long eventId) {
    return reviewRepository.findByUser_UserIdAndEvent_EventId(userId, eventId)
        .map(EventReviewTabItemDto::fromEntity);
  }

  @Override
  @Transactional
  public long create(long userId, ReviewCreateRequestDto request) {
    Long eventId = request.getEventId();

    if (reviewRepository.existsByUser_UserIdAndEvent_EventId(userId, eventId)) {
      throw new IllegalStateException("이미 해당 이벤트에 리뷰를 작성했습니다.");
    }

    ReviewEntity e = new ReviewEntity();
    // ✅ getReference로 “id만 있는 프록시” 세팅 (DB 조회 안 하고 FK만 연결)
    e.setUser(em.getReference(UserEntity.class, userId));
    e.setEvent(em.getReference(EventEntity.class, eventId));

    e.setRatingContent(request.getRatingContent());
    e.setRatingProgress(request.getRatingProgress());
    e.setRatingMood(request.getRatingMood());
    e.setContent(request.getContent());

    ReviewEntity saved = reviewRepository.save(e);
    log.info("Review created. reviewId={}, userId={}, eventId={}", saved.getReviewId(), userId, eventId);
    return saved.getReviewId();
  }

  @Override
  @Transactional
  public boolean edit(long userId, long reviewId, ReviewEditRequestDto request) {
    ReviewEntity e = reviewRepository.findByReviewIdAndUser_UserId(reviewId, userId)
        .orElseThrow(() -> new IllegalArgumentException("본인 리뷰만 수정할 수 있습니다."));

    e.setRatingContent(request.getRatingContent());
    e.setRatingProgress(request.getRatingProgress());
    e.setRatingMood(request.getRatingMood());
    e.setContent(request.getContent());
    return true; // dirty checking
  }

  @Override
  @Transactional
  public boolean delete(long userId, long reviewId) {
    int affected = reviewRepository.deleteByReviewIdAndUser_UserId(reviewId, userId);
    return affected > 0;
  }
}
