package com.example.prtcHr.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // 암호화 해야함

    @Column(nullable = false)
    private String name;

    private boolean isVerified = false; // 이메일 인증 여부

    public Member(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
        verifyEmail();
    }

    // 이메일 인증 메서드
    public void verifyEmail() {
        this.isVerified = true; // 인증 코드를 통과했으므로 true
    }
}
