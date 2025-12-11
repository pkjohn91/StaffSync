# 🏢 StaffSync - HR Management System

Spring Boot와 React를 활용한 직원 관리 시스템(HRMS) MVP 프로젝트입니다.
직원들의 정보를 등록, 조회, 삭제할 수 있는 기능을 제공합니다.

## 🛠 기술 스택
- **Frontend:** React, Vite, Tailwind CSS (v3), Axios
- **Backend:** Java 25(LTS), Spring Boot, JPA, H2 Database
- **Tool:** VS Code

## 🚀 핵심 구성
- 직원 목록 조회 (Read)
- 신규 직원 등록 (Create)
- 직원 정보 삭제 (Delete) - Optimistic UI 적용
- 이메일 검증 (Validation) 기능

---

## 📅 2025-12-09 개발 로그


## 🐛 트러블 슈팅 로그 (Troubleshooting Log)
개발 과정에서 발생한 주요 에러와 해결 과정을 기록했습니다.

### 1. Tailwind CSS 버전 충돌 문제
- **문제:** Vite 환경에서 Tailwind CSS 설치 시 `PostCSS plugin` 관련 에러 발생하며 서버 실행 불가.
- **원인:** Tailwind CSS v4(최신)와 기존 v3 설정 방식(PostCSS) 간의 호환성 문제.
- **해결:** 안정적인 `v3` 버전으로 특정하여 재설치하고 설정 파일을 수동 생성함.

### 2. 의존성 누락 (Axios)
- **문제:** `Failed to run dependency scan... axios could not be resolved` 에러 발생.
- **원인:** 코드에서는 `import axios`를 사용했으나, `package.json`에 라이브러리가 설치되지 않음.
- **해결:** `npm install axios` 명령어로 의존성 추가 후 서버 재시작.

### 3. npm 캐시 및 실행 파일 오류
- **문제:** `npm install` 중 `could not determine executable to run` 에러 발생.
- **원인:** Windows 환경에서 NPM 캐시가 꼬이거나 이전 설치 파일이 잔존함.
- **해결:** `node_modules` 및 `package-lock.json` 삭제 후 `npm cache clean --force` 실행하여 클린 설치 진행.

### 4. CORS 정책 위반
- **문제:** 프론트엔드(5173포트)에서 백엔드(8080포트)로 API 요청 시 `Network Error` 발생.
- **원인:** 브라우저의 보안 정책(Same-Origin Policy)으로 인해 다른 포트 간 통신 차단.
- **해결:** Spring Boot Controller에 `@CrossOrigin(origins = "http://localhost:5173")` 어노테이션 추가.

---

## 📅 2025-12-11 개발 로그

### 주요 기능 추가
#### 1. Backend (Spring Boot & DDD)
- **DDD 패키지 구조 도입:** `domain`, `application`, `interfaces`, `infrastructure` 레이어 분리.
- **이메일 인증 로직 구현:** 가짜(Mock) 이메일 발송 서비스 구현 및 검증 로직 작성.
- **TDD (테스트 주도 개발) 환경 구축:** JUnit5 & Mockito를 활용하여 `MemberService` 단위 테스트 작성.
- **예외 처리 고도화:** `GlobalExceptionHandler`를 도입하여 500 에러를 400(Bad Request)으로 명확하게 변환.
- **리팩토링:** `Controller`가 `EmailService`를 의존하지 않고 `MemberService`를 거치도록 구조 개선.

#### 2. Frontend (React & Tailwind)
- **회원가입 페이지 UI 구현:** `RegisterPage.jsx` 컴포넌트 생성.
- **단계별 가입 UX (Step-by-Step):** 이메일 인증이 완료되어야 이름/비밀번호 입력칸이 활성화되도록 처리.
- **실시간 유효성 검사:** 정규표현식(Regex)을 활용한 이메일 형식 검사 및 버튼 활성화/비활성화 로직 적용.
- **API 연동:** 인증 코드 요청, 검증, 최종 가입 API 연결 완료.


## 🐛 트러블 슈팅 (Troubleshooting)

### 1. Spring Boot 엔티티 인식 오류
- **문제:** `Not a managed type: class ... Member` 에러 발생하며 서버 실행 실패.
- **원인:** `Member` 클래스에 `@Entity` 어노테이션이 누락되었거나, 메인 애플리케이션보다 상위 패키지에 위치함.
- **해결:** `@Entity` 어노테이션 추가 및 패키지 구조 확인 (`backend` 실행 클래스 하위로 이동).

### 2. H2 Database 테이블 생성 실패
- **문제:** API 호출 시 `SQLSyntaxErrorException: Table "MEMBER" not found` (500 에러) 발생.
- **원인:** `application.properties`에 JPA DDL 설정이 누락되어 서버 시작 시 테이블이 생성되지 않음.
- **해결:** `spring.jpa.hibernate.ddl-auto=update` 설정을 추가하여 자동 생성 활성화.

### 3. 중복 가입 시 500 에러 발생
- **문제:** 이미 가입된 이메일로 요청 시 `Internal Server Error` 발생.
- **원인:** 비즈니스 로직에서 `IllegalArgumentException`을 던졌으나, 별도의 핸들러가 없어 서버 에러로 처리됨.
- **해결:** `GlobalExceptionHandler`(@RestControllerAdvice)를 구현하여 400 Bad Request와 명확한 메시지를 반환하도록 수정.

### 4. TDD 실행 시 Java Agent 경고
- **문제:** Mockito 테스트 실행 시 `Mockito is currently self-attaching...` 경고 메시지 출력.
- **원인:** Java 21부터 외부 에이전트(Mockito)의 동적 로딩을 제한하는 보안 정책 강화.
- **해결:** `build.gradle.kts`의 테스트 옵션에 `-XX:+EnableDynamicAgentLoading` 추가.

### 5. Mockito Stubbing 오류 (Unit Test)
- **문제:** `requestVerification_Success` 테스트 실패 (원인: "이미 가입된 이메일입니다").
- **원인:** Mock Repository(`given(...)`)가 `existsByEmail` 호출에 대해 `true`(중복됨)를 리턴하도록 잘못 설정됨.
- **해결:** `given(memberRepository.existsByEmail(email)).willReturn(false);`로 수정하여 중복이 아님을 명시.
