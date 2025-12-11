package com.example.prtcHr.member.application;

import com.example.prtcHr.member.domain.Member;
import com.example.prtcHr.member.domain.MemberRepository;
import com.example.prtcHr.member.infrastructure.EmailService;
import com.example.prtcHr.member.interfaces.dto.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class) // 가짜 객체(Mock)를 사용하겠다고 선언
class MemberServiceTest {

    @Mock // 가짜 리포지토리 (DB 연결 X)
    private MemberRepository memberRepository;

    @Mock // 가짜 이메일 서비스 (메일 발송 X)
    private EmailService emailService;

    @InjectMocks // 가짜들을 주입받는 진짜 서비스
    private MemberService memberService;

    @Test
    @DisplayName("이메일 인증 요청 - 성공: 중복이 없으면 이메일을 보낸다")
    void requestVerification_Success() {
        // 1. given (준비)
        String email = "newuser@test.com";

        // ★ 핵심: 가짜 리포지토리에게 "이 이메일은 DB에 없어(false)!"라고 강제로 정해줍니다.
        // 만약 여기서 true를 리턴하면 "이미 가입된 이메일" 에러가 터져서 테스트가 실패합니다.
        given(memberRepository.existsByEmail(email)).willReturn(false);

        // 2. when (실행)
        // 에러가 발생하지 않아야 정상입니다.
        memberService.requestVerification(email);

        // 3. then (검증)
        // 리포지토리를 한 번 조회했는지 확인
        verify(memberRepository).existsByEmail(email);
        // 이메일 발송 메서드가 실행되었는지 확인
        verify(emailService).sendVerificationCode(email);
    }

    // 실패 케이스 테스트: 에러가 터져야 성공(초록불)
    @Test
    @DisplayName("이메일 인증 요청 - 실패: 이미 가입된 이메일이면 예외가 터져야 한다")
    void requestVerification_Fail_Duplicate() {
        // given
        String email = "duplicate@test.com";
        // 가짜 리포지토리에게 "이 이메일은 이미 있어(true)!"라고 설정
        given(memberRepository.existsByEmail(email)).willReturn(true);

        // when & then
        // 예외가 발생하는지 확인 (발생해야 초록불)
        assertThatThrownBy(() -> memberService.requestVerification(email))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 가입된 이메일입니다.");
    }

    @Test
    @DisplayName("회원가입 - 성공: 인증 코드가 맞다면 회원을 저장한다")
    void register_Success() {
        // given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("validationCode@test.com");
        request.setVerificationCode("123456");
        request.setPassword("password123");
        request.setName("박종훈");

        /*
         * [핵심 1] 가짜 EmailService에게 "무조건 통과(true) 시켜줘!"라고 교육시킵니다.
         * any()를 쓰면 "어떤 이메일이나 코드가 들어와도 무조건 true"라고 설정하는 것입니다.
         * 
         * 아래의 코드가 없으면 Mockito는 boolean의 기본값인 false를 리턴합니다.
         * 그러면 서비스 로직의 if (!verifyCode...)에 걸려서 에러를 뱉고 종료되어 버립니다.
         * 그래서 **"무조건 true를 리턴해라"**고 강제로 설정한 것입니다.
         */
        given(emailService.verifyCode(any(), any())).willReturn(true);

        /*
         * [핵심 2] 가짜 Repository에게 "저장하면, ID가 1인 멤버를 돌려줘"라고 교육시킵니다.
         * 서비스 코드 마지막에 .getId()를 호출하므로, ID가 있는 멤버를 리턴해야 NPE 에러가 안 납니다.
         */
        Member savedMember = new Member(request.getEmail(), request.getPassword(), request.getName());
        /*
         * (테스트용으로 강제로 ID 주입 - 실제로는 DB가 해줌)
         * 리플렉션 없이 간단히 하려면 Mockito answer를 써야하지만,
         * 여기선 save가 호출되었는지만 볼 것이므로 리턴값은 중요하지 않을 수 있으나
         * 서비스 로직에서 return memberRepository.save(member).getId()가 있다면 아래 줄이 필요.
         *
         * 필요한 이유는 Mock의 값은 false기 때문이다.
         * 따라서 MemberService 로직을 타고 원하는 값을 얻기 위해 any()를 사용했다.
         */
        given(memberRepository.save(any(Member.class))).willReturn(savedMember);

        // 2. When (실행)
        memberService.register(request);

        // 3. Then (검증)
        // 이제 중간에 죽지 않고 save()까지 잘 도달했는지 확인합니다.
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("회원가입 - 실패: 인증 코드가 틀리면 예외가 발생")
    void register_Fail_InvalidCode() {
        // given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("invalid@test.com");
        request.setVerificationCode("999999"); // 틀린 인증 코드

        // 인증 코드가 맞다면 -> 아니(false)
        given(emailService.verifyCode(request.getEmail(), request.getVerificationCode())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> memberService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("인증 코드가 올바르지 않습니다.");

        // 저장 로직은 실행되면 안됌
        verify(memberRepository, times(0)).save(any());
    }
}