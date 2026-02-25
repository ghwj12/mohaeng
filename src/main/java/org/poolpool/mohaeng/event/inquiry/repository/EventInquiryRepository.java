package org.poolpool.mohaeng.event.inquiry.repository;

import org.poolpool.mohaeng.event.inquiry.entity.EventInquiryEntity;
import org.poolpool.mohaeng.user.entity.UserEntity;
import org.poolpool.mohaeng.event.list.entity.EventEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventInquiryRepository extends JpaRepository<EventInquiryEntity, Long> {

    // =========================
    // 1) 행사 상세 문의 탭 목록
    // =========================
    List<EventInquiryEntity> findByEventIdOrderByCreatedAtDesc(Long eventId);

    /**
     * ✅ 문의 목록 + 작성자 이름
     * - EventInquiryEntity는 userId만 가지고 있어서, USERS 테이블 조인으로 name을 가져온다.
     */
    @Query("""
            select
              e.inqId as inqId,
              e.eventId as eventId,
              e.userId as userId,
              u.name as userName,
              e.content as content,
              e.replyContent as replyContent,
              e.replyId as replyId,
              e.replyDate as replyDate,
              e.status as status,
              e.createdAt as createdAt
            from EventInquiryEntity e
            join UserEntity u on u.userId = e.userId
            where e.eventId = :eventId
            order by e.createdAt desc
            """)
    List<InquiryRow> findListWithUserName(@Param("eventId") Long eventId);

    interface InquiryRow {
        Long getInqId();
        Long getEventId();
        Long getUserId();
        String getUserName();
        String getContent();
        String getReplyContent();
        Long getReplyId();
        LocalDateTime getReplyDate();
        String getStatus();
        LocalDateTime getCreatedAt();
    }

    // =========================
    // 2) 마이페이지 - 작성 문의(WRITTEN)
    // =========================
    Page<EventInquiryEntity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // =========================
    // 3) 마이페이지 - 받은 문의(RECEIVED)
    //   - 내가 주최한 행사(event.host.userId)에 달린 문의들
    // =========================
    @Query("""
        select i
        from EventInquiryEntity i
        join EventEntity e on e.eventId = i.eventId
        where e.host.userId = :hostUserId
        order by i.createdAt desc
    """)
    Page<EventInquiryEntity> findReceivedByHostUserId(@Param("hostUserId") Long hostUserId, Pageable pageable);

    // =========================
    // 4) 마이페이지 - 전체(ALL)
    //   - (내가 작성한 문의) OR (내가 주최한 행사에 달린 문의)
    // =========================
    @Query("""
        select i
        from EventInquiryEntity i
        where i.userId = :userId
           or i.eventId in (
                select e.eventId
                from EventEntity e
                where e.host.userId = :userId
           )
        order by i.createdAt desc
    """)
    Page<EventInquiryEntity> findAllForMypage(@Param("userId") Long userId, Pageable pageable);
}
