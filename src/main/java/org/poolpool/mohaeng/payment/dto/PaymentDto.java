package org.poolpool.mohaeng.payment.dto;

import lombok.*;
import java.time.LocalDateTime;

public class PaymentDto {

    // ─── 결제 준비 요청 (프론트 → 백엔드) ───
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PrepareRequest {
        private Long pctBoothId;
        private Long eventId;
        private Integer amount;
        private String orderName; // 예: "모행 부스 참가비"
    }

    // ─── 결제 준비 응답 (백엔드 → 프론트) ───
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PrepareResponse {
        private String orderId;      // 내부 주문 ID (토스에 전달)
        private String orderName;
        private Integer amount;
        private String clientKey;    // 토스 클라이언트 키 (프론트에서 SDK 초기화용)
    }

    // ─── 결제 승인 요청 (프론트 → 백엔드) ───
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConfirmRequest {
        private String paymentKey;   // 토스 결제 키
        private String orderId;      // 내부 주문 ID
        private Integer amount;      // 결제 금액 (검증용)
    }

    // ─── 결제 승인 응답 (백엔드 → 프론트) ───
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ConfirmResponse {
        private Long paymentId;
        private String paymentNo;
        private String paymentStatus;
        private Integer amountTotal;
        private LocalDateTime approvedAt;
        private String orderName;
    }
}
