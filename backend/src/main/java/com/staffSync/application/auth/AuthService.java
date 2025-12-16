package com.staffSync.application.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staffSync.member.domain.Member;
import com.staffSync.member.domain.MemberRepository;
import com.staffSync.application.auth.dto.LoginRequest;
import com.staffSync.application.auth.dto.LoginResponse;
import com.staffSync.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

/**
 * 인증 관련 비즈니스 로직 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 로그인 처리
     * 
     * @param request 로그인 요청 (이메일, 비밀번호)
     * @return 로그인 응답 (JWT 토큰, 사용자 정보)
     */
    public LoginResponse login(LoginRequest request) {
        // 1. 이메일로 회원 조회
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 이메일 인증 여부 확인
        if (!member.isVerified()) {
            throw new IllegalArgumentException("이메일 인증이 완료되지 않았습니다.");
        }

        // 4. JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(member.getEmail(), member.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getEmail());

        // 5. 로그인 응답 반환
        return new LoginResponse(
                accessToken,
                refreshToken,
                member.getEmail(),
                member.getName(),
                member.getRole().name());
    }
}
