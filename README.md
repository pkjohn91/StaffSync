# 🏢 StaffSync - HR Management System

Spring Boot와 React를 활용한 직원 관리 시스템(HRMS) MVP 프로젝트입니다.
직원들의 정보를 등록, 조회, 삭제할 수 있는 기능을 제공합니다.

## 🛠 기술 스택
- **Frontend:** React, Vite, Tailwind CSS (v3), Axios
- **Backend:** Java 17, Spring Boot, JPA, H2 Database
- **Tool:** VS Code

## 🚀 핵심 구성
- 직원 목록 조회 (Read)
- 신규 직원 등록 (Create)
- 직원 정보 삭제 (Delete) - Optimistic UI 적용

---

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
