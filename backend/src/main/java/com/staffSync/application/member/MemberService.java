package com.staffSync.application.member;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staffSync.domain.member.Member;
import com.staffSync.domain.member.MemberRepository;
import com.staffSync.domain.member.MemberRole;
import com.staffSync.infrastructure.mail.EmailService;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // 인증 코드 저장소 (실제로는 Redis 사용 권장)
    private final Map<String, String> verificationCodes = new HashMap<>();

    // 인증 코드 만료 시간 저장소
    private final Map<String, LocalDateTime> codeExpirationTimes = new HashMap<>();

    // 인증 코드 유효 시간 (10minutes)
    private static final int CODE_EXPIRATION_MINUTES = 10;

    /**
     * 이메일 인증 코드 발송 요청
     * 
     * @param email 수신자 이메일
     */
    public void requestVerification(String email) {

        // 1. 중복 이메일 체크
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 2. 6자리 랜덤 인증 코드 생성
        String code = String.format("%06d", new Random().nextInt(1000000));

        // 3. 코드 및 만료 시간 저장
        verificationCodes.put(email, code);
        codeExpirationTimes.put(email, LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES));

        // 4. 이메일 전송
        try {
            emailService.sendVerificationCodeHtml(email, code);
            System.out.println("============================");
            System.out.println("[개발용] 인증 코드");
            System.out.println("수신: " + email);
            System.out.println("코드: " + code);
            System.out.println("만료: " + CODE_EXPIRATION_MINUTES + "분 후");
            System.out.println("============================");
            System.out.println("이메일 발송 완료 성공: " + email);
        } catch (MessagingException e) {
            // 이메일 발송 실패 (개발용, 실제 환경에서는 log를 남기는게 좋음)
            System.out.println("이메일 발송 실패: " + e.getMessage());
            System.out.println("============================");
            System.out.println("[백업] 콘솔 출력 인증 코드");
            System.out.println("수신: " + email);
            System.out.println("코드: " + code);
            System.out.println("만료: " + CODE_EXPIRATION_MINUTES + "분 후");
            System.out.println("============================");

            // 발송 실패 시 사용자에게 알릴 지 결정
            // throw new RuntimeException("이메일 발송에 실패했습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    // 이메일 인증 코드 검증 (회원가입x, 검증만)
    public boolean verifyCode(String email, String code) {
        // 1. 저장된 코드 확인
        String storedCode = verificationCodes.get(email);
        if (storedCode == null) {
            return false;
        }

        // 2. 만료 시간 확인
        LocalDateTime expirationTime = codeExpirationTimes.get(email);
        if (expirationTime == null || LocalDateTime.now().isAfter(expirationTime)) {
            // 만료된 코드 삭제
            verificationCodes.remove(email);
            codeExpirationTimes.remove(email);
            return false; // 인증 코드 만료
        }

        // 3. 코드 일치 여부 확인
        return storedCode.equals(code);
    }

    /**
     * 인증 코드 남은 시간 조회 (초 단위)
     * 
     * @param email 이메일
     * @return 남은 시간 (초), 만료됐거나 없으면 0
     */
    public long getRemainingTime(String email) {
        LocalDateTime expirationTime = codeExpirationTimes.get(email);
        if (expirationTime == null) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(expirationTime)) {
            return 0; // 만료됌
        }

        return java.time.Duration.between(now, expirationTime).getSeconds();
    }

    // 2. 회원가입 (비밀번호 암호화 적용)
    @Transactional
    public void register(String email, String name, String password, String code) {
        // 1. 인증 코드 검증
        if (!verifyCode(email, code)) {
            throw new IllegalArgumentException("인증 코드가 일치하지 않거나 만료됐습니다.");
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

        // 6. 인증 코드 및 만료 시간 삭제
        verificationCodes.remove(email);
        codeExpirationTimes.remove(email);
    }
}
