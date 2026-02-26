package org.poolpool.mohaeng.payment.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

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

        // pctBoothId로 eventId 조회 (혹은 req에서 직접 받음)
        String orderId = "BOOTH-" + req.getPctBoothId() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // READY 상태로 결제 레코드 미리 생성
        PaymentEntity payment = PaymentEntity.builder()
                .userId(userId)
                .eventId(req.getEventId())
                .pctBoothId(req.getPctBoothId())
                .payType("BOOTH")
                .paymentKey(orderId)
                .payMethod("UNKNOWN") // 승인 후 업데이트
                .amountTotal(req.getAmount())
                .paymentStatus("READY")
                .build();

        paymentRepository.save(payment);

        log.info("[결제 준비] orderId={}, userId={}, amount={}", orderId, userId, req.getAmount());

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

        // 5. 부스 신청 상태 업데이트 (신청 → 결제완료)
        if (payment.getPctBoothId() != null) {
            participationRepository.findBoothById(payment.getPctBoothId())
                    .ifPresent(booth -> {
                        booth.setStatus("결제완료");
                        participationRepository.saveBooth(booth);
                    });
        }

        log.info("[결제 승인 완료] orderId={}, paymentKey={}", req.getOrderId(), req.getPaymentKey());

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
            // Base64 인코딩: secretKey + ":" (토스 공식 방식)
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
            String method = json.path("method").asText("UNKNOWN");
            String orderName = json.path("orderName").asText("");

            return new TossConfirmResult(method, orderName);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("토스 API 호출 중 오류 발생", e);
        }
    }

    // 내부 결과 클래스
    private record TossConfirmResult(String method, String orderName) {}
}
