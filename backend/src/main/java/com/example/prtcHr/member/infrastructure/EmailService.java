package com.example.prtcHr.member.infrastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    // 임시 저장소(실무에서는 redis 사용 권장)
    private final Map<String, String> verificationStorage = new HashMap<>();

    // 인증 코드 생성 및 발송
    public void sendVerificationCode(String email) {
        String verificationCode = generateCode();
        verificationStorage.put(email, verificationCode);

        // 개발용 콘솔 이메일 인증
        System.out.println("============================");
        System.out.println("To: " + email);
        System.out.println("Verification Code: " + "[" + verificationCode + "]");
        System.out.println("============================");

        // TODO: JavaMailSender를 통해 실제 메일 발송 로직 추가 가능
    }

    // 코드 검증
    public boolean verifyCode(String email, String code) {
        String savedCode = verificationStorage.get(email);
        return savedCode != null && savedCode.equals(code);
    }

    private String generateCode() {
        return String.valueOf(100000 + new Random().nextInt(900000)); // 6자리 난수 생성
    }
}
