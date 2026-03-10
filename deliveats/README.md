Deliveats

Spring Boot 기반 음식 주문 및 배달 관리 플랫폼입니다.  
사용자, 음식점, 배달원을 하나의 시스템으로 연결하여 주문 생성부터 배달 완료까지 관리하는 통합 서비스입니다.

단순 CRUD 프로젝트가 아닌 실제 서비스 구조를 고려한 **백엔드 아키텍처 설계 및 인증 시스템 구현**을 목표로 개발하였습니다.

# 프로젝트 개요

| 항목 | 내용 |
| ----- | ----- |
프로젝트명 | DelivEats
개발 기간 | 약 10주
개발 형태 | 개인 프로젝트
개발 목표 | 배달 플랫폼의 핵심 기능을 직접 설계 및 구현하여 실무형 백엔드 아키텍처 경험 확보

# 주요 기능

### 회원 및 인증
- JWT 기반 인증 시스템
- OAuth2 Google 소셜 로그인
- Refresh Token Cookie 인증 구조

### 음식점 관리
- 음식점 등록 / 수정 / 삭제
- 메뉴 등록 및 관리
- 음식점 목록 조회

### 주문 시스템
- 장바구니 기능
- 주문 생성
- 주문 상태 관리

### 배달 시스템
- 배달 배정
- 배달 상태 관리

### 관리자 기능
- 음식점 관리
- 사용자 관리

# 기술 스택

## Backend

- Java 21
- Spring Boot 3.x
- Spring Data JPA
- Spring Security
- JWT
- OAuth2 Client

## Database

- MySQL
- Hibernate (JPA)

## Frontend

- Thymeleaf
- HTML / CSS / JavaScript

## DevOps

- Gradle
- Git / GitHub

# 시스템 아키텍처

Layered Architecture 기반으로 설계되었습니다.

Controller → Service → Repository → Database

각 계층의 책임을 분리하여 유지보수성과 확장성을 고려한 구조로 설계하였습니다.

# 데이터베이스 설계

핵심 엔티티

User  
Store  
Menu  
Order  
OrderItem  
Delivery  
Cart  
CartItem  

주문(Order)을 중심으로 사용자, 음식점, 메뉴, 배달 엔티티 간 관계를 설계하여 서비스 흐름을 관리하도록 구성했습니다.

# 인증 시스템

JWT 기반 인증 구조

Client  
→ Login Request  
→ Authentication  
→ JWT Token 발급  
→ Security Filter  
→ 인증 처리

OAuth2 로그인과 JWT 인증을 결합하여 인증 구조를 구성했습니다.

# 트러블 슈팅

### JWT 인증 필터 적용 문제

Spring Security 설정 과정에서 인증 필터가 정상 동작하지 않는 문제가 발생했습니다.

해결
- SecurityFilterChain 설정 수정
- JWT 필터 순서 재배치

### 예외 처리 구조 개선

서비스마다 다른 예외 처리 방식으로 API 응답 구조가 일관되지 않는 문제가 있었습니다.

해결
- CustomException 도입
- ErrorCode Enum 설계
- GlobalExceptionHandler 적용

### 환경 설정 분리

개발 환경과 배포 환경 설정이 혼합된 문제 해결

application.yml  
application-dev.yml  
application-prod.yml  
application-oauth.yml

# 프로젝트내에 개인 역할

개인 프로젝트로 기획, 설계, 개발, 테스트 전 과정을 직접 수행
- 서비스 아키텍처 설계
- JWT 인증 시스템 구현
- OAuth2 로그인 구현
- 주문 및 배달 로직 설계
- 예외 처리 구조 설계

# 개선 및 확장 예정
- Docker 배포
- AWS EC2 / RDS
- Redis 캐시
- 주문 알림 시스템
- 테스트 코드 확대