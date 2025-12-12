package com.staffSync.member.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staffSync.member.application.dto.RegisterRequest;
import com.staffSync.member.domain.Member;
import com.staffSync.member.domain.MemberRepository;
import com.staffSync.member.infrastructure.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final EmailService emailService;

    // 1. 인증 코드 요청
    public void requestVerification(String email) {
        boolean exists = memberRepository.existsByEmail(email);

        if (exists) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        emailService.sendVerificationCode(email);
    }

    // 인증 코드 검증 (회원가입x, 검증만)
    public boolean verifyCode(String email, String code) {
        return emailService.verifyCode(email, code);
    }

    // 2. 회원가입 (검증 포함)
    @Transactional
    public Long register(RegisterRequest request) {
        // 이메일 인증 코드 확인
        if (!emailService.verifyCode(request.getEmail(), request.getVerificationCode())) {
            throw new IllegalArgumentException("인증 코드가 올바르지 않습니다.");
        }

        // Member 저장
        Member member = new Member(
                request.getEmail(),
                request.getPassword(), // 암호화 필요
                request.getName());

        return memberRepository.save(member).getId();
    }
}
