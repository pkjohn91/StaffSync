# 📅 StaffSync 개발 로그

날짜별 기능 개발 히스토리 및 주요 작업 내용을 기록한 문서입니다.

---

## 🚀 바로가기

- [2025-12-13 (Day 4)](#2025-12-13-day-4)
- [2025-12-12 (Day 3)](#2025-12-12-day-3)
- [2025-12-11 (Day 2)](#2025-12-11-day-2)
- [2025-12-09 (Day 1)](#2025-12-09-day-1)

---

## 2025-12-13 (Day 4)

### ✨ 기능 개발

#### 직원 관리 기능 확장
- **직원 정보 수정 기능**: `EmployeeEditPage`를 구현하고, 관련 라우트(`employees/edit/:id`)를 추가하여 직원 정보 수정 기능 완성.
- **사원번호 자동 생성**: 신규 직원 등록 시 `EMP001`, `EMP002`와 같이 사원번호가 자동으로 순차 증가하도록 백엔드 로직 수정.
  - `EmployeeCreatePage`에서 사원번호 입력란 제거.
  - `EmployeeEditPage`에서 사원번호를 수정 불가능한 읽기 전용 필드로 변경.

#### 상품 관리 기능 구현
- **상품 정보 수정 기능**: `ProductEditPage`를 구현하고, 관련 라우트(`/products/edit/:id`)를 추가하여 상품의 이름, 카테고리, 가격 등 메타데이터 수정 기능 완성.
- **상품 삭제 기능**: 재고 대시보드에 상품 삭제 버튼을 추가하고, `DELETE /api/products/{id}` API와 연동하여 상품 삭제 기능 구현.
- **백엔드 API 확장**: 상품 단일 조회(`GET /api/products/{id}`) 및 수정(`PUT /api/products/{id}`)을 위한 API 엔드포인트 구현.

### 🎨 UI/UX 개선
- **로그인 UI 직관성 개선**: 활성화된 탭(관리자/직원)과 아래 로그인 폼이 하나로 합쳐져 보이도록 UI를 수정하여 현재 어떤 유형으로 로그인하는지 명확하게 인지할 수 있도록 개선.
- **버튼 UI 적용**: 재고 대시보드와 직원 목록의 '수정', '삭제' 텍스트를 버튼 형태로 변경하여 사용자 인터랙션 개선.

### 🔧 기술 작업 및 버그 수정
- **TDD 유지보수**: 사원번호 자동 생성 로직 변경에 따라 `EmployeeServiceTest`의 테스트 케이스를 리팩토링하고 통과 확인.
- **Network Error 해결**: 상품 수정 페이지 진입 시 발생하던 'Network Error'를 백엔드에 누락된 API 엔드포인트를 추가하여 해결.
- **백엔드 컴파일 오류 수정**: `@PutMapping` 어노테이션 임포트 누락으로 인해 발생한 빌드 실패 문제 해결.
- **도메인 모델 리팩토링**: `Product` 엔티티에 `updateDetails` 메서드를 추가하여 도메인 객체가 자신의 상태 변경을 책임지도록 역할 분리.

### 📝 배운 점
- 기능 변경 시 비즈니스 로직뿐만 아니라 관련 테스트 코드 또한 반드시 함께 수정해야 함을 재확인.
- 프론트엔드 'Network Error' 발생 시, CORS 설정과 함께 백엔드 컨트롤러에 해당 HTTP Method와 URL을 처리하는 엔드포인트가 존재하는지 최우선으로 확인하는 디버깅 절차의 중요성을 느낌.
- 직관적인 UI는 사용자의 실수를 줄이고 명확한 경험을 제공하는 데 핵심적인 역할을 함.

---

## 2025-12-12 (Day 3)

### ✨ 기능 개발

#### 재고 관리 대시보드
- Product 도메인 모델 설계
  - 재고 수량, 최소 재고, 가격 등 관리
  - StockStatus Enum (IN_STOCK, LOW_STOCK, OUT_OF_STOCK)
- ProductRepository 구현
  - 카테고리별 조회
  - 재고 상태별 조회
  - 재고 부족 상품 조회
  - 카테고리별 재고 집계

#### 비즈니스 로직 구현
- 재고 상태 자동 계산
  - 재고 0 → 품절
  - 재고 ≤ 최소재고 → 재고 부족
  - 그 외 → 재고 충분
- 재고 증감 로직
  - `addStock()` - 재고 추가
  - `reduceStock()` - 재고 차감
  - 유효성 검증 포함

#### 대시보드 API
- ProductService 구현
  - `getDashboard()` - 통계 데이터 제공
  - `getAllProducts()` - 전체 상품 조회
  - `getLowStockProducts()` - 재고 부족 상품
- ProductController REST API
  - `GET /api/products/dashboard`
  - `GET /api/products`
  - `GET /api/products/low-stock`
  - `POST /api/products`
  - `PATCH /api/products/{id}/stock`
  - `DELETE /api/products/{id}`

#### 대시보드 UI
- 실시간 재고 현황 대시보드 구현
  - 4개의 통계 카드 (전체/충분/부족/품절)
  - 총 재고 가치 표시
  - 재고 부족 알림 배너
  - 상품 목록 테이블
- 상태별 색상 구분 (초록/노랑/빨강)

#### 인증 시스템 통합
- 로그인 페이지 구현
  - 이메일/비밀번호 입력 폼
  - 로그인 후 대시보드 이동
  - 회원가입 링크 추가
- React Router 라우팅 설정
  - `/` → 로그인 페이지
  - `/login` → 로그인
  - `/register` → 회원가입
  - `/dashboard` → 대시보드 (보호된 라우트)
- ProtectedRoute 컴포넌트
  - 로그인 검증
  - 미인증 시 로그인 페이지로 리다이렉트
- 로그아웃 기능
  - localStorage에서 사용자 정보 제거
  - 로그인 페이지로 이동

#### 초기 데이터 설정
- DataInitializer 구현
  - 서버 시작 시 샘플 상품 8개 자동 생성
  - 전자제품, 가구, 문구 카테고리
  - 다양한 재고 상태 데이터

### 🎨 UI/UX 개선
- 로그인 페이지 디자인
  - 그라데이션 배경
  - StaffSync 로고
  - 에러 메시지 표시
- 회원가입 페이지 리팩토링
  - "로그인으로 돌아가기" 버튼 추가
  - 레이블 및 플레이스홀더 개선
  - 일관된 디자인 적용
- 대시보드 네비게이션 바
  - StaffSync 로고 및 타이틀
  - 로그아웃 버튼
  - 고정 상단 네비게이션
- 로딩 상태 개선
  - 스피너 애니메이션 추가
  - 로딩 중 메시지 표시

### 🔧 기술 작업
- React Router v6 통합
- localStorage 기반 간단 인증
- Axios Promise.all을 이용한 병렬 요청 최적화
- 조건부 렌더링 및 상태 관리

### 📝 배운 점
- React Router의 중첩 라우팅과 보호된 라우트 구현
- Enum 타입을 활용한 상태 관리
- 도메인 로직을 엔티티 내부에 캡슐화하는 방법
- 대시보드 UI 설계 및 통계 데이터 시각화
- localStorage를 이용한 클라이언트 사이드 인증

---

## 2025-12-11 (Day 2)

### ✨ 기능 개발

#### DDD 아키텍처 도입
- 패키지 구조 리팩토링
  - `domain` - 비즈니스 로직 및 엔티티
  - `application` - 서비스 계층
  - `infrastructure` - 리포지토리 및 설정
  - `interfaces` - 컨트롤러

#### 회원 관리 시스템
- Member 도메인 엔티티 설계
- MemberRepository 구현
- MemberService 비즈니스 로직 작성
- MemberController REST API 구현

#### 이메일 인증 시스템
- Mock EmailService 구현
  - 6자리 랜덤 인증 코드 생성
  - 콘솔 출력 방식 (실제 이메일 발송 대신)
- 이메일 인증 플로우 구현
  1. 인증 코드 요청 (`/api/members/send-code`)
  2. 인증 코드 검증 (`/api/members/verify-code`)
  3. 회원가입 완료 (`/api/members/register`)

#### 회원가입 UI 개선
- 단계별 폼 활성화 (Step-by-Step UX)
- 인증 전: 이메일만 입력 가능
- 인증 후: 이름/비밀번호 입력 가능
- 실시간 버튼 상태 변경

#### 예외 처리 개선
- GlobalExceptionHandler 구현
  - `IllegalArgumentException` → 400 Bad Request
  - 명확한 에러 메시지 반환
- 중복 가입 방지 로직

### 🔧 기술 작업

#### TDD 환경 구축
- JUnit5 테스트 프레임워크 설정
- Mockito 통합
- MemberService 단위 테스트 작성
  - `requestVerification_Success()`
  - `requestVerification_DuplicateEmail()`
  - `verifyCode_Success()`
  - `verifyCode_InvalidCode()`

#### 코드 품질 개선
- 서비스 계층 의존성 개선
  - Controller가 EmailService를 직접 의존하지 않도록 수정
  - MemberService를 통한 통합 관리
- 이메일 검증 로직을 도메인 계층으로 이동

### 📝 배운 점
- DDD의 각 계층별 책임 분리
- 도메인 로직을 엔티티 내부에 작성하는 방법
- Mock 객체를 사용한 단위 테스트 작성법
- GlobalExceptionHandler의 필요성

---

## 2025-12-09 (Day 1)

### ✨ 기능 개발

#### 프로젝트 초기 설정
- Spring Boot 프로젝트 생성 (Java 25, Gradle)
- React 프로젝트 생성 (Vite)
- Git 저장소 초기화

#### 직원 관리 기본 기능
- Employee 엔티티 설계 및 구현
- EmployeeRepository 생성 (Spring Data JPA)
- EmployeeController REST API 구현
  - `GET /api/employees` - 직원 목록 조회
  - `POST /api/employees` - 직원 등록
  - `DELETE /api/employees/{id}` - 직원 삭제

#### Frontend 구현
- Tailwind CSS v3 통합
- 직원 목록 화면 구현
- Axios를 이용한 API 통신 설정

#### 이메일 검증
- 정규표현식 기반 이메일 유효성 검사
- 실시간 검증 UI 피드백

### 🔧 기술 작업
- CORS 설정 (`@CrossOrigin` 어노테이션)
- H2 데이터베이스 설정
- JPA DDL 자동 생성 설정

### 📝 배운 점
- Vite의 빠른 개발 서버 경험
- Spring Boot와 React 간 통신 구조 이해
- Tailwind CSS 유틸리티 클래스 활용법

---

## 📊 누적 개발 현황

### 완료된 기능 (✅)
- [x] 회원 관리 (가입/로그인/로그아웃)
- [x] 이메일 인증 시스템
- [x] 재고 관리 대시보드
- [x] 직원 목록 조회 및 수정
- [x] 상품 목록 조회, 등록, 수정, 삭제
- [x] 사원번호 자동 생성
- [x] DDD 아키텍처 구조
- [x] 예외 처리 시스템
- [x] 단위 테스트 환경 (TDD)

### 진행 중 (🚧)
- 재고 증감 UI 구현

### 예정 (📋)
- JWT 기반 인증
- Spring Security 적용
- 실제 이메일 발송 (SMTP)
- 페이징 처리
- 검색 및 필터링 기능 고도화

---

## 🎯 다음 단계

### 단기 목표 (1주일)
1. 재고 증감 버튼 UI 구현
2. 상품 목록의 카테고리별 필터링 기능
3. JWT 및 Spring Security 기반 인증 시스템 도입

### 중기 목표 (1개월)
1. 실제 이메일 발송 연동
2. API 문서화 (Swagger)
3. 직원/상품 목록 페이징 처리

### 장기 목표 (3개월)
1. PostgreSQL 전환
2. Docker 컨테이너화
3. AWS 배포
4. CI/CD 파이프라인

---

**마지막 업데이트**: 2025-12-13