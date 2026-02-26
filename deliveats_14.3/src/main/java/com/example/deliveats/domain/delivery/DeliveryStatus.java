package com.example.deliveats.domain.delivery;

public enum DeliveryStatus {
    WAITING,
    REQUESTED,  // 배달 요청 생성
    ASSIGNED,   // 라이더 배정
    PICKED_UP,  // 픽업 완료
    DELIVERING, // 배당 중
    DELIVERED,  // 배달 완료
    FAILED      // 배달 실패
}
