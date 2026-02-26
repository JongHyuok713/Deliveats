Deliveats (2025-12-31)

Order <-> Delivery 상태 연동 규칙
1. 주문 -> 배달 생성
OrderStatus.ACCEPTED
→ Delivery 생성 (REQUESTED)

2. 라이더 배정
DeliveryStatus.ASSIGNED
(주문 상태 변경 없음)

3. 픽업
DeliveryStatus.PICKED_UP
→ OrderStatus 그대로 COOKING 또는 ACCEPTED

4. 배달 완료
DeliveryStatus.DELIVERED
→ OrderStatus.COMPLETED

5. 배달 실패
DeliveryStatus.FAILED
→ OrderStatus.CANCELED 또는 별도 처리

예외 설계 구조 변경 진행 중
클래스 중심
UserNotFoundException
UserAlreadyExistsException
InvalidUserException

- 각 예외가 ErrorCode를 내부에 고정
- Service 코드가 매우 읽기 쉬움
-> throw new UserNotFoundException();