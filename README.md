# 🏢 StaffSync

> HR Management System - 직원 및 재고 관리 통합 시스템

Spring Boot와 React를 활용한 인사 관리 시스템(HRMS) 프로젝트입니다.  
DDD(Domain-Driven Design) 아키텍처를 적용하여 확장 가능한 구조로 설계되었습니다.

---

## 🛠 기술 스택

### Frontend
- React 18+ (Vite)
- Tailwind CSS v3
- React Router v6
- Axios

### Backend
- Java 25 (LTS)
- Spring Boot 4.0
- Spring Data JPA
- H2 Database (In-Memory)
- Lombok

---

## 🎯 핵심 기능

### 회원 관리
- 이메일 인증 기반 회원가입
- 로그인/로그아웃
- 단계별 회원가입 UX

### 재고 관리
- 실시간 재고 현황 대시보드
- 재고 상태 자동 분류 (재고 충분 / 부족 / 품절)
- 총 재고 가치 계산
- 재고 부족 알림

### 직원 관리
- 직원 목록 조회
- 신규 직원 등록
- 직원 정보 삭제

---

## 🏗 아키텍처

### DDD (Domain-Driven Design) 구조

```
backend/src/main/java/com/staffsync/
├── domain/              # 도메인 계층 - 비즈니스 로직
├── application/         # 애플리케이션 계층 - 유스케이스
├── infrastructure/      # 인프라 계층 - 데이터 접근
└── interfaces/          # 인터페이스 계층 - API 엔드포인트
```

**계층별 역할**
- **Domain**: 핵심 비즈니스 로직과 엔티티
- **Application**: 도메인을 조합한 서비스 로직
- **Infrastructure**: DB 접근 (Repository)
- **Interfaces**: REST API Controller

---

## 🚀 실행 방법

### Backend

```bash
cd backend
./gradlew bootRun
```

서버: `http://localhost:8080`

### Frontend

```bash
cd frontend
npm install
npm run dev
```

클라이언트: `http://localhost:5173`

### H2 Console (선택)

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (공백)

---

## 📝 참고 문서

- [개발 로그](./DEVLOG.md) - 날짜별 기능 개발 히스토리
- [트러블슈팅](./TROUBLESHOOTING.md) - 에러 해결 과정 기록

---
