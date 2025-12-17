# 🏢 StaffSync

> HR Management System - 직원 및 재고 관리 통합 시스템

Spring Boot와 React를 활용한 System Integration 프로젝트입니다.  
DDD(Domain-Driven Design) 아키텍처를 적용하여 확장 가능한 구조로 설계되었습니다.

---

## 🛠 기술 스택

### Frontend
- React 18+ (Vite)
- Tailwind CSS v3
- React Router v6
- Axios (Interceptor 패턴)

### Backend
- Java 25 (LTS)
- Spring Boot 4.0
- Spring Data JPA
- Spring Security 7.0 + JWT
- Spring Mail (SMTP)
- H2 Database (In-Memory)
- Lombok

### Testing
- JUnit 5
- Mockito (TDD)
- Spring Boot Test

---

## 🎯 핵심 기능

### 🔐 회원 관리 및 인증
- **이메일 인증 기반 회원가입**
  - 6자리 랜덤 인증 코드 발송
  - 실제 Gmail/Naver/Kakao SMTP 연동
  - HTML 템플릿 이메일 (브랜드 디자인)
  - 10분 만료 시간 + 재발송 기능
- **JWT 기반 인증**
  - Access Token (1시간) + Refresh Token (7일)
  - Spring Security 통합
  - 역할 기반 권한 관리 (ADMIN, EMPLOYEE)
- **로그인/로그아웃**
  - 탭 방식 UI (관리자/직원 구분)
  - 자동 토큰 갱신
- **보안 기능**
  - 비밀번호 BCrypt 암호화
  - 환경 변수 기반 설정 (.gitignore 적용)

### 📦 재고 관리
- **실시간 재고 현황 대시보드**
  - 4개의 통계 카드 (전체/충분/부족/품절)
  - 총 재고 가치 계산
  - 재고 부족 알림 배너
- **재고 상태 자동 분류**
  - 재고 충분 (초록색)
  - 재고 부족 (노란색)
  - 품절 (빨간색)
- **상품 CRUD**
  - 등록, 조회, 수정, 삭제
  - 카테고리별 검색
  - 상품명 검색

### 👥 직원 관리
- **직원 정보 관리**
  - 사원번호 자동 생성 (EMP001, EMP002...)
  - 직원 등록/수정/삭제
  - 이메일 중복 검증
- **검색 및 필터링**
  - 이름, 사원번호, 이메일 통합 검색
  - 부서별 필터링

---

## 🏗 아키텍처

### DDD (Domain-Driven Design) 구조

```
backend/src/main/java/com/staffsync/
├── domain/              # 도메인 계층 - 비즈니스 로직
│   ├── member/         # 회원 엔티티, 역할(Role), Repository
│   ├── employee/       # 직원 엔티티, Repository
│   └── product/        # 상품 엔티티, 재고 상태, Repository
├── application/         # 애플리케이션 계층 - 유스케이스
│   ├── auth/           # 인증 서비스 (로그인, JWT 발급)
│   ├── member/         # 회원 서비스 (가입, 인증)
│   ├── employee/       # 직원 서비스
│   └── product/        # 상품 서비스
├── infrastructure/      # 인프라 계층 - 기술 구현
│   ├── security/       # JWT, Spring Security 설정
│   ├── mail/           # 이메일 발송 (SMTP)
│   └── persistence/    # JPA Repository 구현
└── interfaces/          # 인터페이스 계층 - API
    ├── auth/           # 인증 API
    ├── member/         # 회원 API
    ├── employee/       # 직원 API
    └── product/        # 상품 API
```

**계층별 역할**
- **Domain**: 핵심 비즈니스 로직과 엔티티
- **Application**: 도메인을 조합한 서비스 로직
- **Infrastructure**: 기술 구현 (DB, Mail, Security)
- **Interfaces**: REST API Controller

---

## 🚀 시작하기

### 사전 요구사항
- Java 25 (LTS)
- Node.js 18+
- Git

### 1. 프로젝트 클론

```bash
git clone https://github.com/your-username/HRsystem.git
cd HRsystem
```

### 2. 환경 변수 설정

#### Gmail 앱 비밀번호 생성

1. [Google 계정 관리](https://myaccount.google.com/) 접속
2. **보안** → **2단계 인증** 활성화
3. **앱 비밀번호** 생성:
   - 앱: 메일
   - 기기: 기타 (StaffSync)
4. 16자리 비밀번호 복사 (공백 제거)

#### launch.json 설정 (VS Code)

**파일**: `backend/.vscode/launch.json.example`을 복사:

```bash
cd backend/.vscode
cp launch.json.example launch.json
```

**파일**: `backend/.vscode/launch.json` 수정:

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "StaffSync Application",
      "request": "launch",
      "mainClass": "com.staffSync.StaffSyncApplication",
      "projectName": "backend",
      "env": {
        "JWT_SECRET": "your-jwt-secret-key-here",
        "MAIL_FROM": "your-email@gmail.com",
        "MAIL_USERNAME": "your-email@gmail.com",
        "MAIL_PASSWORD": "your-16-digit-app-password"
      }
    }
  ]
}
```

### 3. Backend 실행

#### VS Code에서 실행 (권장)

```
1. VS Code에서 backend 폴더 열기
2. Run and Debug (Ctrl + Shift + D)
3. "StaffSync Application" 선택
4. F5 누르기
```

#### 터미널에서 실행 (환경 변수 수동 설정 필요)

```bash
cd backend

# Windows PowerShell
$env:MAIL_USERNAME="your-email@gmail.com"
$env:MAIL_PASSWORD="your-app-password"
./gradlew bootRun

# Mac/Linux
export MAIL_USERNAME="your-email@gmail.com"
export MAIL_PASSWORD="your-app-password"
./gradlew bootRun
```

서버: `http://localhost:8080`

### 4. Frontend 실행

```bash
cd frontend
npm install
npm run dev
```

클라이언트: `http://localhost:5173`

### 5. H2 Console (선택)

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (공백)

---

## 🧪 테스트 실행

```bash
cd backend
./gradlew test
```

**테스트 리포트**: `backend/build/reports/tests/test/index.html`

---

## 📧 이메일 제공자 변경

**파일**: `backend/src/main/resources/application.properties`

```properties
# Gmail (기본)
app.mail.provider=gmail

# Naver
app.mail.provider=naver

# Kakao
app.mail.provider=kakao
```

각 제공자별 환경 변수:
- **Gmail**: `MAIL_USERNAME`, `MAIL_PASSWORD`
- **Naver**: `NAVER_MAIL_USERNAME`, `NAVER_MAIL_PASSWORD`
- **Kakao**: `KAKAO_MAIL_USERNAME`, `KAKAO_MAIL_PASSWORD`

---

## 🔒 보안 주의사항

### Git에 커밋하지 말아야 할 파일
- `.vscode/launch.json` (환경 변수 포함)
- `.env` (환경 변수 파일)
- `application.properties` (실제 비밀번호 포함 시)

### .gitignore 확인
```gitignore
# VS Code
.vscode/launch.json

# Environment variables
.env
.env.local
*.env

# Application properties (선택)
# application.properties
```

### 팀원과 공유할 파일
- `launch.json.example` (템플릿)
- `.env.example` (템플릿)
- README.md (설정 가이드)

---

## 📝 API 문서

### 인증 API
- `POST /api/auth/login` - 로그인

### 회원 API
- `POST /api/members/send-code` - 인증 코드 발송
- `POST /api/members/verify-code` - 인증 코드 검증
- `GET /api/members/code-time` - 남은 시간 조회
- `POST /api/members/register` - 회원가입

### 직원 API
- `GET /api/employees` - 직원 목록 조회
- `GET /api/employees/{id}` - 직원 상세 조회
- `POST /api/employees` - 직원 등록
- `PUT /api/employees/{id}` - 직원 수정
- `DELETE /api/employees/{id}` - 직원 삭제

### 상품 API
- `GET /api/products` - 상품 목록 조회
- `GET /api/products/{id}` - 상품 상세 조회
- `GET /api/products/dashboard` - 대시보드 통계
- `GET /api/products/low-stock` - 재고 부족 상품
- `POST /api/products` - 상품 등록
- `PUT /api/products/{id}` - 상품 수정
- `DELETE /api/products/{id}` - 상품 삭제
- `PATCH /api/products/{id}/stock/increase` - 재고 증가
- `PATCH /api/products/{id}/stock/decrease` - 재고 감소

---

## 📚 참고 문서

- [개발 로그](./DEVLOG.md) - 날짜별 기능 개발 히스토리
- [트러블슈팅](./TROUBLESHOOTING.md) - 에러 해결 과정 기록