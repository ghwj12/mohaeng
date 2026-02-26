package org.poolpool.mohaeng.payment.service;

import org.poolpool.mohaeng.payment.dto.PaymentDto;

public interface PaymentService {

    // 결제 준비 - orderId 생성 및 READY 상태 저장
    PaymentDto.PrepareResponse prepare(Long userId, PaymentDto.PrepareRequest request);

    // 결제 승인 - 토스 서버에 최종 승인 요청 후 DB 업데이트
    PaymentDto.ConfirmResponse confirm(Long userId, PaymentDto.ConfirmRequest request);
}
