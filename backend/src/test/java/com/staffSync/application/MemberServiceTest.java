package com.staffSync.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.staffSync.application.member.MemberService;
import com.staffSync.domain.member.Member;
import com.staffSync.domain.member.MemberRole;
import com.staffSync.infrastructure.mail.EmailService;

import jakarta.mail.MessagingException;

import com.staffSync.domain.member.MemberRepository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Spy
    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("이메일 인증 요청 - 성공: 중복이 없다면 인증 코드를 생성하고 이메일로 발송한다")
    void requestVerification_Success() throws MessagingException {
        // given
        String email = "newuser@test.com";
        given(memberRepository.existsByEmail(email)).willReturn(false);

        // 이메일 발송 Mock 설정
        /**
         * Java Type Error 메시지
         * 문제: doNothing()...sendVerificationCodeHtml(anyString(), anyString())에서 Type
         * 에러 뜸
         * 해결: throws MessagingException 추가
         */
        doNothing().when(emailService).sendVerificationCodeHtml(anyString(), anyString());

        // when
        memberService.requestVerification(email);

        // then
        verify(memberRepository).existsByEmail(email);

        // 이메일 발송 메서드가 호출됐는 지 확인
        verify(emailService, times(1)).sendVerificationCodeHtml(eq(email), anyString());
    }

    @Test
    @DisplayName("이메일 인증 요청 - 실패: 이미 가입된 이메일이면 예외가 발생")
    void requestVerification_Fail_Duplicate() throws MessagingException {
        // given
        String email = "duplicate@test.com";
        given(memberRepository.existsByEmail(email)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.requestVerification(email))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 가입된 이메일입니다.");

        // 이메일이 발송되지 않아야 함
        /**
         * Unhandled exception type MessagingExceptionJava(16777384)
         * 해결: 메서드에 throws MessagingException 추가
         */
        verify(emailService, never()).sendVerificationCodeHtml(anyString(), anyString());
    }

    @Test
    @DisplayName("회원가입 - 성공: 모든 조건이 맞으면 회원을 저장한다")
    void register_Success() {
        // given
        String email = "valid@test.com";
        String name = "홍길동";
        String password = "password123";
        String code = "123456";

        /**
         * @Spy 어노테이션으로 verifyCode() 메서드 대신 Mock(가짜) 처리
         */
        // memberService.setVerificationCodeForTest(email, code);
        doReturn(true).when(memberService).verifyCode(eq(email), eq(code));

        // 2. 회원가입 시 중복 체크 - false (중복 아님)
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

        // memberService.setVerificationCodeForTest(email, correctCode);
        doReturn(false).when(memberService).verifyCode(eq(email), eq(wrongCode));

        // when & then
        assertThatThrownBy(() -> memberService.register(email, name, password, wrongCode))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("인증 코드가 일치하지 않거나 만료됐습니다.");

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
        doReturn(true).when(memberService).verifyCode(email, code);
        // memberService.setVerificationCodeForTest(email, code);

        // ✅ 2. 회원가입 시 중복 체크 - true (중복임)
        given(memberRepository.existsByEmail(email)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.register(email, name, password, code))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 가입된 이메일입니다.");

        verify(memberRepository, times(0)).save(any());
    }
}