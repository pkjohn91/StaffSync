package com.staffSync.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // 암호화된 비밀번호

    @Column(nullable = false)
    private String name;

    private boolean isVerified = false; // 이메일 인증 여부

    // 사용자 역할
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    public Member(String email, String name, String password, MemberRole role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.isVerified = false; // 초기에는 이메일 미인증 상태
        this.role = role;
        verifyEmail();
    }

    // 이메일 인증 메서드
    public void verifyEmail() {
        this.isVerified = true; // 인증 코드를 통과했으므로 true
    }
}
