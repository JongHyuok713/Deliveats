# DelivEats

Spring Boot 기반 **음식 주문 및 배달 관리 플랫폼**입니다.  
사용자, 음식점, 배달원을 하나의 시스템으로 연결하여 **주문 생성 → 배달 완료까지 관리하는 통합 서비스**를 구현했습니다.

이 프로젝트는 실제 배달 서비스의 구조를 참고하여  
**Spring Boot 백엔드 아키텍처 설계, 인증 시스템 구현, 주문 및 배달 도메인 설계 경험**을 목표로 개발되었습니다.

---

# Project Overview

| 항목 | 내용 |
|------|------|
Project Name | DelivEats
Development Period | 약 10주
Project Type | 개인 프로젝트
Main Goal | 배달 플랫폼 백엔드 구조 설계 및 구현
Core Features | 인증 시스템, 음식점 관리, 주문 시스템, 배달 상태 관리

---

# Features

## Authentication
- JWT 기반 인증 시스템
- OAuth2 Google 소셜 로그인
- Refresh Token Cookie 인증 구조

## Store Management
- 음식점 등록 / 수정 / 삭제
- 메뉴 등록 및 관리
- 음식점 목록 조회

## Order System
- 장바구니 기능
- 주문 생성 및 조회
- 주문 상태 관리

## Delivery System
- 배달 배정
- 배달 상태 관리

## Admin
- 사용자 관리
- 음식점 관리

---

# Tech Stack

### Backend
- Java 21
- Spring Boot 3.x
- Spring Data JPA
- Spring Security
- JWT
- OAuth2 Client

### Database
- MySQL
- Hibernate (JPA)

### Frontend
- Thymeleaf
- HTML / CSS
- JavaScript

### DevOps
- Gradle
- Git / GitHub
- Docker (Planned)
- AWS EC2 / RDS (Planned)

---

# System Architecture

Layered Architecture 기반 구조
Client  
↓  
Controller  
↓  
Service  
↓  
Repository  
↓  
Database (MySQL)  

각 계층의 책임을 분리하여
**유지보수성과 확장성을 고려한 구조로 설계했습니다.**

---

# Database Design

핵심 도메인 구
- User
- Store
- Menu
- Cart
- CartItem
- Order
- OrderItem
- Delivery

사용자 → 장바구니 → 주문 → 배달 흐름을 데이터 모델로 구성하여  
서비스 흐름을 관리하도록 설계했습니다.

---

# Authentication Flow

Client  
↓  
Login Request  
↓  
JWT Token 발급  
↓  
JWT Filter  
↓  
Spring Security  
↓  
Controller 접근

OAuth2 로그인과 JWT 인증을 결합한 인증 시스템을 구현했습니다.

---

# Project Structure
- controller
- service
- repository
- domain
- dto
- security
- exception
- config

---

# Troubleshooting

### JWT 인증 필터 문제

Spring Security 설정 과정에서 인증 필터가 정상적으로 동작하지 않는 문제가 발생했습니다.

해결 방법

- SecurityFilterChain 설정 재구성
- JWT Filter 순서 조정

---

### 예외 처리 구조 개선

서비스별로 다른 예외 처리 방식으로 인해 API 응답 구조가 일관되지 않는 문제가 있었습니다.

해결 방법

- CustomException 도입
- ErrorCode Enum 설계
- GlobalExceptionHandler 적용

---

# 📈 Future Improvements

- Docker 기반 배포 환경 구축
- AWS EC2 / RDS 배포
- Redis 캐시 적용
- 주문 알림 기능 구현
- 테스트 코드 확대
