# 🏢 StaffSync - HR Management System

Spring Boot와 React를 활용한 직원 관리 시스템(HRMS) MVP 프로젝트입니다.
직원들의 정보를 등록, 조회, 삭제할 수 있는 기능을 제공합니다.

## 🛠 Tech Stack
- **Frontend:** React, Vite, Tailwind CSS (v3), Axios
- **Backend:** Java 17, Spring Boot, JPA, H2 Database
- **Tool:** IntelliJ, VS Code

## 🚀 Key Features
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
  ```bash
  npm install -D tailwindcss@3 postcss autoprefixer