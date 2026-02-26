package org.poolpool.mohaeng.event.mypage.repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.poolpool.mohaeng.event.mypage.dto.BoothMypageResponse;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class MypageBoothRepository {

    @PersistenceContext
    private EntityManager em;

    /**
     * ✅ 부스 신청/관리 내역(유저 기준)
     * - PARTICIPATION_BOOTH -> HOST_BOOTH -> event 조인
     * - 마이페이지에서 필요한 event 요약정보까지 같이 반환
     */
    public List<BoothMypageResponse> findMyBooths(Long userId) {
        // ⚠️ `event`는 예약어 충돌 가능성이 있어서 백틱 사용
        String sql = "\n" +
                "SELECT \n" +
                "  pb.PCT_BOOTH_ID, pb.HOST_BOOTH_ID, pb.BOOTH_TITLE, pb.BOOTH_TOPIC, pb.BOOTH_COUNT, pb.TOTAL_PRICE, pb.STATUS, pb.CREATED_AT,\n" +
                "  hb.EVENT_ID, e.TITLE, e.THUMBNAIL, e.START_DATE, e.END_DATE\n" +
                "FROM PARTICIPATION_BOOTH pb\n" +
                "JOIN HOST_BOOTH hb ON pb.HOST_BOOTH_ID = hb.BOOTH_ID\n" +
                "JOIN `event` e ON hb.EVENT_ID = e.EVENT_ID\n" +
                "WHERE pb.USER_ID = ?\n" +
                "ORDER BY pb.CREATED_AT DESC";

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter(1, userId)
                .getResultList();

        List<BoothMypageResponse> out = new ArrayList<>();
        for (Object[] r : rows) {
            out.add(BoothMypageResponse.builder()
                    .pctBoothId(toLong(r[0]))
                    .hostBoothId(toLong(r[1]))
                    .boothTitle(toStr(r[2]))
                    .boothTopic(toStr(r[3]))
                    .boothCount(toInt(r[4]))
                    .totalPrice(toInt(r[5]))
                    .status(toStr(r[6]))
                    .createdAt(toLdt(r[7]))
                    .eventId(toLong(r[8]))
                    .eventTitle(toStr(r[9]))
                    .eventThumbnail(toStr(r[10]))
                    .startDate(toLd(r[11]))
                    .endDate(toLd(r[12]))
                    .build());
        }
        return out;
    }

    private static Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.longValue();
        return Long.valueOf(String.valueOf(v));
    }

    private static Integer toInt(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.intValue();
        return Integer.valueOf(String.valueOf(v));
    }

    private static String toStr(Object v) {
        return v == null ? null : String.valueOf(v);
    }

    private static LocalDateTime toLdt(Object v) {
        if (v == null) return null;
        if (v instanceof Timestamp ts) return ts.toLocalDateTime();
        // 일부 드라이버는 LocalDateTime으로 바로 줄 수도 있음
        if (v instanceof LocalDateTime ldt) return ldt;
        return null;
    }

    private static LocalDate toLd(Object v) {
        if (v == null) return null;
        if (v instanceof Date d) return d.toLocalDate();
        if (v instanceof LocalDate ld) return ld;
        return null;
    }
}
