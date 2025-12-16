package com.staffSync.member.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.staffSync.member.domain.Member;
import com.staffSync.member.domain.MemberRole;
import com.staffSync.member.domain.MemberRepository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("이메일 인증 요청 - 성공: 중복이 없으면 인증 코드를 생성한다")
    void requestVerification_Success() {
        // given
        String email = "newuser@test.com";
        given(memberRepository.existsByEmail(email)).willReturn(false);

        // when
        memberService.requestVerification(email);

        // then
        verify(memberRepository).existsByEmail(email);
    }

    @Test
    @DisplayName("이메일 인증 요청 - 실패: 이미 가입된 이메일이면 예외가 발생")
    void requestVerification_Fail_Duplicate() {
        // given
        String email = "duplicate@test.com";
        given(memberRepository.existsByEmail(email)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.requestVerification(email))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 가입된 이메일입니다.");
    }

    @Test
    @DisplayName("회원가입 - 성공: 모든 조건이 맞으면 회원을 저장한다")
    void register_Success() {
        // given
        String email = "valid@test.com";
        String name = "홍길동";
        String password = "password123";
        String code = "123456";

        // ✅ 1. 테스트용 인증 코드 직접 설정
        memberService.setVerificationCodeForTest(email, code);

        // ✅ 2. 회원가입 시 중복 체크 - false (중복 아님)
        given(memberRepository.existsByEmail(email)).willReturn(false);

        // ✅ 3. 비밀번호 암호화 Mock 설정
        String encodedPassword = "encodedPassword123";
        given(passwordEncoder.encode(password)).willReturn(encodedPassword);

        // ✅ 4. Member 저장 Mock 설정
        Member savedMember = new Member(email, name, encodedPassword, MemberRole.ADMIN);
        given(memberRepository.save(any(Member.class))).willReturn(savedMember);

        // when
        memberService.register(email, name, password, code);

        // then
        verify(passwordEncoder).encode(password);
        verify(memberRepository, times(1)).existsByEmail(email);
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("회원가입 - 실패: 인증 코드가 틀리면 예외가 발생")
    void register_Fail_InvalidCode() {
        // given
        String email = "invalid@test.com";
        String name = "김철수";
        String password = "password123";
        String correctCode = "123456";
        String wrongCode = "999999";

        // ✅ 올바른 코드 설정
        memberService.setVerificationCodeForTest(email, correctCode);

        // when & then
        assertThatThrownBy(() -> memberService.register(email, name, password, wrongCode))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("인증 코드가 일치하지 않습니다.");

        // 저장 로직은 실행되면 안 됨
        verify(memberRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("회원가입 - 실패: 이미 가입된 이메일이면 예외가 발생")
    void register_Fail_DuplicateEmail() {
        // given
        String email = "duplicate@test.com";
        String name = "이영희";
        String password = "password123";
        String code = "123456";

        // ✅ 1. 인증 코드 설정 (인증은 통과시켜야 중복 체크까지 도달)
        memberService.setVerificationCodeForTest(email, code);

        // ✅ 2. 회원가입 시 중복 체크 - true (중복임)
        given(memberRepository.existsByEmail(email)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.register(email, name, password, code))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 가입된 이메일입니다.");

        verify(memberRepository, times(0)).save(any());
    }
}