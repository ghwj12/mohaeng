package org.poolpool.mohaeng.payment.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

import org.poolpool.mohaeng.event.participation.entity.EventParticipationEntity;
import org.poolpool.mohaeng.event.participation.repository.EventParticipationRepository;
import org.poolpool.mohaeng.payment.dto.PaymentDto;
import org.poolpool.mohaeng.payment.entity.PaymentEntity;
import org.poolpool.mohaeng.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final EventParticipationRepository participationRepository;
    private final ObjectMapper objectMapper;

    @Value("${toss.payments.client-key}")
    private String clientKey;

    @Value("${toss.payments.secret-key}")
    private String secretKey;

    private static final String TOSS_CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";

    // ─── 결제 준비 ───
    @Override
    @Transactional
    public PaymentDto.PrepareResponse prepare(Long userId, PaymentDto.PrepareRequest req) {

        // ✅ 일반 참여(PCT)와 부스(BOOTH) 구분
        boolean isBooth = req.getPctBoothId() != null;
        String payType = isBooth ? "BOOTH" : "PCT";

        String prefix   = isBooth ? "BOOTH-" + req.getPctBoothId() : "PCT-" + req.getPctId();
        String orderId  = prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // READY 상태로 결제 레코드 생성
        PaymentEntity payment = PaymentEntity.builder()
                .userId(userId)
                .eventId(req.getEventId())
                .pctBoothId(req.getPctBoothId())   // 부스 결제 시 세팅
                .pctId(req.getPctId())              // ✅ 일반 참여 결제 시 세팅 (PaymentEntity에 pctId 필드 필요)
                .payType(payType)
                .paymentKey(orderId)
                .payMethod("UNKNOWN")
                .amountTotal(req.getAmount())
                .paymentStatus("READY")
                .build();

        paymentRepository.save(payment);

        log.info("[결제 준비] orderId={}, userId={}, payType={}, amount={}", orderId, userId, payType, req.getAmount());

        return PaymentDto.PrepareResponse.builder()
                .orderId(orderId)
                .orderName(req.getOrderName())
                .amount(req.getAmount())
                .clientKey(clientKey)
                .build();
    }

    // ─── 결제 승인 ───
    @Override
    @Transactional
    public PaymentDto.ConfirmResponse confirm(Long userId, PaymentDto.ConfirmRequest req) {

        // 1. 내부 결제 레코드 조회
        PaymentEntity payment = paymentRepository.findByPaymentKey(req.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        // 2. 금액 위변조 검증
        if (!payment.getAmountTotal().equals(req.getAmount())) {
            throw new IllegalArgumentException("결제 금액이 일치하지 않습니다.");
        }

        // 3. 토스 최종 승인 API 호출
        TossConfirmResult result = callTossConfirmAPI(req.getPaymentKey(), req.getOrderId(), req.getAmount());

        // 4. 결제 레코드 업데이트
        payment.setPaymentKey(req.getPaymentKey());
        payment.setPayMethod(result.method);
        payment.setPaymentStatus("APPROVED");
        payment.setApprovedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        // 5-A. 부스 참여 상태 → 결제완료
        if (payment.getPctBoothId() != null) {
            participationRepository.findBoothById(payment.getPctBoothId())
                    .ifPresent(booth -> {
                        booth.setStatus("결제완료");
                        participationRepository.saveBooth(booth);
                    });
        }

        // 5-B. ✅ 일반 행사 참여 상태 → 결제완료 (통계에 반영됨)
        if (payment.getPctId() != null) {
            participationRepository.findParticipationById(payment.getPctId())
                    .ifPresent(pct -> {
                        pct.setPctStatus("결제완료");
                        participationRepository.saveParticipation(pct);
                    });
        }

        log.info("[결제 승인 완료] orderId={}, paymentKey={}, payType={}", req.getOrderId(), req.getPaymentKey(), payment.getPayType());

        return PaymentDto.ConfirmResponse.builder()
                .paymentId(payment.getPaymentId())
                .paymentNo(payment.getPaymentKey())
                .paymentStatus(payment.getPaymentStatus())
                .amountTotal(payment.getAmountTotal())
                .approvedAt(payment.getApprovedAt())
                .orderName(result.orderName)
                .build();
    }

    // ─── 토스 서버 승인 API 호출 ───
    private TossConfirmResult callTossConfirmAPI(String paymentKey, String orderId, Integer amount) {
        try {
            String encoded = Base64.getEncoder()
                    .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

            String body = String.format(
                    "{\"paymentKey\":\"%s\",\"orderId\":\"%s\",\"amount\":%d}",
                    paymentKey, orderId, amount
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TOSS_CONFIRM_URL))
                    .header("Authorization", "Basic " + encoded)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                JsonNode errorNode = objectMapper.readTree(response.body());
                String errorMsg = errorNode.path("message").asText("결제 승인 실패");
                throw new RuntimeException("토스 결제 승인 실패: " + errorMsg);
            }

            JsonNode json = objectMapper.readTree(response.body());
            return new TossConfirmResult(
                    json.path("method").asText("UNKNOWN"),
                    json.path("orderName").asText("")
            );

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("토스 API 호출 중 오류 발생", e);
        }
    }

    private record TossConfirmResult(String method, String orderName) {}
}
