package com.staffSync.interfaces.member;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.staffSync.application.member.MemberService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 이메일 인증 코드 발송
     * POST /api/members/send-code?email={email}
     */
    @PostMapping("/send-code")
    public ResponseEntity<Map<String, String>> sendVerificationCode(@RequestParam String email) {
        memberService.requestVerification(email);

        Map<String, String> response = new HashMap<>();
        response.put("message", "인증 코드가 발송되었습니다. (유효시간: 10분)");
        response.put("email: ", email);
        return ResponseEntity.ok(response);
    }

    /**
     * 이메일 인증 코드 검증
     * POST /api/members/verify-code?email={email}&code={code}
     */
    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, Object>> verifyCode(
            @RequestParam String email,
            @RequestParam String code) {

        boolean isvalid = memberService.verifyCode(email, code);

        Map<String, Object> response = new HashMap<>();
        if (isvalid) {
            response.put("success", true);
            response.put("message", "인증이 완료됐습니다.");
        } else {
            response.put("success", false);
            response.put("message", "인증 코드가 일치하지 않거나 만료됐습니다.");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 인증 코드 남은 시간 조회
     * GET /api/members/code-time?email={email}
     */
    public ResponseEntity<Map<String, Object>> getCodeRemainingTime(@RequestParam String email) {
        long remainingTime = memberService.getRemainingTime(email);

        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("remainingTime", remainingTime);
        response.put("isExpired", remainingTime <= 0);

        return ResponseEntity.ok(response);
    }

    // 2. 회원가입 API (검증 포함)
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String name = request.get("name");
        String password = request.get("password");
        String code = request.get("verificationCode");

        memberService.register(email, name, password, code);

        Map<String, String> response = new HashMap<>();
        response.put("message", "회원가입이 완료됐습니다.");
        response.put("email", email);
        return ResponseEntity.ok(response);
    }

}
