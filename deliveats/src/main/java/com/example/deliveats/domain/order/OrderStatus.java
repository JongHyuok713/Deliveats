package com.example.deliveats.domain.order;

public enum OrderStatus {
    PENDING,
    CREATED,    // 주문 생성 (결제 전)
    PAID,       // 결제 완료
    ACCEPTED,   // 점주 수락
    COOKING,    // 조리 중
    DELIVERING, // 주문 배달 상태
    COMPLETED,  // 주문 완료 (배달 완료 후)
    CANCELED    // 주문 취소
}
