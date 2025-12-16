# 🐛 StaffSync 트러블슈팅 로그

프로젝트 개발 과정에서 발생한 모든 에러와 해결 과정을 기록한 문서입니다.

---

## 🚀 바로가기

- [2025-12-16 (Day 5)](#-2025-12-16-트러블슈팅)
- [2025-12-12 (Day 3)](#-2025-12-12-트러블슈팅)
- [2025-12-11 (Day 2)](#-2025-12-11-트러블슈팅)
- [2025-12-09 (Day 1)](#-2025-12-09-트러블슈팅)

---

## 📅 2025-12-16 트러블슈팅

### 1. JwtAuthenticationFilter 클래스 임포트 실패

**문제**
```
SecurityConfig.java에서:
Cannot resolve symbol 'JwtAuthenticationFilter'
컴파일 에러 발생
```

**원인**
- `SecurityConfig.java`를 먼저 작성한 후 `JwtAuthenticationFilter.java`를 작성
- IDE가 아직 생성되지 않은 클래스를 참조하려고 시도
- 파일 생성 순서 문제

**해결**
```java
// 1단계: JwtAuthenticationFilter.java 먼저 생성
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    // ...
}

// 2단계: 그 다음 SecurityConfig.java 작성
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;  // ✅ 정상 인식
    // ...
}

// 3단계: IDE 새로고침 (필요시)
// IntelliJ: File → Invalidate Caches → Restart
// VS Code: Ctrl+Shift+P → Java: Clean Workspace
```

**올바른 파일 생성 순서**:
1. `JwtProperties.java` (설정 클래스)
2. `JwtTokenProvider.java` (토큰 생성/검증)
3. `JwtAuthenticationFilter.java` (필터)
4. `SecurityConfig.java` (Security 설정)

**결과**: ✅ 컴파일 성공 및 Spring Security 정상 작동

---

### 2. MemberService.verificationCodes.remove() 에러

**문제**
```java
// MemberService.java
@Transactional
public void register(String email, String name, String password, String code) {
    // ...
    memberRepository.save(member);
    
    // 6. 인증 코드 삭제
    memberRepository.remove(email);  // ❌ 컴파일 에러
}
```

**에러 메시지**:
```
Cannot resolve method 'remove' in 'MemberRepository'
```

**원인**
- `MemberRepository`는 JPA Repository 인터페이스로, `remove()` 메서드가 존재하지 않음
- `verificationCodes` Map과 `memberRepository`를 혼동
- 인증 코드는 인메모리 Map에 저장되어 있음

**해결**
```java
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final Map<String, String> verificationCodes = new HashMap<>();  // ← 이것!
    
    @Transactional
    public void register(String email, String name, String password, String code) {
        // ...
        memberRepository.save(member);
        
        // ✅ 올바른 수정
        verificationCodes.remove(email);  // Map에서 삭제
    }
}
```

**JPA Repository와 Map의 차이**:
| 구분 | MemberRepository | verificationCodes |
|------|------------------|-------------------|
| 타입 | JpaRepository 인터페이스 | HashMap<String, String> |
| 저장소 | 데이터베이스 (H2) | 메모리 (JVM Heap) |
| 메서드 | save(), findById(), delete() 등 | put(), get(), remove() 등 |
| 용도 | 영구 데이터 저장 | 임시 데이터 저장 |

**결과**: ✅ 회원가입 완료 후 인증 코드 정상 삭제

---

### 3. MemberServiceTest 단위 테스트 실패

**문제**
```java
@Test
@DisplayName("회원가입 - 성공")
void register_Success() {
    // given
    String email = "valid@test.com";
    String code = "123456";
    
    memberService.requestVerification(email);  // 랜덤 코드 생성
    
    // when
    memberService.register(email, name, password, code);  // ❌ 실패!
    
    // then
    // ...
}
```

**에러 메시지**:
```
java.lang.IllegalArgumentException: 인증 코드가 일치하지 않습니다.
```

**원인**
- `requestVerification()` 메서드가 **랜덤 6자리 코드**를 생성
- 테스트에서는 `"123456"` 고정값을 사용
- 랜덤 코드와 고정 코드가 일치할 확률은 0.0001%

**첫 번째 시도 (실패)**:
```java
// ❌ 생성된 코드를 알 수 없음
memberService.requestVerification(email);  // 코드: "482719" (랜덤)
memberService.register(email, name, password, "123456");  // 불일치!
```

**해결 (테스트용 메서드 추가)**:
```java
// MemberService.java
@Service
public class MemberService {
    private final Map<String, String> verificationCodes = new HashMap<>();
    
    // ✅ 테스트용 메서드 추가
    public void setVerificationCodeForTest(String email, String code) {
        verificationCodes.put(email, code);
    }
    
    // ...
}
```

```java
// MemberServiceTest.java
@Test
@DisplayName("회원가입 - 성공")
void register_Success() {
    // given
    String email = "valid@test.com";
    String code = "123456";
    
    // ✅ 테스트용 코드 직접 설정
    memberService.setVerificationCodeForTest(email, code);
    
    given(memberRepository.existsByEmail(email)).willReturn(false);
    given(passwordEncoder.encode(password)).willReturn("encodedPassword");
    given(memberRepository.save(any(Member.class))).willReturn(savedMember);
    
    // when
    memberService.register(email, name, password, code);  // ✅ 성공!
    
    // then
    verify(memberRepository, times(1)).save(any(Member.class));
}
```

**배운 점**:
- 랜덤 값이나 외부 의존성이 있는 코드는 테스트가 어려움
- 테스트 전용 메서드나 주입 가능한 인터페이스 설계 필요
- 프로덕션 코드에 테스트용 메서드를 추가하는 것이 불가피한 경우도 있음

**결과**: ✅ 모든 테스트 케이스 통과 (초록불 🟢)

---

### 4. TDD 테스트 - register_Fail_DuplicateEmail 잘못된 예외 발생

**문제**
```java
@Test
@DisplayName("회원가입 - 실패: 이미 가입된 이메일이면 예외가 발생")
void register_Fail_DuplicateEmail() {
    // given
    String email = "duplicate@test.com";
    String code = "123456";
    
    memberService.requestVerification(email);  // 인증 코드 생성
    given(memberRepository.existsByEmail(email)).willReturn(true);  // 중복!
    
    // when & then
    assertThatThrownBy(() -> memberService.register(email, name, password, code))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("이미 가입된 이메일입니다.");  // ❌ 예상
}
```

**에러 메시지**:
```
예상: "이미 가입된 이메일입니다."
실제: "인증 코드가 일치하지 않습니다."
```

**원인 분석**:
```java
// MemberService.register() 메서드 실행 순서
public void register(String email, String name, String password, String code) {
    // 1. 인증 코드 검증 (먼저 실행!)
    if (!verifyCode(email, code)) {
        throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");  // ← 여기서 걸림!
    }
    
    // 2. 중복 체크 (실행 안 됨)
    if (memberRepository.existsByEmail(email)) {
        throw new IllegalArgumentException("이미 가입된 이메일입니다.");
    }
    // ...
}
```

- `requestVerification()`으로 랜덤 코드 생성 → 예: "748291"
- 테스트에서 `"123456"` 전달 → 불일치!
- 인증 코드 검증에서 먼저 예외 발생 → 중복 체크까지 도달 못 함

**해결**:
```java
@Test
@DisplayName("회원가입 - 실패: 이미 가입된 이메일이면 예외가 발생")
void register_Fail_DuplicateEmail() {
    // given
    String email = "duplicate@test.com";
    String code = "123456";
    
    // ✅ 1. 인증 코드를 먼저 설정 (인증 통과시킴)
    memberService.setVerificationCodeForTest(email, code);
    
    // ✅ 2. 그 다음 중복 설정
    given(memberRepository.existsByEmail(email)).willReturn(true);
    
    // when & then
    assertThatThrownBy(() -> memberService.register(email, name, password, code))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("이미 가입된 이메일입니다.");  // ✅ 정상!
}
```

**실행 흐름**:
```
1. verifyCode(email, "123456") → true ✅ (통과)
2. existsByEmail(email) → true → 예외 발생 ✅ (예상된 예외)
```

**결과**: ✅ 테스트 통과 및 정확한 예외 메시지 확인

---

### 5. ProductRepository.findByNameContainingIgnoreCase() 메서드 미정의

**문제**
```java
// ProductService.java
public List<ProductDto> searchProducts(String keyword) {
    return productRepository.findByNameContainingIgnoreCase(keyword)  // ❌ 컴파일 에러
        .stream()
        .map(ProductDto::from)
        .collect(Collectors.toList());
}
```

**에러 메시지**:
```
Cannot resolve method 'findByNameContainingIgnoreCase' in 'ProductRepository'
```

**원인**
- `ProductRepository` 인터페이스에 해당 메서드 선언이 없음
- Spring Data JPA는 메서드 이름을 보고 자동으로 쿼리를 생성하지만, 메서드 자체는 선언되어야 함

**해결**:
```java
// ProductRepository.java
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // ✅ 메서드 추가 (Spring Data JPA가 자동 구현)
    List<Product> findByNameContainingIgnoreCase(String keyword);
    
    // 기존 메서드들
    List<Product> findByCategory(String category);
    List<Product> findByStatus(StockStatus status);
    // ...
}
```

**Spring Data JPA 자동 구현 원리**:
```java
// 메서드 이름 분석
findBy + Name + Containing + IgnoreCase

// 생성되는 JPQL (자동)
SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
```

**메서드 네이밍 규칙**:
| 키워드 | 설명 | 예시 |
|--------|------|------|
| `findBy` | 조회 | `findByName()` |
| `Containing` | LIKE '%keyword%' | `findByNameContaining()` |
| `IgnoreCase` | 대소문자 무시 | `findByNameIgnoreCase()` |
| `StartingWith` | LIKE 'keyword%' | `findByNameStartingWith()` |
| `EndingWith` | LIKE '%keyword' | `findByNameEndingWith()` |
| `OrderBy` | 정렬 | `findByNameOrderByPriceDesc()` |

**결과**: ✅ 상품명 검색 기능 정상 작동

---

### 6. EmployeeEditPage에서 수정 버튼 클릭 시 로그인 페이지로 리다이렉트

**문제**
```
직원 목록에서 "수정" 버튼 클릭
→ /employees/edit/{id}로 이동 시도
→ 갑자기 /login으로 리다이렉트됨
```

**원인 분석**:
```jsx
// ProtectedRoute.jsx
const ProtectedRoute = ({ children }) => {
  const user = localStorage.getItem('user');
  
  if (!user) {
    console.log('❌ 인증 없음, 로그인 페이지로 리다이렉트');
    return <Navigate to="/login" replace />;
  }
  
  return children;
};
```

**가능한 원인들**:
1. localStorage의 `user` 키가 삭제됨
2. 페이지 이동 중 localStorage가 초기화됨
3. React Router의 상태 관리 문제
4. 브라우저 세션 만료

**디버깅 과정**:
```jsx
// EmployeeListPage.jsx - 수정 버튼
<button
  onClick={() => {
    console.log('수정 버튼 클릭, ID:', employee.id);
    console.log('현재 localStorage:', localStorage.getItem('user'));  // ← 확인
    navigate(`/employees/edit/${employee.id}`);
  }}
>
  수정
</button>
```

**해결 시도 1: ProtectedRoute 강화**
```jsx
import { Navigate, useLocation } from 'react-router-dom';

const ProtectedRoute = ({ children }) => {
  const location = useLocation();
  const userString = localStorage.getItem('user');
  
  console.log('=== ProtectedRoute 디버깅 ===');
  console.log('현재 경로:', location.pathname);
  console.log('localStorage user:', userString);
  
  if (!userString) {
    console.log('❌ 인증 없음 - 로그인 페이지로 리다이렉트');
    return <Navigate to="/login" state={{ from: location }} replace />;
  }
  
  try {
    const user = JSON.parse(userString);
    console.log('✅ 인증됨:', user);
    return children;
  } catch (error) {
    console.error('❌ user 데이터 파싱 에러:', error);
    localStorage.removeItem('user');  // 손상된 데이터 제거
    return <Navigate to="/login" replace />;
  }
};
```

**해결 시도 2: 라우팅 순서 확인**
```jsx
// App.jsx
<Routes>
  {/* ✅ 더 구체적인 경로를 먼저 */}
  <Route 
    path="/employees/edit/:id" 
    element={
      <ProtectedRoute>
        <EmployeeEditPage />
      </ProtectedRoute>
    } 
  />
  
  {/* ✅ 덜 구체적인 경로를 나중에 */}
  <Route 
    path="/employees" 
    element={
      <ProtectedRoute>
        <EmployeeListPage />
      </ProtectedRoute>
    } 
  />
</Routes>
```

**최종 해결**: 
- 브라우저 콘솔 로그 확인 결과, localStorage에 `user` 데이터가 정상 존재
- 실제 문제는 **다른 곳**에서 발생했을 가능성 (예: API 401 에러로 인한 Axios Interceptor의 자동 로그아웃)
- ProtectedRoute 디버깅 로그 추가로 문제 원인 파악 가능

**결과**: ✅ 수정 페이지 정상 접근 가능

---

### 7. Gmail SMTP 설정 시 "535 Authentication failed" 에러

**문제**
```
Caused by: javax.mail.AuthenticationFailedException: 
535-5.7.8 Username and Password not accepted.
```

**원인**
1. **2단계 인증 미활성화**: Gmail은 보안상 2단계 인증 필수
2. **앱 비밀번호 미생성**: 일반 Gmail 비밀번호는 SMTP에서 사용 불가
3. **잘못된 비밀번호**: 공백 포함 또는 오타

**해결 단계**:

**1단계: 2단계 인증 활성화**
```
1. Google 계정 관리 (https://myaccount.google.com/) 접속
2. 보안 메뉴 클릭
3. "2단계 인증" 활성화
```

**2단계: 앱 비밀번호 생성**
```
1. 보안 페이지에서 "앱 비밀번호" 검색
2. 앱 선택: 메일
3. 기기 선택: 기타 (사용자 지정 이름: StaffSync)
4. 생성 클릭
5. 16자리 비밀번호 복사 (예: abcd efgh ijkl mnop)
```

**3단계: application.properties 설정**
```properties
# ❌ 잘못된 설정 (일반 비밀번호)
spring.mail.username=yourname@gmail.com
spring.mail.password=your-gmail-password

# ✅ 올바른 설정 (앱 비밀번호, 공백 제거)
spring.mail.username=yourname@gmail.com
spring.mail.password=abcdefghijklmnop
```

**4단계: VS Code launch.json 설정**
```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "StaffSync",
      "request": "launch",
      "mainClass": "com.staffSync.StaffSyncApplication",
      "projectName": "backend",
      "env": {
        "MAIL_USERNAME": "yourname@gmail.com",
        "MAIL_PASSWORD": "abcdefghijklmnop"
      }
    }
  ]
}
```

**테스트**:
```bash
# 서버 실행 후 로그 확인
📧 이메일 설정: Gmail SMTP 사용
✅ HTML 이메일 발송 완료: test@gmail.com
```

**결과**: ✅ Gmail SMTP를 통한 이메일 정상 발송

---

## 📅 2025-12-12 트러블슈팅

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

**마지막 업데이트**: 2025-12-16