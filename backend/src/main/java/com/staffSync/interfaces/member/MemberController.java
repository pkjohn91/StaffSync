package com.staffSync.interfaces.member;

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

    // 1. 이메일 인증 코드 요청 API
    @PostMapping("/send-code")
    public ResponseEntity<String> sendVerificationCode(@RequestParam String email) {
        memberService.requestVerification(email);
        return ResponseEntity.ok("인증 코드가 발송되었습니다. (콘솔 확인)");
    }

    // 인증 코드 검증 API (회원가입X, only 검증O)
    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@RequestParam String email, @RequestParam String code) {
        boolean isVerified = memberService.verifyCode(email, code);

        if (isVerified) {
            return ResponseEntity.ok("인증이 완료되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("인증 코드가 일치하지 않습니다.");
        }
    }

    // 2. 회원가입 API (검증 포함)
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String name = request.get("name");
        String password = request.get("password");
        String code = request.get("verificationCode");

        memberService.register(email, name, password, code);

        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

}
