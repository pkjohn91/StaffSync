# 📅 StaffSync 개발 로그

날짜별 기능 개발 히스토리 및 주요 작업 내용을 기록한 문서입니다.

---

## 🚀 바로가기

- [2025-12-16 (Day 5)](#2025-12-16-day-5)
- [2025-12-13 (Day 4)](#2025-12-13-day-4)
- [2025-12-12 (Day 3)](#2025-12-12-day-3)
- [2025-12-11 (Day 2)](#2025-12-11-day-2)
- [2025-12-09 (Day 1)](#2025-12-09-day-1)

---

## 2025-12-16 (Day 5)

### ✨ 기능 개발

#### 재고 증감 UI 구현
- **재고 증가/감소 버튼 추가**: 대시보드 테이블에 +/- 버튼을 추가하여 직관적인 재고 관리 가능
- **백엔드 API 구현**:
  - `PATCH /api/products/{id}/stock/increase?amount=1` - 재고 증가
  - `PATCH /api/products/{id}/stock/decrease?amount=1` - 재고 감소
- **실시간 데이터 갱신**: 재고 변경 후 자동으로 대시보드 전체 데이터 새로고침

#### 상품 등록 폼 구현
- **ProductCreatePage 생성**: 상품명, 카테고리, 초기 재고, 최소 재고, 가격 입력 폼 구현
- **유효성 검사**: 클라이언트 측에서 필수 입력 검증 및 음수 방지
- **등록 정보 미리보기**: 입력한 정보를 실시간으로 미리보기하여 사용자 확인 가능
- **라우팅 연동**: `/products/create` 경로로 접근 가능하도록 설정

#### 카테고리별 검색 및 필터링
- **백엔드 API 추가**:
  - `GET /api/products/category/{category}` - 카테고리별 상품 조회
  - `GET /api/products/search?name=키워드` - 상품명 검색
  - `ProductRepository.findByNameContainingIgnoreCase()` 구현
- **프론트엔드 검색 UI**:
  - 검색창과 카테고리 드롭다운 필터 추가
  - 클라이언트 측 필터링으로 즉각 반응
  - 검색 결과 개수 표시
  - 결과 없을 시 안내 메시지 출력

#### 직원 관리 시스템 재설계
- **Employee 엔티티 재구성**:
  - 기존: `name`, `department`, `position`, `salary`
  - 변경: `employeeId`, `name`, `email`, `hireDate`, `salary`, `department`
- **사원번호(employeeId) 필드 추가**: 고유한 직원 식별자 (예: EMP001)
- **이메일(email) 필드 추가**: 직원 이메일 주소 (Unique 제약)
- **입사일(hireDate) 필드 추가**: LocalDate 타입으로 입사 날짜 관리
- **EmployeeRepository 확장**:
  - `findByEmployeeId()` - 사원번호로 조회
  - `findByEmail()` - 이메일로 조회
  - `existsByEmployeeId()`, `existsByEmail()` - 중복 체크
  - `findByDepartment()` - 부서별 조회
  - `findByNameContainingIgnoreCase()` - 이름 검색

#### 직원 등록/수정 페이지 개선
- **EmployeeCreatePage**:
  - 사원번호, 이름, 이메일, 입사일, 부서, 급여 입력 폼
  - 이메일 중복 검증
  - 입사일 날짜 선택기 (Date Picker)
- **EmployeeEditPage**:
  - 사원번호와 입사일은 읽기 전용으로 표시
  - 이름, 이메일, 부서, 급여만 수정 가능
  - 수정 시 이메일 중복 검증 (본인 제외)

#### 직원 목록 검색 및 필터링
- **검색 기능**: 이름, 사원번호, 이메일로 통합 검색
- **부서별 필터**: 드롭다운으로 부서 선택 시 해당 부서 직원만 표시
- **검색 결과 개수**: 필터링된 직원 수 실시간 표시

#### 로그인 UI 개선
- **탭 방식 도입**: 관리자 로그인 / 직원 로그인 탭으로 구분
- **직관적인 아이콘**: 각 탭에 역할 구분 아이콘 추가
- **탭별 기능 차별화**: 관리자만 회원가입 링크 표시

### 🔐 보안 및 인증

#### JWT 기반 인증 시스템
- **JWT 라이브러리 통합**: JJWT 0.12.3 버전 사용
- **JwtTokenProvider 구현**:
  - `createAccessToken()` - Access Token 생성 (1시간 유효)
  - `createRefreshToken()` - Refresh Token 생성 (7일 유효)
  - `validateToken()` - 토큰 유효성 검증
  - `getEmailFromToken()`, `getRoleFromToken()` - 토큰에서 정보 추출
- **JwtProperties**: application.properties에서 설정값 주입

#### Spring Security 설정
- **SecurityConfig 구현**:
  - Stateless 세션 관리 (JWT 사용)
  - CSRF 비활성화
  - URL별 권한 설정 (인증 필요/불필요 구분)
  - CORS 설정
- **JwtAuthenticationFilter**:
  - 모든 HTTP 요청에 대해 JWT 검증
  - Authorization 헤더에서 Bearer 토큰 추출
  - 유효한 토큰이면 SecurityContext에 인증 정보 저장
  - 필터 체인을 통한 요청 처리

#### 회원 엔티티 확장
- **Member 엔티티 수정**:
  - `password` 필드 추가 (암호화된 비밀번호 저장)
  - `role` 필드 추가 (MemberRole Enum: ADMIN, EMPLOYEE)
  - 비밀번호 암호화: BCryptPasswordEncoder 사용
- **MemberRole Enum**: 관리자와 직원 역할 구분

#### 인증 API 구현
- **AuthService**:
  - `login()` - 이메일/비밀번호 검증 후 JWT 토큰 발급
  - 비밀번호 매칭: PasswordEncoder.matches() 사용
  - 이메일 인증 여부 확인
- **AuthController**:
  - `POST /api/auth/login` - 로그인 API
  - LoginRequest DTO (이메일, 비밀번호)
  - LoginResponse DTO (accessToken, refreshToken, 사용자 정보)

#### Frontend JWT 연동
- **LoginPage 수정**:
  - 실제 로그인 API 호출
  - JWT 토큰을 localStorage에 저장
  - 역할(ADMIN/EMPLOYEE)에 따라 페이지 분기
- **Axios Interceptor 설정**:
  - 요청 인터셉터: 모든 API 요청에 JWT 토큰 자동 첨부 (`Authorization: Bearer {token}`)
  - 응답 인터셉터: 401 에러 시 자동 로그아웃 및 로그인 페이지 리다이렉트
- **axiosConfig.js 생성**: Axios 인스턴스 중앙 관리

### 📧 이메일 발송 시스템

#### Spring Boot Mail 통합
- **의존성 추가**: `spring-boot-starter-mail`
- **SMTP 설정**: application.properties에 이메일 서버 설정

#### 멀티 SMTP 서비스 지원
- **Gmail SMTP**: TLS/STARTTLS 방식, 앱 비밀번호 필요
- **Naver SMTP**: SSL 방식, 네이버 계정 비밀번호 사용
- **Kakao(Daum) SMTP**: SSL 방식, Daum 계정 비밀번호 사용

#### MailConfig 구현
- **MailProperties**: 사용할 이메일 제공자 선택 (`app.mail.provider`)
- **JavaMailSender 동적 설정**:
  - provider 값에 따라 Gmail/Naver/Kakao SMTP 자동 설정
  - 각 서비스별 host, port, 보안 설정 분리
- **환경 변수 사용**: 민감한 정보(이메일, 비밀번호)를 환경 변수로 관리

#### EmailService 구현
- **텍스트 이메일 발송**: `sendVerificationCode()`
- **HTML 이메일 발송**: `sendVerificationCodeHtml()`
  - 브랜드 로고 포함
  - 시각적으로 개선된 이메일 템플릿
  - 인증 코드 강조 표시
- **MemberService 연동**: 회원가입 시 실제 이메일 발송

#### VS Code 환경 설정
- **launch.json**: 디버깅 시 환경 변수 설정
- **.env 파일**: 민감 정보 분리 관리
- **.gitignore**: .env 파일 Git 커밋 방지

### 🔧 기술 작업 및 버그 수정

#### TDD 테스트 리팩토링
- **MemberServiceTest 수정**:
  - PasswordEncoder Mock 추가
  - 회원가입 시그니처 변경 반영
  - `setVerificationCodeForTest()` 메서드로 테스트 안정화
  - 랜덤 인증 코드 문제 해결
- **테스트 통과 확인**: 모든 단위 테스트 초록불 ✅

#### 의존성 관리
- **JWT 라이브러리**: io.jsonwebtoken:jjwt 추가
- **Spring Security**: spring-boot-starter-security 추가
- **Spring Mail**: spring-boot-starter-mail 추가
- **Gradle 빌드 검증**: 모든 의존성 정상 작동 확인

#### 코드 품질 개선
- **주석 추가**: 모든 주요 메서드에 JavaDoc 스타일 주석 작성
- **예외 처리**: IllegalArgumentException으로 명확한 에러 메시지 제공
- **비즈니스 로직 분리**: 도메인 객체가 자신의 로직을 책임지도록 설계

### 📝 배운 점

#### 보안
- **JWT의 구조와 동작 원리**: Header, Payload, Signature의 역할 이해
- **Stateless 인증**: 서버가 세션을 유지하지 않고 토큰만으로 인증
- **PasswordEncoder**: 평문 비밀번호를 절대 저장하지 않는 보안 원칙
- **환경 변수의 중요성**: 민감한 정보를 코드에서 분리하여 관리

#### Spring Security
- **필터 체인**: 요청이 컨트롤러에 도달하기 전 인증/인가 처리
- **SecurityContext**: 현재 요청의 인증 정보를 저장하는 공간
- **권한 관리**: URL 패턴별로 접근 권한 세밀하게 제어

#### 이메일 발송
- **SMTP 프로토콜**: 이메일 전송 표준 프로토콜 이해
- **Gmail 앱 비밀번호**: 2단계 인증 활성화 후 별도 비밀번호 생성 필요
- **HTML 이메일**: MIME 타입을 이용한 시각적 이메일 작성
- **다양한 SMTP 서비스**: 각 서비스별 설정 차이점 학습

#### 테스트
- **Mock 객체의 한계**: 랜덤 값이나 외부 의존성은 테스트 전용 메서드 필요
- **테스트 격리**: 각 테스트가 독립적으로 실행되도록 설계
- **given-when-then 패턴**: 명확한 테스트 구조 유지

#### 프론트엔드
- **Axios Interceptor**: 공통 로직을 중앙화하여 코드 중복 제거
- **토큰 자동 첨부**: 매번 헤더를 설정하지 않아도 되는 편리함
- **401 에러 처리**: 토큰 만료 시 자동 로그아웃으로 UX 개선


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
- [x] 이메일 인증 시스템 (Mock → 실제 SMTP)
- [x] 재고 관리 대시보드
- [x] 재고 증감 UI (+/- 버튼)
- [x] 상품 등록 폼
- [x] 카테고리별 검색 및 필터링
- [x] 직원 목록 조회, 등록, 수정, 삭제
- [x] 직원 검색 및 필터링 (이름, 사원번호, 이메일, 부서)
- [x] 상품 목록 조회, 등록, 수정, 삭제
- [x] JWT 기반 인증
- [x] Spring Security 적용
- [x] 실제 이메일 발송 (SMTP - Gmail/Naver/Kakao)
- [x] DDD 아키텍처 구조
- [x] 예외 처리 시스템
- [x] 단위 테스트 환경 (TDD)

### 진행 중 (🚧)
- 없음

### 예정 (📋)
- 페이징 처리 (상품, 직원 목록)
- API 문서화 (Swagger/OpenAPI)
- 파일 업로드 (프로필 사진, 상품 이미지)
- PostgreSQL/MySQL 전환
- Docker 컨테이너화
- AWS 배포 및 CI/CD

---

## 🎯 다음 단계

### 단기 목표 (1주일)
1. 페이징 처리 (상품 및 직원 목록)
2. API 문서 자동화 (Swagger/SpringDoc 통합)
3. 프로필 이미지 업로드 기능

### 중기 목표 (1개월)
1. Refresh Token 갱신 로직 구현
2. 비밀번호 찾기/재설정 기능
3. 관리자 권한 관리 페이지
4. 엑셀 다운로드 기능

### 장기 목표 (3개월)
1. PostgreSQL/MySQL 전환
2. Docker 컨테이너화
3. AWS 배포 (EC2, RDS, S3)
4. CI/CD 파이프라인 (GitHub Actions)
5. 모니터링 및 로깅 시스템 (Actuator, ELK Stack)

---

**마지막 업데이트**: 2025-12-16