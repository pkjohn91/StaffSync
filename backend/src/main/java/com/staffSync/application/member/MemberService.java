package com.staffSync.application.member;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staffSync.domain.member.Member;
import com.staffSync.domain.member.MemberRepository;
import com.staffSync.domain.member.MemberRole;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 인증 코드 저장소 (실제로는 Redis 사용 권장)
    private final Map<String, String> verificationCodes = new HashMap<>();

    /**
     * 이메일 인증 코드 발송 요청
     */
    public void requestVerification(String email) {

        if (memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 6자리 랜덤 인증 코드 생성
        String code = String.format("%06d", new Random().nextInt(999999));
        verificationCodes.put(email, code);

        // Mock 이메일 전송 (콘솔 출력용)
        System.out.println("============================");
        System.out.println("이메일 인증 코드 발송");
        System.out.println("수신: " + email);
        System.out.println("코드: " + code);
        System.out.println("============================");
    }

    /**
     * 테스트용: 인증 코드를 직접 설정합니다.
     * 프로덕션에서는 사용하지 않습니다.
     */
    public void setVerificationCodeForTest(String email, String code) {
        verificationCodes.put(email, code);
    }

    // 이메일 인증 코드 검증 (회원가입x, 검증만)
    public boolean verifyCode(String email, String code) {
        String storedCode = verificationCodes.get(email);
        return storedCode != null && storedCode.equals(code);
    }

    // 2. 회원가입 (비밀번호 암호화 적용)
    @Transactional
    public void register(String email, String name, String password, String code) {
        // 1. 인증 코드 검증
        if (!verifyCode(email, code)) {
            throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");
        }

        // 2. 중복 체크
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 3. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 4. 회원 생성
        Member member = new Member(
                email,
                name,
                encodedPassword,
                MemberRole.ADMIN // 기본값: ADMIN
        );
        member.verifyEmail(); // 이메일 인증 완료

        // 5. 저장
        memberRepository.save(member);

        // 6. 인증 코드 삭제
        verificationCodes.remove(email);
    }
}
