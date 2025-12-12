# 🐛 StaffSync 트러블슈팅 로그

프로젝트 개발 과정에서 발생한 모든 에러와 해결 과정을 기록한 문서입니다.

---

## 📅 2025-12-09 트러블슈팅

### 1. Tailwind CSS 버전 충돌 문제

**문제**
```
PostCSS plugin error: Cannot find module 'tailwindcss'
Vite 서버 실행 불가
```

**원인**
- Tailwind CSS v4 (최신 버전)와 Vite의 PostCSS 설정 방식이 호환되지 않음
- v4는 새로운 설정 방식을 사용하나, 프로젝트는 기존 v3 방식으로 구성됨

**해결**
```bash
# 기존 설치 제거
npm uninstall tailwindcss

# v3으로 특정 버전 설치
npm install -D tailwindcss@^3.0.0 postcss autoprefixer

# 설정 파일 수동 생성
npx tailwindcss init -p
```

**결과**: ✅ Vite 개발 서버 정상 실행

---

### 2. Axios 의존성 누락

**문제**
```
Failed to run dependency scan... axios could not be resolved
Module not found: Can't resolve 'axios'
```

**원인**
- 코드에서 `import axios from 'axios'` 사용
- `package.json`에 axios 라이브러리가 설치되지 않음

**해결**
```bash
npm install axios
```

**결과**: ✅ API 통신 정상 작동

---

### 3. npm 캐시 및 실행 파일 오류

**문제**
```
npm install 실행 시:
could not determine executable to run
Error: ENOENT: no such file or directory
```

**원인**
- Windows 환경에서 NPM 캐시가 손상됨
- 이전 설치 파일의 잔존으로 인한 충돌

**해결**
```bash
# node_modules 및 lock 파일 삭제
rm -rf node_modules package-lock.json

# npm 캐시 클리어
npm cache clean --force

# 클린 설치
npm install
```

**결과**: ✅ 의존성 설치 정상 완료

---

### 4. CORS 정책 위반

**문제**
```
Access to XMLHttpRequest at 'http://localhost:8080/api/members'
from origin 'http://localhost:5173' has been blocked by CORS policy
Network Error
```

**원인**
- 브라우저의 Same-Origin Policy로 인해 다른 포트(5173 → 8080) 간 통신 차단
- Spring Boot Controller에 CORS 설정 누락

**해결**
```java
@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "http://localhost:5173")  // 추가
public class MemberController {
    // ...
}
```

**결과**: ✅ 프론트엔드-백엔드 통신 정상화

---

## 📅 2025-12-11 트러블슈팅

### 1. Spring Boot 엔티티 인식 오류

**문제**
```
Not a managed type: class com.staffsync.domain.member.Member
org.springframework.beans.factory.BeanCreationException
Application failed to start
```

**원인**
- `Member` 클래스에 `@Entity` 어노테이션 누락
- 또는 엔티티가 메인 애플리케이션 클래스(`@SpringBootApplication`)보다 상위 패키지에 위치

**해결**
```java
// Member.java
@Entity  // 추가!
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    // ...
}
```

**패키지 구조 확인**:
```
com.staffsync.StaffSyncApplication  ← 메인 클래스
com.staffsync.domain.member.Member  ← 엔티티 (하위 패키지 ✅)
```

**결과**: ✅ JPA 엔티티 정상 인식

---

### 2. H2 Database 테이블 생성 실패

**문제**
```
SQLSyntaxErrorException: Table "MEMBER" not found [42102-200]
500 Internal Server Error
```

**원인**
- `application.properties`에 JPA DDL 설정 누락
- Hibernate가 서버 시작 시 테이블을 자동 생성하지 않음

**해결**
```properties
# application.properties에 추가
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

**DDL 옵션 설명**:
- `create`: 서버 시작마다 테이블 재생성 (기존 데이터 삭제)
- `update`: 변경사항만 반영 (데이터 유지)
- `validate`: 스키마 검증만 수행
- `none`: 아무것도 하지 않음

**결과**: ✅ H2 테이블 자동 생성 완료

---

### 3. 중복 가입 시 500 에러 발생

**문제**
```
POST /api/members/register
500 Internal Server Error
"이미 가입된 이메일입니다" 메시지가 사용자에게 전달되지 않음
```

**원인**
- 비즈니스 로직에서 `IllegalArgumentException` 발생
- 별도의 예외 처리 핸들러가 없어 서버 에러(500)로 처리됨

**해결**
```java
// GlobalExceptionHandler.java 생성
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
```

**결과**: ✅ 400 Bad Request + 명확한 에러 메시지 반환

---

### 4. TDD 실행 시 Java Agent 경고

**문제**
```
WARNING: Mockito is currently self-attaching to enable the inline mock maker.
This will no longer work in future releases of the JVM.
```

**원인**
- Java 21부터 외부 에이전트(Mockito)의 동적 로딩을 제한하는 보안 정책 강화
- Mockito가 런타임에 바이트코드를 조작하기 위해 에이전트를 로드하려 함

**해결**
```kotlin
// build.gradle.kts
tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("-XX:+EnableDynamicAgentLoading")  // 추가
}
```

**결과**: ✅ 경고 없이 테스트 정상 실행

---

### 5. Mockito Stubbing 오류 (Unit Test)

**문제**
```java
@Test
void requestVerification_Success() {
    // 테스트 실패: "이미 가입된 이메일입니다"
}
```

**원인**
```java
// 잘못된 Stubbing
given(memberRepository.existsByEmail(email)).willReturn(true);  // ❌
// → 테스트가 "이미 존재하는 이메일"로 인식
```

**해결**
```java
// 올바른 Stubbing
given(memberRepository.existsByEmail(email)).willReturn(false);  // ✅
// → "존재하지 않는 이메일"로 정상 테스트
```

**결과**: ✅ 단위 테스트 통과

---

## 📅 2025-12-12 트러블슈팅

### 1. ProductRepository 오타 에러

**문제**
```
org.springframework.data.repository.query.QueryCreationException:
No property 'satatus' found for type 'Product'; Did you mean 'status'

Execution failed for task ':bootRun'.
Process finished with non-zero exit value 1
```

**원인**
```java
// ProductRepository.java
List<Product> findBySatatus(StockStatus status);  // ❌ satatus (오타)
long countBySatatus(StockStatus status);          // ❌ satatus (오타)
```
- Spring Data JPA가 메서드 이름을 분석하여 쿼리 자동 생성
- `satatus`라는 필드가 `Product` 엔티티에 존재하지 않음
- 실제 필드명은 `status`

**해결**
```java
// ProductRepository.java 수정
List<Product> findByStatus(StockStatus status);   // ✅ status
long countByStatus(StockStatus status);           // ✅ status
```

**Spring Data JPA 메서드 네이밍 규칙**:
- `findBy필드명`: 해당 필드로 조회
- `countBy필드명`: 개수 세기
- 필드명은 **대소문자를 정확히 일치**시켜야 함

**결과**: ✅ 서버 정상 실행 완료

---

### 2. React Router 의존성 누락

**문제**
```
Module not found: Can't resolve 'react-router-dom'
Failed to compile
```

**원인**
- 코드에서 `import { useNavigate } from 'react-router-dom'` 사용
- `package.json`에 react-router-dom 설치되지 않음

**해결**
```bash
cd frontend
npm install react-router-dom
```

**결과**: ✅ 라우팅 기능 정상 작동

---

### 3. 로그인 없이 대시보드 접근 문제

**문제**
- 로그인하지 않은 사용자도 URL 직접 입력으로 `/dashboard` 접근 가능
- 보안 취약점 발생

**원인**
- 클라이언트 사이드 라우팅만 존재
- 인증 상태 체크 로직 부재

**해결**
```jsx
// ProtectedRoute.jsx 생성
import { Navigate } from 'react-router-dom';

const ProtectedRoute = ({ children }) => {
  const user = localStorage.getItem('user');
  
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  
  return children;
};

export default ProtectedRoute;
```

```jsx
// App.jsx에 적용
<Route 
  path="/dashboard" 
  element={
    <ProtectedRoute>
      <DashboardPage />
    </ProtectedRoute>
  } 
/>
```

**결과**: ✅ 미인증 사용자 자동 로그인 페이지로 리다이렉트

---

## 🎓 트러블슈팅을 통해 배운 점

### 1. 의존성 관리의 중요성
- `package.json`과 코드의 일관성 유지
- 버전 호환성 체크 필수

### 2. CORS의 이해
- SPA와 REST API 분리 시 필수 설정
- 프로덕션에서는 보안을 고려한 세밀한 설정 필요

### 3. Spring Data JPA 메서드 네이밍
- 필드명과 정확히 일치해야 함
- 오타는 컴파일 에러가 아닌 런타임 에러 발생

### 4. 예외 처리 전략
- 비즈니스 예외는 적절한 HTTP 상태코드로 변환
- 사용자에게 명확한 에러 메시지 전달

### 5. TDD의 가치
- 프로덕션 코드 작성 전 테스트 작성
- Mock 객체의 올바른 Stubbing 중요

### 6. 보안의 기본
- 클라이언트 사이드 검증만으로는 불충분
- 서버 사이드 인증/인가 필수

---

## 🔍 디버깅 팁

### Backend (Spring Boot)
1. **로그 확인**: `application.properties`에 `spring.jpa.show-sql=true` 설정
2. **H2 Console**: 데이터베이스 상태 직접 확인
3. **Postman/Insomnia**: API 단독 테스트
4. **IntelliJ Debugger**: 브레이크포인트로 실행 흐름 추적

### Frontend (React)
1. **Chrome DevTools**: Network 탭에서 API 요청/응답 확인
2. **Console**: `console.log()`로 상태 추적
3. **React DevTools**: 컴포넌트 상태 실시간 확인
4. **Axios Interceptor**: 요청/응답 로깅

### 일반적인 디버깅 순서
1. 에러 메시지 정확히 읽기
2. 스택 트레이스 분석
3. 구글 검색 (영어로 에러 메시지 검색)
4. 공식 문서 참고
5. 로그 추가하여 실행 흐름 파악
6. 최소 재현 코드 작성

---

## 💡 자주 발생하는 에러 예방법

### 1. 코드 작성 전 확인사항
- [ ] 의존성이 `package.json` / `build.gradle.kts`에 추가되었는가?
- [ ] 어노테이션 (`@Entity`, `@Repository` 등) 누락은 없는가?
- [ ] 환경 설정 파일(`application.properties`)은 올바른가?

### 2. 커밋 전 체크리스트
- [ ] 백엔드 서버 정상 실행 확인
- [ ] 프론트엔드 빌드 에러 없음
- [ ] 주요 기능 수동 테스트 완료
- [ ] 테스트 코드 작성 및 통과

### 3. 코드 리뷰 포인트
- [ ] 오타 확인 (특히 메서드명, 필드명)
- [ ] 예외 처리 추가
- [ ] CORS 설정 확인
- [ ] 불필요한 콘솔 로그 제거

---

## 📚 참고 자료

### 공식 문서
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [React Router](https://reactrouter.com/en/main)
- [Tailwind CSS](https://tailwindcss.com/docs)

### 유용한 링크
- [Stack Overflow](https://stackoverflow.com/)
- [Baeldung - Spring Tutorials](https://www.baeldung.com/)
- [MDN Web Docs](https://developer.mozilla.org/)

---

**마지막 업데이트**: 2025-12-12
