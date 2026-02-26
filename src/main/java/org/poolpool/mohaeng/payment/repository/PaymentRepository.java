package org.poolpool.mohaeng.payment.repository;

import org.poolpool.mohaeng.payment.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    Optional<PaymentEntity> findByPaymentKey(String paymentKey);

    Optional<PaymentEntity> findByPctBoothId(Long pctBoothId);
}
